# Development tool - standard commands plugin
#
# Copyright (C) 2014-2015 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
"""Devtool standard plugins"""

import os
import sys
import re
import shutil
import tempfile
import logging
import argparse
import scriptutils
import errno
from devtool import exec_build_env_command, setup_tinfoil, DevtoolError
from devtool import parse_recipe

logger = logging.getLogger('devtool')


def add(args, config, basepath, workspace):
    """Entry point for the devtool 'add' subcommand"""
    import bb
    import oe.recipeutils

    if args.recipename in workspace:
        raise DevtoolError("recipe %s is already in your workspace" %
                            args.recipename)

    reason = oe.recipeutils.validate_pn(args.recipename)
    if reason:
        raise DevtoolError(reason)

    srctree = os.path.abspath(args.srctree)
    if os.path.exists(srctree):
        if args.fetch:
            if not os.path.isdir(srctree):
                raise DevtoolError("Cannot fetch into source tree path %s as "
                                   "it exists and is not a directory" %
                                   srctree)
            elif os.listdir(srctree):
                raise DevtoolError("Cannot fetch into source tree path %s as "
                                   "it already exists and is non-empty" %
                                   srctree)
    elif not args.fetch:
        raise DevtoolError("Specified source tree %s could not be found" %
                           srctree)

    appendpath = os.path.join(config.workspace_path, 'appends')
    if not os.path.exists(appendpath):
        os.makedirs(appendpath)

    recipedir = os.path.join(config.workspace_path, 'recipes', args.recipename)
    bb.utils.mkdirhier(recipedir)
    rfv = None
    if args.version:
        if '_' in args.version or ' ' in args.version:
            raise DevtoolError('Invalid version string "%s"' % args.version)
        rfv = args.version
    if args.fetch:
        if args.fetch.startswith('git://'):
            rfv = 'git'
        elif args.fetch.startswith('svn://'):
            rfv = 'svn'
        elif args.fetch.startswith('hg://'):
            rfv = 'hg'
    if rfv:
        bp = "%s_%s" % (args.recipename, rfv)
    else:
        bp = args.recipename
    recipefile = os.path.join(recipedir, "%s.bb" % bp)
    if sys.stdout.isatty():
        color = 'always'
    else:
        color = args.color
    extracmdopts = ''
    if args.fetch:
        source = args.fetch
        extracmdopts = '-x %s' % srctree
    else:
        source = srctree
    if args.version:
        extracmdopts += ' -V %s' % args.version
    try:
        stdout, _ = exec_build_env_command(config.init_path, basepath, 'recipetool --color=%s create -o %s "%s" %s' % (color, recipefile, source, extracmdopts))
        logger.info('Recipe %s has been automatically created; further editing may be required to make it fully functional' % recipefile)
    except bb.process.ExecutionError as e:
        raise DevtoolError('Command \'%s\' failed:\n%s' % (e.command, e.stdout))

    _add_md5(config, args.recipename, recipefile)

    initial_rev = None
    if os.path.exists(os.path.join(srctree, '.git')):
        (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srctree)
        initial_rev = stdout.rstrip()

    appendfile = os.path.join(appendpath, '%s.bbappend' % bp)
    with open(appendfile, 'w') as f:
        f.write('inherit externalsrc\n')
        f.write('EXTERNALSRC = "%s"\n' % srctree)
        if args.same_dir:
            f.write('EXTERNALSRC_BUILD = "%s"\n' % srctree)
        if initial_rev:
            f.write('\n# initial_rev: %s\n' % initial_rev)

    _add_md5(config, args.recipename, appendfile)

    return 0


def _check_compatible_recipe(pn, d):
    """Check if the recipe is supported by devtool"""
    if pn == 'perf':
        raise DevtoolError("The perf recipe does not actually check out "
                           "source and thus cannot be supported by this tool")

    if pn in ['kernel-devsrc', 'package-index'] or pn.startswith('gcc-source'):
        raise DevtoolError("The %s recipe is not supported by this tool" % pn)

    if bb.data.inherits_class('image', d):
        raise DevtoolError("The %s recipe is an image, and therefore is not "
                           "supported by this tool" % pn)

    if bb.data.inherits_class('populate_sdk', d):
        raise DevtoolError("The %s recipe is an SDK, and therefore is not "
                           "supported by this tool" % pn)

    if bb.data.inherits_class('packagegroup', d):
        raise DevtoolError("The %s recipe is a packagegroup, and therefore is "
                           "not supported by this tool" % pn)

    if bb.data.inherits_class('meta', d):
        raise DevtoolError("The %s recipe is a meta-recipe, and therefore is "
                           "not supported by this tool" % pn)

    if bb.data.inherits_class('externalsrc', d) and d.getVar('EXTERNALSRC', True):
        raise DevtoolError("externalsrc is currently enabled for the %s "
                           "recipe. This prevents the normal do_patch task "
                           "from working. You will need to disable this "
                           "first." % pn)

def _ls_tree(directory):
    """Recursive listing of files in a directory"""
    ret = []
    for root, dirs, files in os.walk(directory):
        ret.extend([os.path.relpath(os.path.join(root, fname), directory) for
                    fname in files])
    return ret


def extract(args, config, basepath, workspace):
    """Entry point for the devtool 'extract' subcommand"""
    import bb

    tinfoil = _prep_extract_operation(config, basepath, args.recipename)

    rd = parse_recipe(config, tinfoil, args.recipename, True)
    if not rd:
        return 1

    srctree = os.path.abspath(args.srctree)
    initial_rev = _extract_source(srctree, args.keep_temp, args.branch, rd)
    logger.info('Source tree extracted to %s' % srctree)

    if initial_rev:
        return 0
    else:
        return 1

class BbTaskExecutor(object):
    """Class for executing bitbake tasks for a recipe

    FIXME: This is very awkward. Unfortunately it's not currently easy to
    properly execute tasks outside of bitbake itself, until then this has to
    suffice if we are to handle e.g. linux-yocto's extra tasks
    """

    def __init__(self, rdata):
        self.rdata = rdata
        self.executed = []

    def exec_func(self, func, report):
        """Run bitbake task function"""
        if not func in self.executed:
            deps = self.rdata.getVarFlag(func, 'deps')
            if deps:
                for taskdepfunc in deps:
                    self.exec_func(taskdepfunc, True)
            if report:
                logger.info('Executing %s...' % func)
            fn = self.rdata.getVar('FILE', True)
            localdata = bb.build._task_data(fn, func, self.rdata)
            bb.build.exec_func(func, localdata)
            self.executed.append(func)


def _prep_extract_operation(config, basepath, recipename):
    """HACK: Ugly workaround for making sure that requirements are met when
       trying to extract a package. Returns the tinfoil instance to be used."""
    tinfoil = setup_tinfoil()
    rd = parse_recipe(config, tinfoil, recipename, True)

    if bb.data.inherits_class('kernel-yocto', rd):
        tinfoil.shutdown()
        try:
            stdout, _ = exec_build_env_command(config.init_path, basepath,
                                               'bitbake kern-tools-native')
            tinfoil = setup_tinfoil()
        except bb.process.ExecutionError as err:
            raise DevtoolError("Failed to build kern-tools-native:\n%s" %
                               err.stdout)
    return tinfoil


def _extract_source(srctree, keep_temp, devbranch, d):
    """Extract sources of a recipe"""
    import bb.event
    import oe.recipeutils

    def eventfilter(name, handler, event, d):
        """Bitbake event filter for devtool extract operation"""
        if name == 'base_eventhandler':
            return True
        else:
            return False

    if hasattr(bb.event, 'set_eventfilter'):
        bb.event.set_eventfilter(eventfilter)

    pn = d.getVar('PN', True)

    _check_compatible_recipe(pn, d)

    if os.path.exists(srctree):
        if not os.path.isdir(srctree):
            raise DevtoolError("output path %s exists and is not a directory" %
                               srctree)
        elif os.listdir(srctree):
            raise DevtoolError("output path %s already exists and is "
                               "non-empty" % srctree)

    # Prepare for shutil.move later on
    bb.utils.mkdirhier(srctree)
    os.rmdir(srctree)

    # We don't want notes to be printed, they are too verbose
    origlevel = bb.logger.getEffectiveLevel()
    if logger.getEffectiveLevel() > logging.DEBUG:
        bb.logger.setLevel(logging.WARNING)

    initial_rev = None
    tempdir = tempfile.mkdtemp(prefix='devtool')
    try:
        crd = d.createCopy()
        # Make a subdir so we guard against WORKDIR==S
        workdir = os.path.join(tempdir, 'workdir')
        crd.setVar('WORKDIR', workdir)
        crd.setVar('T', os.path.join(tempdir, 'temp'))
        if not crd.getVar('S', True).startswith(workdir):
            # Usually a shared workdir recipe (kernel, gcc)
            # Try to set a reasonable default
            if bb.data.inherits_class('kernel', d):
                crd.setVar('S', '${WORKDIR}/source')
            else:
                crd.setVar('S', '${WORKDIR}/${BP}')
        if bb.data.inherits_class('kernel', d):
            # We don't want to move the source to STAGING_KERNEL_DIR here
            crd.setVar('STAGING_KERNEL_DIR', '${S}')

        task_executor = BbTaskExecutor(crd)

        crd.setVar('EXTERNALSRC_forcevariable', '')

        logger.info('Fetching %s...' % pn)
        task_executor.exec_func('do_fetch', False)
        logger.info('Unpacking...')
        task_executor.exec_func('do_unpack', False)
        if bb.data.inherits_class('kernel-yocto', d):
            # Extra step for kernel to populate the source directory
            logger.info('Doing kernel checkout...')
            task_executor.exec_func('do_kernel_checkout', False)
        srcsubdir = crd.getVar('S', True)
        if srcsubdir == workdir:
            # Find non-patch sources that were "unpacked" to srctree directory
            recipe_patches = [os.path.basename(patch) for patch in
                              oe.recipeutils.get_recipe_patches(crd)]
            src_files = [fname for fname in _ls_tree(workdir) if
                         os.path.basename(fname) not in recipe_patches]
            # Force separate S so that patch files can be left out from srctree
            srcsubdir = tempfile.mkdtemp(dir=workdir)
            crd.setVar('S', srcsubdir)
            # Move source files to S
            for path in src_files:
                tgt_dir = os.path.join(srcsubdir, os.path.dirname(path))
                bb.utils.mkdirhier(tgt_dir)
                shutil.move(os.path.join(workdir, path), tgt_dir)
        elif os.path.dirname(srcsubdir) != workdir:
            # Handle if S is set to a subdirectory of the source
            srcsubdir = os.path.join(workdir, os.path.relpath(srcsubdir, workdir).split(os.sep)[0])

        scriptutils.git_convert_standalone_clone(srcsubdir)

        patchdir = os.path.join(srcsubdir, 'patches')
        haspatches = False
        if os.path.exists(patchdir):
            if os.listdir(patchdir):
                haspatches = True
            else:
                os.rmdir(patchdir)

        if not os.listdir(srcsubdir):
            raise DevtoolError("no source unpacked to S, perhaps the %s "
                               "recipe doesn't use any source?" % pn)

        if not os.path.exists(os.path.join(srcsubdir, '.git')):
            bb.process.run('git init', cwd=srcsubdir)
            bb.process.run('git add .', cwd=srcsubdir)
            bb.process.run('git commit -q -m "Initial commit from upstream at version %s"' % crd.getVar('PV', True), cwd=srcsubdir)

        (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srcsubdir)
        initial_rev = stdout.rstrip()

        bb.process.run('git checkout -b %s' % devbranch, cwd=srcsubdir)
        bb.process.run('git tag -f devtool-base', cwd=srcsubdir)
        crd.setVar('PATCHTOOL', 'git')

        logger.info('Patching...')
        task_executor.exec_func('do_patch', False)

        bb.process.run('git tag -f devtool-patched', cwd=srcsubdir)

        if os.path.exists(patchdir):
            shutil.rmtree(patchdir)
            if haspatches:
                bb.process.run('git checkout patches', cwd=srcsubdir)

        shutil.move(srcsubdir, srctree)
    finally:
        bb.logger.setLevel(origlevel)

        if keep_temp:
            logger.info('Preserving temporary directory %s' % tempdir)
        else:
            shutil.rmtree(tempdir)
    return initial_rev

def _add_md5(config, recipename, filename):
    """Record checksum of a file (or recursively for a directory) to the md5-file of the workspace"""
    import bb.utils

    def addfile(fn):
        md5 = bb.utils.md5_file(fn)
        with open(os.path.join(config.workspace_path, '.devtool_md5'), 'a') as f:
            f.write('%s|%s|%s\n' % (recipename, os.path.relpath(fn, config.workspace_path), md5))

    if os.path.isdir(filename):
        for root, _, files in os.walk(os.path.dirname(filename)):
            for f in files:
                addfile(os.path.join(root, f))
    else:
        addfile(filename)

def _check_preserve(config, recipename):
    """Check if a file was manually changed and needs to be saved in 'attic'
       directory"""
    import bb.utils
    origfile = os.path.join(config.workspace_path, '.devtool_md5')
    newfile = os.path.join(config.workspace_path, '.devtool_md5_new')
    preservepath = os.path.join(config.workspace_path, 'attic')
    with open(origfile, 'r') as f:
        with open(newfile, 'w') as tf:
            for line in f.readlines():
                splitline = line.rstrip().split('|')
                if splitline[0] == recipename:
                    removefile = os.path.join(config.workspace_path, splitline[1])
                    try:
                        md5 = bb.utils.md5_file(removefile)
                    except IOError as err:
                        if err.errno == 2:
                            # File no longer exists, skip it
                            continue
                        else:
                            raise
                    if splitline[2] != md5:
                        bb.utils.mkdirhier(preservepath)
                        preservefile = os.path.basename(removefile)
                        logger.warn('File %s modified since it was written, preserving in %s' % (preservefile, preservepath))
                        shutil.move(removefile, os.path.join(preservepath, preservefile))
                    else:
                        os.remove(removefile)
                else:
                    tf.write(line)
    os.rename(newfile, origfile)

def modify(args, config, basepath, workspace):
    """Entry point for the devtool 'modify' subcommand"""
    import bb
    import oe.recipeutils

    if args.recipename in workspace:
        raise DevtoolError("recipe %s is already in your workspace" %
                           args.recipename)

    if not args.extract and not os.path.isdir(args.srctree):
        raise DevtoolError("directory %s does not exist or not a directory "
                           "(specify -x to extract source from recipe)" %
                           args.srctree)
    if args.extract:
        tinfoil = _prep_extract_operation(config, basepath, args.recipename)
    else:
        tinfoil = setup_tinfoil()

    rd = parse_recipe(config, tinfoil, args.recipename, True)
    if not rd:
        return 1
    recipefile = rd.getVar('FILE', True)
    appendname = os.path.splitext(os.path.basename(recipefile))[0]
    if args.wildcard:
        appendname = re.sub(r'_.*', '_%', appendname)
    appendpath = os.path.join(config.workspace_path, 'appends')
    appendfile = os.path.join(appendpath, appendname + '.bbappend')
    if os.path.exists(appendfile):
        raise DevtoolError("Another variant of recipe %s is already in your "
                           "workspace (only one variant of a recipe can "
                           "currently be worked on at once)"
                           % args.recipename)

    _check_compatible_recipe(args.recipename, rd)

    initial_rev = None
    commits = []
    srctree = os.path.abspath(args.srctree)
    if args.extract:
        initial_rev = _extract_source(args.srctree, False, args.branch, rd)
        if not initial_rev:
            return 1
        logger.info('Source tree extracted to %s' % srctree)
        # Get list of commits since this revision
        (stdout, _) = bb.process.run('git rev-list --reverse %s..HEAD' % initial_rev, cwd=args.srctree)
        commits = stdout.split()
    else:
        if os.path.exists(os.path.join(args.srctree, '.git')):
            # Check if it's a tree previously extracted by us
            try:
                (stdout, _) = bb.process.run('git branch --contains devtool-base', cwd=args.srctree)
            except bb.process.ExecutionError:
                stdout = ''
            for line in stdout.splitlines():
                if line.startswith('*'):
                    (stdout, _) = bb.process.run('git rev-parse devtool-base', cwd=args.srctree)
                    initial_rev = stdout.rstrip()
            if not initial_rev:
                # Otherwise, just grab the head revision
                (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=args.srctree)
                initial_rev = stdout.rstrip()

    # Check that recipe isn't using a shared workdir
    s = os.path.abspath(rd.getVar('S', True))
    workdir = os.path.abspath(rd.getVar('WORKDIR', True))
    if s.startswith(workdir) and s != workdir and os.path.dirname(s) != workdir:
        # Handle if S is set to a subdirectory of the source
        srcsubdir = os.path.relpath(s, workdir).split(os.sep, 1)[1]
        srctree = os.path.join(srctree, srcsubdir)

    if not os.path.exists(appendpath):
        os.makedirs(appendpath)
    with open(appendfile, 'w') as f:
        f.write('FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n\n')
        f.write('inherit externalsrc\n')
        f.write('# NOTE: We use pn- overrides here to avoid affecting multiple variants in the case where the recipe uses BBCLASSEXTEND\n')
        f.write('EXTERNALSRC_pn-%s = "%s"\n' % (args.recipename, srctree))

        b_is_s = True
        if args.no_same_dir:
            logger.info('using separate build directory since --no-same-dir specified')
            b_is_s = False
        elif args.same_dir:
            logger.info('using source tree as build directory since --same-dir specified')
        elif bb.data.inherits_class('autotools-brokensep', rd):
            logger.info('using source tree as build directory since original recipe inherits autotools-brokensep')
        elif rd.getVar('B', True) == s:
            logger.info('using source tree as build directory since that is the default for this recipe')
        else:
            b_is_s = False
        if b_is_s:
            f.write('EXTERNALSRC_BUILD_pn-%s = "%s"\n' % (args.recipename, srctree))

        if bb.data.inherits_class('kernel', rd):
            f.write('SRCTREECOVEREDTASKS = "do_validate_branches do_kernel_checkout do_fetch do_unpack"\n')
        if initial_rev:
            f.write('\n# initial_rev: %s\n' % initial_rev)
            for commit in commits:
                f.write('# commit: %s\n' % commit)

    _add_md5(config, args.recipename, appendfile)

    logger.info('Recipe %s now set up to build from %s' % (args.recipename, srctree))

    return 0

def _get_patchset_revs(args, srctree, recipe_path):
    """Get initial and update rev of a recipe. These are the start point of the
    whole patchset and start point for the patches to be re-generated/updated.
    """
    import bb

    if args.initial_rev:
        return args.initial_rev, args.initial_rev

    # Parse initial rev from recipe
    commits = []
    initial_rev = None
    with open(recipe_path, 'r') as f:
        for line in f:
            if line.startswith('# initial_rev:'):
                initial_rev = line.split(':')[-1].strip()
            elif line.startswith('# commit:'):
                commits.append(line.split(':')[-1].strip())

    update_rev = initial_rev
    if initial_rev:
        # Find first actually changed revision
        stdout, _ = bb.process.run('git rev-list --reverse %s..HEAD' %
                                   initial_rev, cwd=srctree)
        newcommits = stdout.split()
        for i in xrange(min(len(commits), len(newcommits))):
            if newcommits[i] == commits[i]:
                update_rev = commits[i]

    return initial_rev, update_rev

def _remove_patch_entries(srcuri, patchlist):
    """Remove patch entries from SRC_URI"""
    remaining = patchlist[:]
    entries = []
    for patch in patchlist:
        patchfile = os.path.basename(patch)
        for i in xrange(len(srcuri)):
            if srcuri[i].startswith('file://') and os.path.basename(srcuri[i].split(';')[0]) == patchfile:
                entries.append(srcuri[i])
                remaining.remove(patch)
                srcuri.pop(i)
                break
    return entries, remaining

def _remove_patch_files(args, patches, destpath):
    """Unlink existing patch files"""
    for patchfile in patches:
        if args.append:
            if not destpath:
                raise Exception('destpath should be set here')
            patchfile = os.path.join(destpath, os.path.basename(patchfile))

        if os.path.exists(patchfile):
            logger.info('Removing patch %s' % patchfile)
            # FIXME "git rm" here would be nice if the file in question is
            #       tracked
            # FIXME there's a chance that this file is referred to by
            #       another recipe, in which case deleting wouldn't be the
            #       right thing to do
            os.remove(patchfile)
            # Remove directory if empty
            try:
                os.rmdir(os.path.dirname(patchfile))
            except OSError as ose:
                if ose.errno != errno.ENOTEMPTY:
                    raise

def _update_recipe_srcrev(args, srctree, rd, config_data):
    """Implement the 'srcrev' mode of update-recipe"""
    import bb
    import oe.recipeutils
    from oe.patch import GitApplyTree

    recipefile = rd.getVar('FILE', True)
    logger.info('Updating SRCREV in recipe %s' % os.path.basename(recipefile))

    # Get HEAD revision
    try:
        stdout, _ = bb.process.run('git rev-parse HEAD', cwd=srctree)
    except bb.process.ExecutionError as err:
        raise DevtoolError('Failed to get HEAD revision in %s: %s' %
                           (srctree, err))
    srcrev = stdout.strip()
    if len(srcrev) != 40:
        raise DevtoolError('Invalid hash returned by git: %s' % stdout)

    destpath = None
    removepatches = []
    patchfields = {}
    patchfields['SRCREV'] = srcrev
    orig_src_uri = rd.getVar('SRC_URI', False) or ''
    if not args.no_remove:
        # Find list of existing patches in recipe file
        existing_patches = oe.recipeutils.get_recipe_patches(rd)

        old_srcrev = (rd.getVar('SRCREV', False) or '')
        tempdir = tempfile.mkdtemp(prefix='devtool')
        try:
            GitApplyTree.extractPatches(srctree, old_srcrev, tempdir)
            newpatches = os.listdir(tempdir)
            for patch in existing_patches:
                patchfile = os.path.basename(patch)
                if patchfile in newpatches:
                    removepatches.append(patch)
        finally:
            shutil.rmtree(tempdir)

        if removepatches:
            srcuri = orig_src_uri.split()
            removedentries, _ = _remove_patch_entries(srcuri, removepatches)
            if removedentries:
                patchfields['SRC_URI'] = ' '.join(srcuri)

    if args.append:
        _, destpath = oe.recipeutils.bbappend_recipe(
                rd, args.append, None, wildcardver=args.wildcard_version,
                extralines=patchfields)
    else:
        oe.recipeutils.patch_recipe(rd, recipefile, patchfields)

    if not 'git://' in orig_src_uri:
        logger.info('You will need to update SRC_URI within the recipe to '
                    'point to a git repository where you have pushed your '
                    'changes')

    _remove_patch_files(args, removepatches, destpath)

def _update_recipe_patch(args, config, srctree, rd, config_data):
    """Implement the 'patch' mode of update-recipe"""
    import bb
    import oe.recipeutils
    from oe.patch import GitApplyTree

    recipefile = rd.getVar('FILE', True)
    append = os.path.join(config.workspace_path, 'appends', '%s.bbappend' %
                          os.path.splitext(os.path.basename(recipefile))[0])
    if not os.path.exists(append):
        raise DevtoolError('unable to find workspace bbappend for recipe %s' %
                           args.recipename)

    initial_rev, update_rev = _get_patchset_revs(args, srctree, append)
    if not initial_rev:
        raise DevtoolError('Unable to find initial revision - please specify '
                           'it with --initial-rev')

    # Find list of existing patches in recipe file
    existing_patches = oe.recipeutils.get_recipe_patches(rd)

    removepatches = []
    seqpatch_re = re.compile('^([0-9]{4}-)?(.+)')
    if not args.no_remove:
        # Get all patches from source tree and check if any should be removed
        tempdir = tempfile.mkdtemp(prefix='devtool')
        try:
            GitApplyTree.extractPatches(srctree, initial_rev, tempdir)
            # Strip numbering from patch names. If it's a git sequence named
            # patch, the numbers might not match up since we are starting from
            # a different revision This does assume that people are using
            # unique shortlog values, but they ought to be anyway...
            newpatches = [seqpatch_re.match(fname).group(2) for fname in
                          os.listdir(tempdir)]
            for patch in existing_patches:
                basename = seqpatch_re.match(
                                os.path.basename(patch)).group(2)
                if basename not in newpatches:
                    removepatches.append(patch)
        finally:
            shutil.rmtree(tempdir)

    # Get updated patches from source tree
    tempdir = tempfile.mkdtemp(prefix='devtool')
    try:
        GitApplyTree.extractPatches(srctree, update_rev, tempdir)

        # Match up and replace existing patches with corresponding new patches
        updatepatches = False
        updaterecipe = False
        destpath = None
        newpatches = os.listdir(tempdir)
        if args.append:
            patchfiles = {}
            for patch in existing_patches:
                patchfile = os.path.basename(patch)
                if patchfile in newpatches:
                    patchfiles[os.path.join(tempdir, patchfile)] = patchfile
                    newpatches.remove(patchfile)
            for patchfile in newpatches:
                patchfiles[os.path.join(tempdir, patchfile)] = None

            if patchfiles or removepatches:
                removevalues = None
                if removepatches:
                    srcuri = (rd.getVar('SRC_URI', False) or '').split()
                    removedentries, remaining = _remove_patch_entries(
                                                    srcuri, removepatches)
                    if removedentries or remaining:
                        remaining = ['file://' + os.path.basename(item) for
                                     item in remaining]
                        removevalues = {'SRC_URI': removedentries + remaining}
                _, destpath = oe.recipeutils.bbappend_recipe(
                                rd, args.append, patchfiles,
                                removevalues=removevalues)
            else:
                logger.info('No patches needed updating')
        else:
            for patch in existing_patches:
                patchfile = os.path.basename(patch)
                if patchfile in newpatches:
                    logger.info('Updating patch %s' % patchfile)
                    shutil.move(os.path.join(tempdir, patchfile), patch)
                    newpatches.remove(patchfile)
                    updatepatches = True
            srcuri = (rd.getVar('SRC_URI', False) or '').split()
            if newpatches:
                # Add any patches left over
                patchdir = os.path.join(os.path.dirname(recipefile),
                                        rd.getVar('BPN', True))
                bb.utils.mkdirhier(patchdir)
                for patchfile in newpatches:
                    logger.info('Adding new patch %s' % patchfile)
                    shutil.move(os.path.join(tempdir, patchfile),
                                os.path.join(patchdir, patchfile))
                    srcuri.append('file://%s' % patchfile)
                    updaterecipe = True
            if removepatches:
                removedentries, _ = _remove_patch_entries(srcuri, removepatches)
                if removedentries:
                    updaterecipe = True
            if updaterecipe:
                logger.info('Updating recipe %s' % os.path.basename(recipefile))
                oe.recipeutils.patch_recipe(rd, recipefile,
                                            {'SRC_URI': ' '.join(srcuri)})
            elif not updatepatches:
                # Neither patches nor recipe were updated
                logger.info('No patches need updating')
    finally:
        shutil.rmtree(tempdir)

    _remove_patch_files(args, removepatches, destpath)

def _guess_recipe_update_mode(srctree, rdata):
    """Guess the recipe update mode to use"""
    src_uri = (rdata.getVar('SRC_URI', False) or '').split()
    git_uris = [uri for uri in src_uri if uri.startswith('git://')]
    if not git_uris:
        return 'patch'
    # Just use the first URI for now
    uri = git_uris[0]
    # Check remote branch
    params = bb.fetch.decodeurl(uri)[5]
    upstr_branch = params['branch'] if 'branch' in params else 'master'
    # Check if current branch HEAD is found in upstream branch
    stdout, _ = bb.process.run('git rev-parse HEAD', cwd=srctree)
    head_rev = stdout.rstrip()
    stdout, _ = bb.process.run('git branch -r --contains %s' % head_rev,
                               cwd=srctree)
    remote_brs = [branch.strip() for branch in stdout.splitlines()]
    if 'origin/' + upstr_branch in remote_brs:
        return 'srcrev'

    return 'patch'

def update_recipe(args, config, basepath, workspace):
    """Entry point for the devtool 'update-recipe' subcommand"""
    if not args.recipename in workspace:
        raise DevtoolError("no recipe named %s in your workspace" %
                           args.recipename)

    if args.append:
        if not os.path.exists(args.append):
            raise DevtoolError('bbappend destination layer directory "%s" '
                               'does not exist' % args.append)
        if not os.path.exists(os.path.join(args.append, 'conf', 'layer.conf')):
            raise DevtoolError('conf/layer.conf not found in bbappend '
                               'destination layer "%s"' % args.append)

    tinfoil = setup_tinfoil()

    rd = parse_recipe(config, tinfoil, args.recipename, True)
    if not rd:
        return 1

    srctree = workspace[args.recipename]['srctree']
    if args.mode == 'auto':
        mode = _guess_recipe_update_mode(srctree, rd)
    else:
        mode = args.mode

    if mode == 'srcrev':
        _update_recipe_srcrev(args, srctree, rd, tinfoil.config_data)
    elif mode == 'patch':
        _update_recipe_patch(args, config, srctree, rd, tinfoil.config_data)
    else:
        raise DevtoolError('update_recipe: invalid mode %s' % mode)

    rf = rd.getVar('FILE', True)
    if rf.startswith(config.workspace_path):
        logger.warn('Recipe file %s has been updated but is inside the workspace - you will need to move it (and any associated files next to it) out to the desired layer before using "devtool reset" in order to keep any changes' % rf)

    return 0


def status(args, config, basepath, workspace):
    """Entry point for the devtool 'status' subcommand"""
    if workspace:
        for recipe, value in workspace.iteritems():
            print("%s: %s" % (recipe, value['srctree']))
    else:
        logger.info('No recipes currently in your workspace - you can use "devtool modify" to work on an existing recipe or "devtool add" to add a new one')
    return 0


def reset(args, config, basepath, workspace):
    """Entry point for the devtool 'reset' subcommand"""
    import bb
    if args.recipename:
        if args.all:
            raise DevtoolError("Recipe cannot be specified if -a/--all is used")
        elif not args.recipename in workspace:
            raise DevtoolError("no recipe named %s in your workspace" %
                               args.recipename)
    elif not args.all:
        raise DevtoolError("Recipe must be specified, or specify -a/--all to "
                           "reset all recipes")
    if args.all:
        recipes = workspace
    else:
        recipes = [args.recipename]

    for pn in recipes:
        if not args.no_clean:
            logger.info('Cleaning sysroot for recipe %s...' % pn)
            try:
                exec_build_env_command(config.init_path, basepath, 'bitbake -c clean %s' % pn)
            except bb.process.ExecutionError as e:
                raise DevtoolError('Command \'%s\' failed, output:\n%s\nIf you '
                                   'wish, you may specify -n/--no-clean to '
                                   'skip running this command when resetting' %
                                   (e.command, e.stdout))

        _check_preserve(config, pn)

        preservepath = os.path.join(config.workspace_path, 'attic', pn)
        def preservedir(origdir):
            if os.path.exists(origdir):
                for root, dirs, files in os.walk(origdir):
                    for fn in files:
                        logger.warn('Preserving %s in %s' % (fn, preservepath))
                        bb.utils.mkdirhier(preservepath)
                        shutil.move(os.path.join(origdir, fn), os.path.join(preservepath, fn))
                    for dn in dirs:
                        os.rmdir(os.path.join(root, dn))
                os.rmdir(origdir)

        preservedir(os.path.join(config.workspace_path, 'recipes', pn))
        # We don't automatically create this dir next to appends, but the user can
        preservedir(os.path.join(config.workspace_path, 'appends', pn))

    return 0


def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_add = subparsers.add_parser('add', help='Add a new recipe',
                                       description='Adds a new recipe')
    parser_add.add_argument('recipename', help='Name for new recipe to add')
    parser_add.add_argument('srctree', help='Path to external source tree')
    parser_add.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    parser_add.add_argument('--fetch', '-f', help='Fetch the specified URI and extract it to create the source tree', metavar='URI')
    parser_add.add_argument('--version', '-V', help='Version to use within recipe (PV)')
    parser_add.set_defaults(func=add)

    parser_modify = subparsers.add_parser('modify', help='Modify the source for an existing recipe',
                                       description='Enables modifying the source for an existing recipe',
                                       formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_modify.add_argument('recipename', help='Name for recipe to edit')
    parser_modify.add_argument('srctree', help='Path to external source tree')
    parser_modify.add_argument('--wildcard', '-w', action="store_true", help='Use wildcard for unversioned bbappend')
    parser_modify.add_argument('--extract', '-x', action="store_true", help='Extract source as well')
    group = parser_modify.add_mutually_exclusive_group()
    group.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    group.add_argument('--no-same-dir', help='Force build in a separate build directory', action="store_true")
    parser_modify.add_argument('--branch', '-b', default="devtool", help='Name for development branch to checkout (only when using -x)')
    parser_modify.set_defaults(func=modify)

    parser_extract = subparsers.add_parser('extract', help='Extract the source for an existing recipe',
                                       description='Extracts the source for an existing recipe',
                                       formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_extract.add_argument('recipename', help='Name for recipe to extract the source for')
    parser_extract.add_argument('srctree', help='Path to where to extract the source tree')
    parser_extract.add_argument('--branch', '-b', default="devtool", help='Name for development branch to checkout')
    parser_extract.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_extract.set_defaults(func=extract)

    parser_update_recipe = subparsers.add_parser('update-recipe', help='Apply changes from external source tree to recipe',
                                       description='Applies changes from external source tree to a recipe (updating/adding/removing patches as necessary, or by updating SRCREV)')
    parser_update_recipe.add_argument('recipename', help='Name of recipe to update')
    parser_update_recipe.add_argument('--mode', '-m', choices=['patch', 'srcrev', 'auto'], default='auto', help='Update mode (where %(metavar)s is %(choices)s; default is %(default)s)', metavar='MODE')
    parser_update_recipe.add_argument('--initial-rev', help='Starting revision for patches')
    parser_update_recipe.add_argument('--append', '-a', help='Write changes to a bbappend in the specified layer instead of the recipe', metavar='LAYERDIR')
    parser_update_recipe.add_argument('--wildcard-version', '-w', help='In conjunction with -a/--append, use a wildcard to make the bbappend apply to any recipe version', action='store_true')
    parser_update_recipe.add_argument('--no-remove', '-n', action="store_true", help='Don\'t remove patches, only add or update')
    parser_update_recipe.set_defaults(func=update_recipe)

    parser_status = subparsers.add_parser('status', help='Show workspace status',
                                          description='Lists recipes currently in your workspace and the paths to their respective external source trees',
                                          formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_status.set_defaults(func=status)

    parser_reset = subparsers.add_parser('reset', help='Remove a recipe from your workspace',
                                         description='Removes the specified recipe from your workspace (resetting its state)',
                                         formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_reset.add_argument('recipename', nargs='?', help='Recipe to reset')
    parser_reset.add_argument('--all', '-a', action="store_true", help='Reset all recipes (clear workspace)')
    parser_reset.add_argument('--no-clean', '-n', action="store_true", help='Don\'t clean the sysroot to remove recipe output')
    parser_reset.set_defaults(func=reset)
