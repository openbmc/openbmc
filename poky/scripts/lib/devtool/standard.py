# Development tool - standard commands plugin
#
# Copyright (C) 2014-2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool standard plugins"""

import os
import sys
import re
import shutil
import subprocess
import tempfile
import logging
import argparse
import argparse_oe
import scriptutils
import errno
import glob
from collections import OrderedDict
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, use_external_build, setup_git_repo, recipe_to_append, get_bbclassextend_targets, update_unlockedsigs, check_prerelease_version, check_git_repo_dirty, check_git_repo_op, DevtoolError
from devtool import parse_recipe

logger = logging.getLogger('devtool')

override_branch_prefix = 'devtool-override-'


def add(args, config, basepath, workspace):
    """Entry point for the devtool 'add' subcommand"""
    import bb
    import oe.recipeutils

    if not args.recipename and not args.srctree and not args.fetch and not args.fetchuri:
        raise argparse_oe.ArgumentUsageError('At least one of recipename, srctree, fetchuri or -f/--fetch must be specified', 'add')

    # These are positional arguments, but because we're nice, allow
    # specifying e.g. source tree without name, or fetch URI without name or
    # source tree (if we can detect that that is what the user meant)
    if scriptutils.is_src_url(args.recipename):
        if not args.fetchuri:
            if args.fetch:
                raise DevtoolError('URI specified as positional argument as well as -f/--fetch')
            args.fetchuri = args.recipename
            args.recipename = ''
    elif scriptutils.is_src_url(args.srctree):
        if not args.fetchuri:
            if args.fetch:
                raise DevtoolError('URI specified as positional argument as well as -f/--fetch')
            args.fetchuri = args.srctree
            args.srctree = ''
    elif args.recipename and not args.srctree:
        if os.sep in args.recipename:
            args.srctree = args.recipename
            args.recipename = None
        elif os.path.isdir(args.recipename):
            logger.warning('Ambiguous argument "%s" - assuming you mean it to be the recipe name' % args.recipename)

    if not args.fetchuri:
        if args.srcrev:
            raise DevtoolError('The -S/--srcrev option is only valid when fetching from an SCM repository')
        if args.srcbranch:
            raise DevtoolError('The -B/--srcbranch option is only valid when fetching from an SCM repository')

    if args.srctree and os.path.isfile(args.srctree):
        args.fetchuri = 'file://' + os.path.abspath(args.srctree)
        args.srctree = ''

    if args.fetch:
        if args.fetchuri:
            raise DevtoolError('URI specified as positional argument as well as -f/--fetch')
        else:
            logger.warning('-f/--fetch option is deprecated - you can now simply specify the URL to fetch as a positional argument instead')
            args.fetchuri = args.fetch

    if args.recipename:
        if args.recipename in workspace:
            raise DevtoolError("recipe %s is already in your workspace" %
                               args.recipename)
        reason = oe.recipeutils.validate_pn(args.recipename)
        if reason:
            raise DevtoolError(reason)

    if args.srctree:
        srctree = os.path.abspath(args.srctree)
        srctreeparent = None
        tmpsrcdir = None
    else:
        srctree = None
        srctreeparent = get_default_srctree(config)
        bb.utils.mkdirhier(srctreeparent)
        tmpsrcdir = tempfile.mkdtemp(prefix='devtoolsrc', dir=srctreeparent)

    if srctree and os.path.exists(srctree):
        if args.fetchuri:
            if not os.path.isdir(srctree):
                raise DevtoolError("Cannot fetch into source tree path %s as "
                                   "it exists and is not a directory" %
                                   srctree)
            elif os.listdir(srctree):
                raise DevtoolError("Cannot fetch into source tree path %s as "
                                   "it already exists and is non-empty" %
                                   srctree)
    elif not args.fetchuri:
        if args.srctree:
            raise DevtoolError("Specified source tree %s could not be found" %
                               args.srctree)
        elif srctree:
            raise DevtoolError("No source tree exists at default path %s - "
                               "either create and populate this directory, "
                               "or specify a path to a source tree, or a "
                               "URI to fetch source from" % srctree)
        else:
            raise DevtoolError("You must either specify a source tree "
                               "or a URI to fetch source from")

    if args.version:
        if '_' in args.version or ' ' in args.version:
            raise DevtoolError('Invalid version string "%s"' % args.version)

    if args.color == 'auto' and sys.stdout.isatty():
        color = 'always'
    else:
        color = args.color
    extracmdopts = ''
    if args.fetchuri:
        source = args.fetchuri
        if srctree:
            extracmdopts += ' -x %s' % srctree
        else:
            extracmdopts += ' -x %s' % tmpsrcdir
    else:
        source = srctree
    if args.recipename:
        extracmdopts += ' -N %s' % args.recipename
    if args.version:
        extracmdopts += ' -V %s' % args.version
    if args.binary:
        extracmdopts += ' -b'
    if args.also_native:
        extracmdopts += ' --also-native'
    if args.src_subdir:
        extracmdopts += ' --src-subdir "%s"' % args.src_subdir
    if args.autorev:
        extracmdopts += ' -a'
    if args.npm_dev:
        extracmdopts += ' --npm-dev'
    if args.no_pypi:
        extracmdopts += ' --no-pypi'
    if args.mirrors:
        extracmdopts += ' --mirrors'
    if args.srcrev:
        extracmdopts += ' --srcrev %s' % args.srcrev
    if args.srcbranch:
        extracmdopts += ' --srcbranch %s' % args.srcbranch
    if args.provides:
        extracmdopts += ' --provides %s' % args.provides

    tempdir = tempfile.mkdtemp(prefix='devtool')
    try:
        try:
            stdout, _ = exec_build_env_command(config.init_path, basepath, 'recipetool --color=%s create --devtool -o %s \'%s\' %s' % (color, tempdir, source, extracmdopts), watch=True)
        except bb.process.ExecutionError as e:
            if e.exitcode == 15:
                raise DevtoolError('Could not auto-determine recipe name, please specify it on the command line')
            else:
                raise DevtoolError('Command \'%s\' failed' % e.command)

        recipes = glob.glob(os.path.join(tempdir, '*.bb'))
        if recipes:
            recipename = os.path.splitext(os.path.basename(recipes[0]))[0].split('_')[0]
            if recipename in workspace:
                raise DevtoolError('A recipe with the same name as the one being created (%s) already exists in your workspace' % recipename)
            recipedir = os.path.join(config.workspace_path, 'recipes', recipename)
            bb.utils.mkdirhier(recipedir)
            recipefile = os.path.join(recipedir, os.path.basename(recipes[0]))
            appendfile = recipe_to_append(recipefile, config)
            if os.path.exists(appendfile):
                # This shouldn't be possible, but just in case
                raise DevtoolError('A recipe with the same name as the one being created already exists in your workspace')
            if os.path.exists(recipefile):
                raise DevtoolError('A recipe file %s already exists in your workspace; this shouldn\'t be there - please delete it before continuing' % recipefile)
            if tmpsrcdir:
                srctree = os.path.join(srctreeparent, recipename)
                if os.path.exists(tmpsrcdir):
                    if os.path.exists(srctree):
                        if os.path.isdir(srctree):
                            try:
                                os.rmdir(srctree)
                            except OSError as e:
                                if e.errno == errno.ENOTEMPTY:
                                    raise DevtoolError('Source tree path %s already exists and is not empty' % srctree)
                                else:
                                    raise
                        else:
                            raise DevtoolError('Source tree path %s already exists and is not a directory' % srctree)
                    logger.info('Using default source tree path %s' % srctree)
                    shutil.move(tmpsrcdir, srctree)
                else:
                    raise DevtoolError('Couldn\'t find source tree created by recipetool')
            bb.utils.mkdirhier(recipedir)
            shutil.move(recipes[0], recipefile)
            # Move any additional files created by recipetool
            for fn in os.listdir(tempdir):
                shutil.move(os.path.join(tempdir, fn), recipedir)
        else:
            raise DevtoolError('Command \'%s\' did not create any recipe file:\n%s' % (e.command, e.stdout))
        attic_recipe = os.path.join(config.workspace_path, 'attic', recipename, os.path.basename(recipefile))
        if os.path.exists(attic_recipe):
            logger.warning('A modified recipe from a previous invocation exists in %s - you may wish to move this over the top of the new recipe if you had changes in it that you want to continue with' % attic_recipe)
    finally:
        if tmpsrcdir and os.path.exists(tmpsrcdir):
            shutil.rmtree(tmpsrcdir)
        shutil.rmtree(tempdir)

    for fn in os.listdir(recipedir):
        _add_md5(config, recipename, os.path.join(recipedir, fn))

    tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
    try:
        try:
            rd = tinfoil.parse_recipe_file(recipefile, False)
        except Exception as e:
            logger.error(str(e))
            rd = None
        if not rd:
            # Parsing failed. We just created this recipe and we shouldn't
            # leave it in the workdir or it'll prevent bitbake from starting
            movefn = '%s.parsefailed' % recipefile
            logger.error('Parsing newly created recipe failed, moving recipe to %s for reference. If this looks to be caused by the recipe itself, please report this error.' % movefn)
            shutil.move(recipefile, movefn)
            return 1

        if args.fetchuri and not args.no_git:
            setup_git_repo(srctree, args.version, 'devtool', d=tinfoil.config_data)

        initial_rev = {}
        if os.path.exists(os.path.join(srctree, '.git')):
            (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srctree)
            initial_rev["."] = stdout.rstrip()
            (stdout, _) = bb.process.run('git submodule --quiet foreach --recursive  \'echo `git rev-parse HEAD` $PWD\'', cwd=srctree)
            for line in stdout.splitlines():
                (rev, submodule) = line.split()
                initial_rev[os.path.relpath(submodule, srctree)] = rev

        if args.src_subdir:
            srctree = os.path.join(srctree, args.src_subdir)

        bb.utils.mkdirhier(os.path.dirname(appendfile))
        with open(appendfile, 'w') as f:
            f.write('inherit externalsrc\n')
            f.write('EXTERNALSRC = "%s"\n' % srctree)

            b_is_s = use_external_build(args.same_dir, args.no_same_dir, rd)
            if b_is_s:
                f.write('EXTERNALSRC_BUILD = "%s"\n' % srctree)
            if initial_rev:
                for key, value in initial_rev.items():
                    f.write('\n# initial_rev %s: %s\n' % (key, value))

            if args.binary:
                f.write('do_install:append() {\n')
                f.write('    rm -rf ${D}/.git\n')
                f.write('    rm -f ${D}/singletask.lock\n')
                f.write('}\n')

            if bb.data.inherits_class('npm', rd):
                f.write('python do_configure:append() {\n')
                f.write('    pkgdir = d.getVar("NPM_PACKAGE")\n')
                f.write('    lockfile = os.path.join(pkgdir, "singletask.lock")\n')
                f.write('    bb.utils.remove(lockfile)\n')
                f.write('}\n')

        # Check if the new layer provides recipes whose priorities have been
        # overriden by PREFERRED_PROVIDER.
        recipe_name = rd.getVar('PN')
        provides = rd.getVar('PROVIDES')
        # Search every item defined in PROVIDES
        for recipe_provided in provides.split():
            preferred_provider = 'PREFERRED_PROVIDER_' + recipe_provided
            current_pprovider = rd.getVar(preferred_provider)
            if current_pprovider and current_pprovider != recipe_name:
                if args.fixed_setup:
                    #if we are inside the eSDK add the new PREFERRED_PROVIDER in the workspace layer.conf
                    layerconf_file = os.path.join(config.workspace_path, "conf", "layer.conf")
                    with open(layerconf_file, 'a') as f:
                        f.write('%s = "%s"\n' % (preferred_provider, recipe_name))
                else:
                    logger.warning('Set \'%s\' in order to use the recipe' % preferred_provider)
                break

        _add_md5(config, recipename, appendfile)

        check_prerelease_version(rd.getVar('PV'), 'devtool add')

        logger.info('Recipe %s has been automatically created; further editing may be required to make it fully functional' % recipefile)

    finally:
        tinfoil.shutdown()

    return 0


def _check_compatible_recipe(pn, d):
    """Check if the recipe is supported by devtool"""
    if pn == 'perf':
        raise DevtoolError("The perf recipe does not actually check out "
                           "source and thus cannot be supported by this tool",
                           4)

    if pn in ['kernel-devsrc', 'package-index'] or pn.startswith('gcc-source'):
        raise DevtoolError("The %s recipe is not supported by this tool" % pn, 4)

    if bb.data.inherits_class('image', d):
        raise DevtoolError("The %s recipe is an image, and therefore is not "
                           "supported by this tool" % pn, 4)

    if bb.data.inherits_class('populate_sdk', d):
        raise DevtoolError("The %s recipe is an SDK, and therefore is not "
                           "supported by this tool" % pn, 4)

    if bb.data.inherits_class('packagegroup', d):
        raise DevtoolError("The %s recipe is a packagegroup, and therefore is "
                           "not supported by this tool" % pn, 4)

    if bb.data.inherits_class('externalsrc', d) and d.getVar('EXTERNALSRC'):
        # Not an incompatibility error per se, so we don't pass the error code
        raise DevtoolError("externalsrc is currently enabled for the %s "
                           "recipe. This prevents the normal do_patch task "
                           "from working. You will need to disable this "
                           "first." % pn)

def _dry_run_copy(src, dst, dry_run_outdir, base_outdir):
    """Common function for copying a file to the dry run output directory"""
    relpath = os.path.relpath(dst, base_outdir)
    if relpath.startswith('..'):
        raise Exception('Incorrect base path %s for path %s' % (base_outdir, dst))
    dst = os.path.join(dry_run_outdir, relpath)
    dst_d = os.path.dirname(dst)
    if dst_d:
        bb.utils.mkdirhier(dst_d)
    # Don't overwrite existing files, otherwise in the case of an upgrade
    # the dry-run written out recipe will be overwritten with an unmodified
    # version
    if not os.path.exists(dst):
        shutil.copy(src, dst)

def _move_file(src, dst, dry_run_outdir=None, base_outdir=None):
    """Move a file. Creates all the directory components of destination path."""
    dry_run_suffix = ' (dry-run)' if dry_run_outdir else ''
    logger.debug('Moving %s to %s%s' % (src, dst, dry_run_suffix))
    if dry_run_outdir:
        # We want to copy here, not move
        _dry_run_copy(src, dst, dry_run_outdir, base_outdir)
    else:
        dst_d = os.path.dirname(dst)
        if dst_d:
            bb.utils.mkdirhier(dst_d)
        shutil.move(src, dst)

def _copy_file(src, dst, dry_run_outdir=None, base_outdir=None):
    """Copy a file. Creates all the directory components of destination path."""
    dry_run_suffix = ' (dry-run)' if dry_run_outdir else ''
    logger.debug('Copying %s to %s%s' % (src, dst, dry_run_suffix))
    if dry_run_outdir:
        _dry_run_copy(src, dst, dry_run_outdir, base_outdir)
    else:
        dst_d = os.path.dirname(dst)
        if dst_d:
            bb.utils.mkdirhier(dst_d)
        shutil.copy(src, dst)

def _git_ls_tree(repodir, treeish='HEAD', recursive=False):
    """List contents of a git treeish"""
    import bb
    cmd = ['git', 'ls-tree', '-z', treeish]
    if recursive:
        cmd.append('-r')
    out, _ = bb.process.run(cmd, cwd=repodir)
    ret = {}
    if out:
        for line in out.split('\0'):
            if line:
                split = line.split(None, 4)
                ret[split[3]] = split[0:3]
    return ret

def _git_modified(repodir):
    """List the difference between HEAD and the index"""
    import bb
    cmd = ['git', 'status', '--porcelain']
    out, _ = bb.process.run(cmd, cwd=repodir)
    ret = []
    if out:
        for line in out.split("\n"):
            if line and not line.startswith('??'):
                ret.append(line[3:])
    return ret


def _git_exclude_path(srctree, path):
    """Return pathspec (list of paths) that excludes certain path"""
    # NOTE: "Filtering out" files/paths in this way is not entirely reliable -
    # we don't catch files that are deleted, for example. A more reliable way
    # to implement this would be to use "negative pathspecs" which were
    # introduced in Git v1.9.0. Revisit this when/if the required Git version
    # becomes greater than that.
    path = os.path.normpath(path)
    recurse = True if len(path.split(os.path.sep)) > 1 else False
    git_files = list(_git_ls_tree(srctree, 'HEAD', recurse).keys())
    if path in git_files:
        git_files.remove(path)
        return git_files
    else:
        return ['.']

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

    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    if not tinfoil:
        # Error already shown
        return 1
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        srctree = os.path.abspath(args.srctree)
        initial_rev, _ = _extract_source(srctree, args.keep_temp, args.branch, False, config, basepath, workspace, args.fixed_setup, rd, tinfoil, no_overrides=args.no_overrides)
        logger.info('Source tree extracted to %s' % srctree)

        if initial_rev:
            return 0
        else:
            return 1
    finally:
        tinfoil.shutdown()

def sync(args, config, basepath, workspace):
    """Entry point for the devtool 'sync' subcommand"""
    import bb

    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    if not tinfoil:
        # Error already shown
        return 1
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        srctree = os.path.abspath(args.srctree)
        initial_rev, _ = _extract_source(srctree, args.keep_temp, args.branch, True, config, basepath, workspace, args.fixed_setup, rd, tinfoil, no_overrides=True)
        logger.info('Source tree %s synchronized' % srctree)

        if initial_rev:
            return 0
        else:
            return 1
    finally:
        tinfoil.shutdown()

def _extract_source(srctree, keep_temp, devbranch, sync, config, basepath, workspace, fixed_setup, d, tinfoil, no_overrides=False):
    """Extract sources of a recipe"""
    import oe.recipeutils
    import oe.patch
    import oe.path

    pn = d.getVar('PN')

    _check_compatible_recipe(pn, d)

    if sync:
        if not os.path.exists(srctree):
                raise DevtoolError("output path %s does not exist" % srctree)
    else:
        if os.path.exists(srctree):
            if not os.path.isdir(srctree):
                raise DevtoolError("output path %s exists and is not a directory" %
                                   srctree)
            elif os.listdir(srctree):
                raise DevtoolError("output path %s already exists and is "
                                   "non-empty" % srctree)

        if 'noexec' in (d.getVarFlags('do_unpack', False) or []):
            raise DevtoolError("The %s recipe has do_unpack disabled, unable to "
                               "extract source" % pn, 4)

    if not sync:
        # Prepare for shutil.move later on
        bb.utils.mkdirhier(srctree)
        os.rmdir(srctree)

    extra_overrides = []
    if not no_overrides:
        history = d.varhistory.variable('SRC_URI')
        for event in history:
            if not 'flag' in event:
                if event['op'].startswith((':append[', ':prepend[')):
                    override = event['op'].split('[')[1].split(']')[0]
                    if not override.startswith('pn-'):
                        extra_overrides.append(override)
        # We want to remove duplicate overrides. If a recipe had multiple
        # SRC_URI_override += values it would cause mulitple instances of
        # overrides. This doesn't play nicely with things like creating a
        # branch for every instance of DEVTOOL_EXTRA_OVERRIDES.
        extra_overrides = list(set(extra_overrides))
        if extra_overrides:
            logger.info('SRC_URI contains some conditional appends/prepends - will create branches to represent these')

    initial_rev = None

    recipefile = d.getVar('FILE')
    appendfile = recipe_to_append(recipefile, config)
    is_kernel_yocto = bb.data.inherits_class('kernel-yocto', d)

    # We need to redirect WORKDIR, STAMPS_DIR etc. under a temporary
    # directory so that:
    # (a) we pick up all files that get unpacked to the WORKDIR, and
    # (b) we don't disturb the existing build
    # However, with recipe-specific sysroots the sysroots for the recipe
    # will be prepared under WORKDIR, and if we used the system temporary
    # directory (i.e. usually /tmp) as used by mkdtemp by default, then
    # our attempts to hardlink files into the recipe-specific sysroots
    # will fail on systems where /tmp is a different filesystem, and it
    # would have to fall back to copying the files which is a waste of
    # time. Put the temp directory under the WORKDIR to prevent that from
    # being a problem.
    tempbasedir = d.getVar('WORKDIR')
    bb.utils.mkdirhier(tempbasedir)
    tempdir = tempfile.mkdtemp(prefix='devtooltmp-', dir=tempbasedir)
    try:
        tinfoil.logger.setLevel(logging.WARNING)

        # FIXME this results in a cache reload under control of tinfoil, which is fine
        # except we don't get the knotty progress bar

        if os.path.exists(appendfile):
            appendbackup = os.path.join(tempdir, os.path.basename(appendfile) + '.bak')
            shutil.copyfile(appendfile, appendbackup)
        else:
            appendbackup = None
            bb.utils.mkdirhier(os.path.dirname(appendfile))
        logger.debug('writing append file %s' % appendfile)
        with open(appendfile, 'a') as f:
            f.write('###--- _extract_source\n')
            f.write('deltask do_recipe_qa\n')
            f.write('deltask do_recipe_qa_setscene\n')
            f.write('ERROR_QA:remove = "patch-fuzz"\n')
            f.write('DEVTOOL_TEMPDIR = "%s"\n' % tempdir)
            f.write('DEVTOOL_DEVBRANCH = "%s"\n' % devbranch)
            if not is_kernel_yocto:
                f.write('PATCHTOOL = "git"\n')
                f.write('PATCH_COMMIT_FUNCTIONS = "1"\n')
            if extra_overrides:
                f.write('DEVTOOL_EXTRA_OVERRIDES = "%s"\n' % ':'.join(extra_overrides))
            f.write('inherit devtool-source\n')
            f.write('###--- _extract_source\n')

        update_unlockedsigs(basepath, workspace, fixed_setup, [pn])

        sstate_manifests = d.getVar('SSTATE_MANIFESTS')
        bb.utils.mkdirhier(sstate_manifests)
        preservestampfile = os.path.join(sstate_manifests, 'preserve-stamps')
        with open(preservestampfile, 'w') as f:
            f.write(d.getVar('STAMP'))
        tinfoil.modified_files()
        try:
            if is_kernel_yocto:
                # We need to generate the kernel config
                task = 'do_configure'
            else:
                task = 'do_patch'

                if 'noexec' in (d.getVarFlags(task, False) or []) or 'task' not in (d.getVarFlags(task, False) or []):
                    logger.info('The %s recipe has %s disabled. Running only '
                                       'do_configure task dependencies' % (pn, task))

                    if 'depends' in d.getVarFlags('do_configure', False):
                        pn = d.getVarFlags('do_configure', False)['depends']
                        pn = pn.replace('${PV}', d.getVar('PV'))
                        pn = pn.replace('${COMPILERDEP}', d.getVar('COMPILERDEP'))
                        task = None

            # Run the fetch + unpack tasks
            res = tinfoil.build_targets(pn,
                                        task,
                                        handle_events=True)
        finally:
            if os.path.exists(preservestampfile):
                os.remove(preservestampfile)

        if not res:
            raise DevtoolError('Extracting source for %s failed' % pn)

        if not is_kernel_yocto and ('noexec' in (d.getVarFlags('do_patch', False) or []) or 'task' not in (d.getVarFlags('do_patch', False) or [])):
            workshareddir = d.getVar('S')
            if os.path.islink(srctree):
                os.unlink(srctree)

            os.symlink(workshareddir, srctree)

            # The initial_rev file is created in devtool_post_unpack function that will not be executed if
            # do_unpack/do_patch tasks are disabled so we have to directly say that source extraction was successful
            return True, True

        try:
            with open(os.path.join(tempdir, 'initial_rev'), 'r') as f:
                initial_rev = f.read()

            with open(os.path.join(tempdir, 'srcsubdir'), 'r') as f:
                srcsubdir = f.read()
        except FileNotFoundError as e:
            raise DevtoolError('Something went wrong with source extraction - the devtool-source class was not active or did not function correctly:\n%s' % str(e))
        srcsubdir_rel = os.path.relpath(srcsubdir, os.path.join(tempdir, 'workdir'))

        # Check if work-shared is empty, if yes
        # find source and copy to work-shared
        if is_kernel_yocto:
            workshareddir = d.getVar('STAGING_KERNEL_DIR')
            staging_kerVer = get_staging_kver(workshareddir)
            kernelVersion = d.getVar('LINUX_VERSION')

            # handle dangling symbolic link in work-shared:
            if os.path.islink(workshareddir):
                os.unlink(workshareddir)

            if os.path.exists(workshareddir) and (not os.listdir(workshareddir) or kernelVersion != staging_kerVer):
                shutil.rmtree(workshareddir)
                oe.path.copyhardlinktree(srcsubdir, workshareddir)
            elif not os.path.exists(workshareddir):
                oe.path.copyhardlinktree(srcsubdir, workshareddir)

        if sync:
            try:
                logger.info('Backing up current %s branch as branch: %s.bak' % (devbranch, devbranch))
                bb.process.run('git branch -f ' + devbranch + '.bak', cwd=srctree)

                # Use git fetch to update the source with the current recipe
                # To be able to update the currently checked out branch with
                # possibly new history (no fast-forward) git needs to be told
                # that's ok
                logger.info('Syncing source files including patches to git branch: %s' % devbranch)
                bb.process.run('git fetch --update-head-ok --force file://' + srcsubdir + ' ' + devbranch + ':' + devbranch, cwd=srctree)
            except bb.process.ExecutionError as e:
                raise DevtoolError("Error when syncing source files to local checkout: %s" % str(e))

        else:
            shutil.move(srcsubdir, srctree)

        if is_kernel_yocto:
            logger.info('Copying kernel config to srctree')
            shutil.copy2(os.path.join(tempdir, '.config'), srctree)

    finally:
        if appendbackup:
            shutil.copyfile(appendbackup, appendfile)
        elif os.path.exists(appendfile):
            os.remove(appendfile)
        if keep_temp:
            logger.info('Preserving temporary directory %s' % tempdir)
        else:
            shutil.rmtree(tempdir)
    return initial_rev, srcsubdir_rel

def _add_md5(config, recipename, filename):
    """Record checksum of a file (or recursively for a directory) to the md5-file of the workspace"""
    import bb.utils

    def addfile(fn):
        md5 = bb.utils.md5_file(fn)
        with open(os.path.join(config.workspace_path, '.devtool_md5'), 'a+') as f:
            md5_str = '%s|%s|%s\n' % (recipename, os.path.relpath(fn, config.workspace_path), md5)
            f.seek(0, os.SEEK_SET)
            if not md5_str in f.read():
                f.write(md5_str)

    if os.path.isdir(filename):
        for root, _, files in os.walk(filename):
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
    preservepath = os.path.join(config.workspace_path, 'attic', recipename)
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
                        logger.warning('File %s modified since it was written, preserving in %s' % (preservefile, preservepath))
                        shutil.move(removefile, os.path.join(preservepath, preservefile))
                    else:
                        os.remove(removefile)
                else:
                    tf.write(line)
    bb.utils.rename(newfile, origfile)

def get_staging_kver(srcdir):
    # Kernel version from work-shared
    kerver = []
    staging_kerVer=""
    if os.path.exists(srcdir) and os.listdir(srcdir):
        with open(os.path.join(srcdir, "Makefile")) as f:
            version = [next(f) for x in range(5)][1:4]
            for word in version:
                kerver.append(word.split('= ')[1].split('\n')[0])
            staging_kerVer = ".".join(kerver)
    return staging_kerVer

def get_staging_kbranch(srcdir):
    staging_kbranch = ""
    if os.path.exists(srcdir) and os.listdir(srcdir):
        (branch, _) = bb.process.run('git branch | grep \\* | cut -d \' \' -f2', cwd=srcdir)
        staging_kbranch = "".join(branch.split('\n')[0])
    return staging_kbranch

def get_real_srctree(srctree, s, workdir):
    # Check that recipe isn't using a shared workdir
    s = os.path.abspath(s)
    workdir = os.path.abspath(workdir)
    if s.startswith(workdir) and s != workdir and os.path.dirname(s) != workdir:
        # Handle if S is set to a subdirectory of the source
        srcsubdir = os.path.relpath(s, workdir).split(os.sep, 1)[1]
        srctree = os.path.join(srctree, srcsubdir)
    return srctree

def modify(args, config, basepath, workspace):
    """Entry point for the devtool 'modify' subcommand"""
    import bb
    import oe.recipeutils
    import oe.patch
    import oe.path

    if args.recipename in workspace:
        raise DevtoolError("recipe %s is already in your workspace" %
                           args.recipename)

    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        pn = rd.getVar('PN')
        if pn != args.recipename:
            logger.info('Mapping %s to %s' % (args.recipename, pn))
        if pn in workspace:
            raise DevtoolError("recipe %s is already in your workspace" %
                            pn)

        if args.srctree:
            srctree = os.path.abspath(args.srctree)
        else:
            srctree = get_default_srctree(config, pn)

        if args.no_extract and not os.path.isdir(srctree):
            raise DevtoolError("--no-extract specified and source path %s does "
                            "not exist or is not a directory" %
                            srctree)

        recipefile = rd.getVar('FILE')
        appendfile = recipe_to_append(recipefile, config, args.wildcard)
        if os.path.exists(appendfile):
            raise DevtoolError("Another variant of recipe %s is already in your "
                            "workspace (only one variant of a recipe can "
                            "currently be worked on at once)"
                            % pn)

        _check_compatible_recipe(pn, rd)

        initial_revs = {}
        commits = {}
        check_commits = False

        if bb.data.inherits_class('kernel-yocto', rd):
            # Current set kernel version
            kernelVersion = rd.getVar('LINUX_VERSION')
            srcdir = rd.getVar('STAGING_KERNEL_DIR')
            kbranch = rd.getVar('KBRANCH')

            staging_kerVer = get_staging_kver(srcdir)
            staging_kbranch = get_staging_kbranch(srcdir)
            if (os.path.exists(srcdir) and os.listdir(srcdir)) and (kernelVersion in staging_kerVer and staging_kbranch == kbranch):
                oe.path.copyhardlinktree(srcdir, srctree)
                unpackdir = rd.getVar('UNPACKDIR')
                srcsubdir = rd.getVar('S')

                # Add locally copied files to gitignore as we add back to the metadata directly
                local_files = oe.recipeutils.get_recipe_local_files(rd)
                srcabspath = os.path.abspath(srcsubdir)
                local_files = [fname for fname in local_files if
                               os.path.exists(os.path.join(unpackdir, fname)) and
                               srcabspath == unpackdir]
                if local_files:
                    with open(os.path.join(srctree, '.gitignore'), 'a+') as f:
                        f.write('# Ignore local files, by default. Remove following lines'
                                'if you want to commit the directory to Git\n')
                        for fname in local_files:
                            f.write('%s\n' % fname)

                task = 'do_configure'
                res = tinfoil.build_targets(pn, task, handle_events=True)

                # Copy .config to workspace
                kconfpath = rd.getVar('B')
                logger.info('Copying kernel config to workspace')
                shutil.copy2(os.path.join(kconfpath, '.config'), srctree)

                # Set this to true, we still need to get initial_rev
                # by parsing the git repo
                args.no_extract = True

        if not args.no_extract:
            initial_revs["."], _ = _extract_source(srctree, args.keep_temp, args.branch, False, config, basepath, workspace, args.fixed_setup, rd, tinfoil, no_overrides=args.no_overrides)
            if not initial_revs["."]:
                return 1
            logger.info('Source tree extracted to %s' % srctree)

            if os.path.exists(os.path.join(srctree, '.git')):
                # Get list of commits since this revision
                (stdout, _) = bb.process.run('git rev-list --reverse %s..HEAD' % initial_revs["."], cwd=srctree)
                commits["."] = stdout.split()
                check_commits = True
                try:
                    (stdout, _) = bb.process.run('git submodule --quiet foreach --recursive  \'echo `git rev-parse devtool-base` $PWD\'', cwd=srctree)
                except bb.process.ExecutionError:
                    stdout = ""
                for line in stdout.splitlines():
                    (rev, submodule_path) = line.split()
                    submodule = os.path.relpath(submodule_path, srctree)
                    initial_revs[submodule] = rev
                    (stdout, _) = bb.process.run('git rev-list --reverse devtool-base..HEAD', cwd=submodule_path)
                    commits[submodule] = stdout.split()
        else:
            if os.path.exists(os.path.join(srctree, '.git')):
                # Check if it's a tree previously extracted by us. This is done
                # by ensuring that devtool-base and args.branch (devtool) exist.
                # The check_commits logic will cause an exception if either one
                # of these doesn't exist
                try:
                    (stdout, _) = bb.process.run('git branch --contains devtool-base', cwd=srctree)
                    bb.process.run('git rev-parse %s' % args.branch, cwd=srctree)
                except bb.process.ExecutionError:
                    stdout = ''
                if stdout:
                    check_commits = True
                for line in stdout.splitlines():
                    if line.startswith('*'):
                        (stdout, _) = bb.process.run('git rev-parse devtool-base', cwd=srctree)
                        initial_revs["."] = stdout.rstrip()
                if "." not in initial_revs:
                    # Otherwise, just grab the head revision
                    (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srctree)
                    initial_revs["."] = stdout.rstrip()

        branch_patches = {}
        if check_commits:
            # Check if there are override branches
            (stdout, _) = bb.process.run('git branch', cwd=srctree)
            branches = []
            for line in stdout.rstrip().splitlines():
                branchname = line[2:].rstrip()
                if branchname.startswith(override_branch_prefix):
                    branches.append(branchname)
            if branches:
                logger.warning('SRC_URI is conditionally overridden in this recipe, thus several %s* branches have been created, one for each override that makes changes to SRC_URI. It is recommended that you make changes to the %s branch first, then checkout and rebase each %s* branch and update any unique patches there (duplicates on those branches will be ignored by devtool finish/update-recipe)' % (override_branch_prefix, args.branch, override_branch_prefix))
            branches.insert(0, args.branch)
            seen_patches = []
            for branch in branches:
                branch_patches[branch] = []
                (stdout, _) = bb.process.run('git rev-list devtool-base..%s' % branch, cwd=srctree)
                for sha1 in stdout.splitlines():
                    notes = oe.patch.GitApplyTree.getNotes(srctree, sha1.strip())
                    origpatch = notes.get(oe.patch.GitApplyTree.original_patch)
                    if origpatch and origpatch not in seen_patches:
                        seen_patches.append(origpatch)
                        branch_patches[branch].append(origpatch)

        # Need to grab this here in case the source is within a subdirectory
        srctreebase = srctree
        srctree = get_real_srctree(srctree, rd.getVar('S'), rd.getVar('WORKDIR'))

        bb.utils.mkdirhier(os.path.dirname(appendfile))
        with open(appendfile, 'w') as f:
            # if not present, add type=git-dependency to the secondary sources
            # (non local files) so they can be extracted correctly when building a recipe after
            #  doing a devtool modify on it
            src_uri = rd.getVar('SRC_URI').split()
            src_uri_append = []
            src_uri_remove = []

            # Assume first entry is main source extracted in ${S} so skip it
            src_uri = src_uri[1::]

            # Add "type=git-dependency" to all non local sources
            for url in src_uri:
                if not url.startswith('file://') and not 'type=' in url:
                    src_uri_remove.append(url)
                    src_uri_append.append('%s;type=git-dependency' % url)

            if src_uri_remove:
                f.write('SRC_URI:remove = "%s"\n' % ' '.join(src_uri_remove))
                f.write('SRC_URI:append = " %s"\n\n' % ' '.join(src_uri_append))

            f.write('FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n')
            # Local files can be modified/tracked in separate subdir under srctree
            # Mostly useful for packages with S != WORKDIR
            f.write('FILESPATH:prepend := "%s:"\n' %
                    os.path.join(srctreebase, 'oe-local-files'))
            f.write('# srctreebase: %s\n' % srctreebase)

            f.write('\ninherit externalsrc\n')
            f.write('# NOTE: We use pn- overrides here to avoid affecting multiple variants in the case where the recipe uses BBCLASSEXTEND\n')
            f.write('EXTERNALSRC:pn-%s = "%s"\n' % (pn, srctree))

            b_is_s = use_external_build(args.same_dir, args.no_same_dir, rd)
            if b_is_s:
                f.write('EXTERNALSRC_BUILD:pn-%s = "%s"\n' % (pn, srctree))

            if bb.data.inherits_class('kernel', rd):
                f.write('\ndo_kernel_configme:prepend() {\n'
                        '    if [ -e ${S}/.config ]; then\n'
                        '        mv ${S}/.config ${S}/.config.old\n'
                        '    fi\n'
                        '}\n')
            if rd.getVarFlag('do_menuconfig', 'task'):
                f.write('\ndo_configure:append() {\n'
                '    if [ ${@oe.types.boolean(d.getVar("KCONFIG_CONFIG_ENABLE_MENUCONFIG"))} = True ]; then\n'
                '        cp ${KCONFIG_CONFIG_ROOTDIR}/.config ${S}/.config.baseline\n'
                '        ln -sfT ${KCONFIG_CONFIG_ROOTDIR}/.config ${S}/.config.new\n'
                '    fi\n'
                '}\n')
            if initial_revs:
                for name, rev in initial_revs.items():
                    f.write('\n# initial_rev %s: %s\n' % (name, rev))
                    if name in commits:
                        for commit in commits[name]:
                            f.write('# commit %s: %s\n' % (name, commit))
            if branch_patches:
                for branch in branch_patches:
                    if branch == args.branch:
                        continue
                    f.write('# patches_%s: %s\n' % (branch, ','.join(branch_patches[branch])))

        update_unlockedsigs(basepath, workspace, args.fixed_setup, [pn])

        _add_md5(config, pn, appendfile)

        logger.info('Recipe %s now set up to build from %s' % (pn, srctree))

    finally:
        tinfoil.shutdown()

    return 0


def rename(args, config, basepath, workspace):
    """Entry point for the devtool 'rename' subcommand"""
    import bb
    import oe.recipeutils

    check_workspace_recipe(workspace, args.recipename)

    if not (args.newname or args.version):
        raise DevtoolError('You must specify a new name, a version with -V/--version, or both')

    recipefile = workspace[args.recipename]['recipefile']
    if not recipefile:
        raise DevtoolError('devtool rename can only be used where the recipe file itself is in the workspace (e.g. after devtool add)')

    if args.newname and args.newname != args.recipename:
        reason = oe.recipeutils.validate_pn(args.newname)
        if reason:
            raise DevtoolError(reason)
        newname = args.newname
    else:
        newname = args.recipename

    append = workspace[args.recipename]['bbappend']
    appendfn = os.path.splitext(os.path.basename(append))[0]
    splitfn = appendfn.split('_')
    if len(splitfn) > 1:
        origfnver = appendfn.split('_')[1]
    else:
        origfnver = ''

    recipefilemd5 = None
    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        bp = rd.getVar('BP')
        bpn = rd.getVar('BPN')
        if newname != args.recipename:
            localdata = rd.createCopy()
            localdata.setVar('PN', newname)
            newbpn = localdata.getVar('BPN')
        else:
            newbpn = bpn
        s = rd.getVar('S', False)
        src_uri = rd.getVar('SRC_URI', False)
        pv = rd.getVar('PV')

        # Correct variable values that refer to the upstream source - these
        # values must stay the same, so if the name/version are changing then
        # we need to fix them up
        new_s = s
        new_src_uri = src_uri
        if newbpn != bpn:
            # ${PN} here is technically almost always incorrect, but people do use it
            new_s = new_s.replace('${BPN}', bpn)
            new_s = new_s.replace('${PN}', bpn)
            new_s = new_s.replace('${BP}', '%s-${PV}' % bpn)
            new_src_uri = new_src_uri.replace('${BPN}', bpn)
            new_src_uri = new_src_uri.replace('${PN}', bpn)
            new_src_uri = new_src_uri.replace('${BP}', '%s-${PV}' % bpn)
        if args.version and origfnver == pv:
            new_s = new_s.replace('${PV}', pv)
            new_s = new_s.replace('${BP}', '${BPN}-%s' % pv)
            new_src_uri = new_src_uri.replace('${PV}', pv)
            new_src_uri = new_src_uri.replace('${BP}', '${BPN}-%s' % pv)
        patchfields = {}
        if new_s != s:
            patchfields['S'] = new_s
        if new_src_uri != src_uri:
            patchfields['SRC_URI'] = new_src_uri
        if patchfields:
            recipefilemd5 = bb.utils.md5_file(recipefile)
            oe.recipeutils.patch_recipe(rd, recipefile, patchfields)
            newrecipefilemd5 = bb.utils.md5_file(recipefile)
    finally:
        tinfoil.shutdown()

    if args.version:
        newver = args.version
    else:
        newver = origfnver

    if newver:
        newappend = '%s_%s.bbappend' % (newname, newver)
        newfile =  '%s_%s.bb' % (newname, newver)
    else:
        newappend = '%s.bbappend' % newname
        newfile = '%s.bb' % newname

    oldrecipedir = os.path.dirname(recipefile)
    newrecipedir = os.path.join(config.workspace_path, 'recipes', newname)
    if oldrecipedir != newrecipedir:
        bb.utils.mkdirhier(newrecipedir)

    newappend = os.path.join(os.path.dirname(append), newappend)
    newfile = os.path.join(newrecipedir, newfile)

    # Rename bbappend
    logger.info('Renaming %s to %s' % (append, newappend))
    bb.utils.rename(append, newappend)
    # Rename recipe file
    logger.info('Renaming %s to %s' % (recipefile, newfile))
    bb.utils.rename(recipefile, newfile)

    # Rename source tree if it's the default path
    appendmd5 = None
    if not args.no_srctree:
        srctree = workspace[args.recipename]['srctree']
        if os.path.abspath(srctree) == os.path.join(config.workspace_path, 'sources', args.recipename):
            newsrctree = os.path.join(config.workspace_path, 'sources', newname)
            logger.info('Renaming %s to %s' % (srctree, newsrctree))
            shutil.move(srctree, newsrctree)
            # Correct any references (basically EXTERNALSRC*) in the .bbappend
            appendmd5 = bb.utils.md5_file(newappend)
            appendlines = []
            with open(newappend, 'r') as f:
                for line in f:
                    appendlines.append(line)
            with open(newappend, 'w') as f:
                for line in appendlines:
                    if srctree in line:
                        line = line.replace(srctree, newsrctree)
                    f.write(line)
            newappendmd5 = bb.utils.md5_file(newappend)

    bpndir = None
    newbpndir = None
    if newbpn != bpn:
        bpndir = os.path.join(oldrecipedir, bpn)
        if os.path.exists(bpndir):
            newbpndir = os.path.join(newrecipedir, newbpn)
            logger.info('Renaming %s to %s' % (bpndir, newbpndir))
            shutil.move(bpndir, newbpndir)

    bpdir = None
    newbpdir = None
    if newver != origfnver or newbpn != bpn:
        bpdir = os.path.join(oldrecipedir, bp)
        if os.path.exists(bpdir):
            newbpdir = os.path.join(newrecipedir, '%s-%s' % (newbpn, newver))
            logger.info('Renaming %s to %s' % (bpdir, newbpdir))
            shutil.move(bpdir, newbpdir)

    if oldrecipedir != newrecipedir:
        # Move any stray files and delete the old recipe directory
        for entry in os.listdir(oldrecipedir):
            oldpath = os.path.join(oldrecipedir, entry)
            newpath = os.path.join(newrecipedir, entry)
            logger.info('Renaming %s to %s' % (oldpath, newpath))
            shutil.move(oldpath, newpath)
        os.rmdir(oldrecipedir)

    # Now take care of entries in .devtool_md5
    md5entries = []
    with open(os.path.join(config.workspace_path, '.devtool_md5'), 'r') as f:
        for line in f:
            md5entries.append(line)

    if bpndir and newbpndir:
        relbpndir = os.path.relpath(bpndir, config.workspace_path) + '/'
    else:
        relbpndir = None
    if bpdir and newbpdir:
        relbpdir = os.path.relpath(bpdir, config.workspace_path) + '/'
    else:
        relbpdir = None

    with open(os.path.join(config.workspace_path, '.devtool_md5'), 'w') as f:
        for entry in md5entries:
            splitentry = entry.rstrip().split('|')
            if len(splitentry) > 2:
                if splitentry[0] == args.recipename:
                    splitentry[0] = newname
                    if splitentry[1] == os.path.relpath(append, config.workspace_path):
                        splitentry[1] = os.path.relpath(newappend, config.workspace_path)
                        if appendmd5 and splitentry[2] == appendmd5:
                            splitentry[2] = newappendmd5
                    elif splitentry[1] == os.path.relpath(recipefile, config.workspace_path):
                        splitentry[1] = os.path.relpath(newfile, config.workspace_path)
                        if recipefilemd5 and splitentry[2] == recipefilemd5:
                            splitentry[2] = newrecipefilemd5
                    elif relbpndir and splitentry[1].startswith(relbpndir):
                        splitentry[1] = os.path.relpath(os.path.join(newbpndir, splitentry[1][len(relbpndir):]), config.workspace_path)
                    elif relbpdir and splitentry[1].startswith(relbpdir):
                        splitentry[1] = os.path.relpath(os.path.join(newbpdir, splitentry[1][len(relbpdir):]), config.workspace_path)
                    entry = '|'.join(splitentry) + '\n'
            f.write(entry)
    return 0


def _get_patchset_revs(srctree, recipe_path, initial_rev=None, force_patch_refresh=False):
    """Get initial and update rev of a recipe. These are the start point of the
    whole patchset and start point for the patches to be re-generated/updated.
    """
    import bb

    # Get current branch
    stdout, _ = bb.process.run('git rev-parse --abbrev-ref HEAD',
                               cwd=srctree)
    branchname = stdout.rstrip()

    # Parse initial rev from recipe if not specified
    commits = {}
    patches = []
    initial_revs = {}
    with open(recipe_path, 'r') as f:
        for line in f:
            pattern = r'^#\s.*\s(.*):\s([0-9a-fA-F]+)$'
            match = re.search(pattern, line)
            if match:
                name = match.group(1)
                rev = match.group(2)
                if line.startswith('# initial_rev'):
                    if not (name == "." and initial_rev):
                        initial_revs[name] = rev
                elif line.startswith('# commit') and not force_patch_refresh:
                    if name not in commits:
                        commits[name] = [rev]
                    else:
                        commits[name].append(rev)
                elif line.startswith('# patches_%s:' % branchname):
                    patches = line.split(':')[-1].strip().split(',')

    update_revs = dict(initial_revs)
    changed_revs = {}
    for name, rev in initial_revs.items():
        # Find first actually changed revision
        stdout, _ = bb.process.run('git rev-list --reverse %s..HEAD' %
                                   rev, cwd=os.path.join(srctree, name))
        newcommits = stdout.split()
        if name in commits:
            for i in range(min(len(commits[name]), len(newcommits))):
                if newcommits[i] == commits[name][i]:
                    update_revs[name] = commits[name][i]

        try:
            stdout, _ = bb.process.run('git cherry devtool-patched',
                                        cwd=os.path.join(srctree, name))
        except bb.process.ExecutionError as err:
            stdout = None

        if stdout is not None and not force_patch_refresh:
            for line in stdout.splitlines():
                if line.startswith('+ '):
                    rev = line.split()[1]
                    if rev in newcommits:
                        if name not in changed_revs:
                            changed_revs[name] = [rev]
                        else:
                            changed_revs[name].append(rev)

    return initial_revs, update_revs, changed_revs, patches

def _remove_file_entries(srcuri, filelist):
    """Remove file:// entries from SRC_URI"""
    remaining = filelist[:]
    entries = []
    for fname in filelist:
        basename = os.path.basename(fname)
        for i in range(len(srcuri)):
            if (srcuri[i].startswith('file://') and
                    os.path.basename(srcuri[i].split(';')[0]) == basename):
                entries.append(srcuri[i])
                remaining.remove(fname)
                srcuri.pop(i)
                break
    return entries, remaining

def _replace_srcuri_entry(srcuri, filename, newentry):
    """Replace entry corresponding to specified file with a new entry"""
    basename = os.path.basename(filename)
    for i in range(len(srcuri)):
        if os.path.basename(srcuri[i].split(';')[0]) == basename:
            srcuri.pop(i)
            srcuri.insert(i, newentry)
            break

def _remove_source_files(append, files, destpath, no_report_remove=False, dry_run=False):
    """Unlink existing patch files"""

    dry_run_suffix = ' (dry-run)' if dry_run else ''

    for path in files:
        if append:
            if not destpath:
                raise Exception('destpath should be set here')
            path = os.path.join(destpath, os.path.basename(path))

        if os.path.exists(path):
            if not no_report_remove:
                logger.info('Removing file %s%s' % (path, dry_run_suffix))
            if not dry_run:
                # FIXME "git rm" here would be nice if the file in question is
                #       tracked
                # FIXME there's a chance that this file is referred to by
                #       another recipe, in which case deleting wouldn't be the
                #       right thing to do
                os.remove(path)
                # Remove directory if empty
                try:
                    os.rmdir(os.path.dirname(path))
                except OSError as ose:
                    if ose.errno != errno.ENOTEMPTY:
                        raise


def _export_patches(srctree, rd, start_revs, destdir, changed_revs=None):
    """Export patches from srctree to given location.
       Returns three-tuple of dicts:
         1. updated - patches that already exist in SRCURI
         2. added - new patches that don't exist in SRCURI
         3  removed - patches that exist in SRCURI but not in exported patches
       In each dict the key is the 'basepath' of the URI and value is:
         - for updated and added dicts, a dict with 2 optionnal keys:
            - 'path': the absolute path to the existing file in recipe space (if any)
            - 'patchdir': the directory in wich the patch should be applied (if any)
         - for removed dict, the absolute path to the existing file in recipe space
    """
    import oe.recipeutils
    from oe.patch import GitApplyTree
    updated = OrderedDict()
    added = OrderedDict()
    seqpatch_re = re.compile('^([0-9]{4}-)?(.+)')

    existing_patches = dict((os.path.basename(path), path) for path in
                            oe.recipeutils.get_recipe_patches(rd))
    logger.debug('Existing patches: %s' % existing_patches)

    # Generate patches from Git, exclude local files directory
    patch_pathspec = _git_exclude_path(srctree, 'oe-local-files')
    GitApplyTree.extractPatches(srctree, start_revs, destdir, patch_pathspec)
    for dirpath, dirnames, filenames in os.walk(destdir):
        new_patches = filenames
        reldirpath = os.path.relpath(dirpath, destdir)
        for new_patch in new_patches:
            # Strip numbering from patch names. If it's a git sequence named patch,
            # the numbers might not match up since we are starting from a different
            # revision This does assume that people are using unique shortlog
            # values, but they ought to be anyway...
            new_basename = seqpatch_re.match(new_patch).group(2)
            match_name = None
            for old_patch in existing_patches:
                old_basename = seqpatch_re.match(old_patch).group(2)
                old_basename_splitext = os.path.splitext(old_basename)
                if old_basename.endswith(('.gz', '.bz2', '.Z')) and old_basename_splitext[0] == new_basename:
                    old_patch_noext = os.path.splitext(old_patch)[0]
                    match_name = old_patch_noext
                    break
                elif new_basename == old_basename:
                    match_name = old_patch
                    break
            if match_name:
                # Rename patch files
                if new_patch != match_name:
                    bb.utils.rename(os.path.join(destdir, new_patch),
                              os.path.join(destdir, match_name))
                # Need to pop it off the list now before checking changed_revs
                oldpath = existing_patches.pop(old_patch)
                if changed_revs is not None and dirpath in changed_revs:
                    # Avoid updating patches that have not actually changed
                    with open(os.path.join(dirpath, match_name), 'r') as f:
                        firstlineitems = f.readline().split()
                        # Looking for "From <hash>" line
                        if len(firstlineitems) > 1 and len(firstlineitems[1]) == 40:
                            if not firstlineitems[1] in changed_revs[dirpath]:
                                continue
                # Recompress if necessary
                if oldpath.endswith(('.gz', '.Z')):
                    bb.process.run(['gzip', match_name], cwd=destdir)
                    if oldpath.endswith('.gz'):
                        match_name += '.gz'
                    else:
                        match_name += '.Z'
                elif oldpath.endswith('.bz2'):
                    bb.process.run(['bzip2', match_name], cwd=destdir)
                    match_name += '.bz2'
                updated[match_name] = {'path' : oldpath}
                if reldirpath != ".":
                    updated[match_name]['patchdir'] = reldirpath
            else:
                added[new_patch] = {}
                if reldirpath != ".":
                    added[new_patch]['patchdir'] = reldirpath

    return (updated, added, existing_patches)


def _create_kconfig_diff(srctree, rd, outfile):
    """Create a kconfig fragment"""
    # Only update config fragment if both config files exist
    orig_config = os.path.join(srctree, '.config.baseline')
    new_config = os.path.join(srctree, '.config.new')
    if os.path.exists(orig_config) and os.path.exists(new_config):
        cmd = ['diff', '--new-line-format=%L', '--old-line-format=',
               '--unchanged-line-format=', orig_config, new_config]
        pipe = subprocess.Popen(cmd, stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE)
        stdout, stderr = pipe.communicate()
        if pipe.returncode == 1:
            logger.info("Updating config fragment %s" % outfile)
            with open(outfile, 'wb') as fobj:
                fobj.write(stdout)
        elif pipe.returncode == 0:
            logger.info("Would remove config fragment %s" % outfile)
            if os.path.exists(outfile):
                # Remove fragment file in case of empty diff
                logger.info("Removing config fragment %s" % outfile)
                os.unlink(outfile)
        else:
            raise bb.process.ExecutionError(cmd, pipe.returncode, stdout, stderr)
        return True
    return False


def _export_local_files(srctree, rd, destdir, srctreebase):
    """Copy local files from srctree to given location.
       Returns three-tuple of dicts:
         1. updated - files that already exist in SRCURI
         2. added - new files files that don't exist in SRCURI
         3  removed - files that exist in SRCURI but not in exported files
       In each dict the key is the 'basepath' of the URI and value is:
         - for updated and added dicts, a dict with 1 optionnal key:
           - 'path': the absolute path to the existing file in recipe space (if any)
         - for removed dict, the absolute path to the existing file in recipe space
    """
    import oe.recipeutils

    # Find out local files (SRC_URI files that exist in the "recipe space").
    # Local files that reside in srctree are not included in patch generation.
    # Instead they are directly copied over the original source files (in
    # recipe space).
    existing_files = oe.recipeutils.get_recipe_local_files(rd)

    new_set = None
    updated = OrderedDict()
    added = OrderedDict()
    removed = OrderedDict()

    # Get current branch and return early with empty lists
    # if on one of the override branches
    # (local files are provided only for the main branch and processing
    # them against lists from recipe overrides will result in mismatches
    # and broken modifications to recipes).
    stdout, _ = bb.process.run('git rev-parse --abbrev-ref HEAD',
                               cwd=srctree)
    branchname = stdout.rstrip()
    if branchname.startswith(override_branch_prefix):
        return (updated, added, removed)

    files = _git_modified(srctree)
        #if not files:
        #    files = _ls_tree(srctree)
    for f in files:
        fullfile = os.path.join(srctree, f)
        if os.path.exists(os.path.join(fullfile, ".git")):
            # submodules handled elsewhere
            continue
        if f not in existing_files:
            added[f] = {}
            if os.path.isdir(os.path.join(srctree, f)):
                shutil.copytree(fullfile, os.path.join(destdir, f))
            else:
                shutil.copy2(fullfile, os.path.join(destdir, f))
        elif not os.path.exists(fullfile):
            removed[f] = existing_files[f]
        elif f in existing_files:
            updated[f] = {'path' : existing_files[f]}
            if os.path.isdir(os.path.join(srctree, f)):
                shutil.copytree(fullfile, os.path.join(destdir, f))
            else:
                shutil.copy2(fullfile, os.path.join(destdir, f))

    # Special handling for kernel config
    if bb.data.inherits_class('kernel-yocto', rd):
        fragment_fn = 'devtool-fragment.cfg'
        fragment_path = os.path.join(destdir, fragment_fn)
        if _create_kconfig_diff(srctree, rd, fragment_path):
            if os.path.exists(fragment_path):
                if fragment_fn in removed:
                    del removed[fragment_fn]
                if fragment_fn not in updated and fragment_fn not in added:
                    added[fragment_fn] = {}
            else:
                if fragment_fn in updated:
                    revoved[fragment_fn] = updated[fragment_fn]
                    del updated[fragment_fn]

    # Special handling for cml1, ccmake, etc bbclasses that generated
    # configuration fragment files that are consumed as source files
    for frag_class, frag_name in [("cml1", "fragment.cfg"), ("ccmake", "site-file.cmake")]:
        if bb.data.inherits_class(frag_class, rd):
            srcpath = os.path.join(rd.getVar('WORKDIR'), frag_name)
            if os.path.exists(srcpath):
                if frag_name in removed:
                    del removed[frag_name]
                if frag_name not in updated:
                    added[frag_name] = {}
                # copy fragment into destdir
                shutil.copy2(srcpath, destdir)

    return (updated, added, removed)


def _determine_files_dir(rd):
    """Determine the appropriate files directory for a recipe"""
    recipedir = rd.getVar('FILE_DIRNAME')
    for entry in rd.getVar('FILESPATH').split(':'):
        relpth = os.path.relpath(entry, recipedir)
        if not os.sep in relpth:
            # One (or zero) levels below only, so we don't put anything in machine-specific directories
            if os.path.isdir(entry):
                return entry
    return os.path.join(recipedir, rd.getVar('BPN'))


def _update_recipe_srcrev(recipename, workspace, srctree, rd, appendlayerdir, wildcard_version, no_remove, no_report_remove, dry_run_outdir=None):
    """Implement the 'srcrev' mode of update-recipe"""
    import bb
    import oe.recipeutils

    dry_run_suffix = ' (dry-run)' if dry_run_outdir else ''

    recipefile = rd.getVar('FILE')
    recipedir = os.path.basename(recipefile)
    logger.info('Updating SRCREV in recipe %s%s' % (recipedir, dry_run_suffix))

    # Get original SRCREV
    old_srcrev = rd.getVar('SRCREV') or ''
    if old_srcrev == "INVALID":
            raise DevtoolError('Update mode srcrev is only valid for recipe fetched from an SCM repository')
    old_srcrev = {'.': old_srcrev}

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
    remove_files = []
    patchfields = {}
    patchfields['SRCREV'] = srcrev
    orig_src_uri = rd.getVar('SRC_URI', False) or ''
    srcuri = orig_src_uri.split()
    tempdir = tempfile.mkdtemp(prefix='devtool')
    update_srcuri = False
    appendfile = None
    try:
        local_files_dir = tempfile.mkdtemp(dir=tempdir)
        srctreebase = workspace[recipename]['srctreebase']
        upd_f, new_f, del_f = _export_local_files(srctree, rd, local_files_dir, srctreebase)
        if not no_remove:
            # Find list of existing patches in recipe file
            patches_dir = tempfile.mkdtemp(dir=tempdir)
            upd_p, new_p, del_p = _export_patches(srctree, rd, old_srcrev,
                                                  patches_dir)
            logger.debug('Patches: update %s, new %s, delete %s' % (dict(upd_p), dict(new_p), dict(del_p)))

            # Remove deleted local files and "overlapping" patches
            remove_files = list(del_f.values()) + [value["path"] for value in upd_p.values() if "path" in value] + [value["path"] for value in del_p.values() if "path" in value]
            if remove_files:
                removedentries = _remove_file_entries(srcuri, remove_files)[0]
                update_srcuri = True

        if appendlayerdir:
            files = dict((os.path.join(local_files_dir, key), val) for
                          key, val in list(upd_f.items()) + list(new_f.items()))
            removevalues = {}
            if update_srcuri:
                removevalues  = {'SRC_URI': removedentries}
                patchfields['SRC_URI'] = '\\\n    '.join(srcuri)
            if dry_run_outdir:
                logger.info('Creating bbappend (dry-run)')
            appendfile, destpath = oe.recipeutils.bbappend_recipe(
                    rd, appendlayerdir, files, wildcardver=wildcard_version,
                    extralines=patchfields, removevalues=removevalues,
                    redirect_output=dry_run_outdir)
        else:
            files_dir = _determine_files_dir(rd)
            for basepath, param in upd_f.items():
                path = param['path']
                logger.info('Updating file %s%s' % (basepath, dry_run_suffix))
                if os.path.isabs(basepath):
                    # Original file (probably with subdir pointing inside source tree)
                    # so we do not want to move it, just copy
                    _copy_file(basepath, path, dry_run_outdir=dry_run_outdir, base_outdir=recipedir)
                else:
                    _move_file(os.path.join(local_files_dir, basepath), path,
                               dry_run_outdir=dry_run_outdir, base_outdir=recipedir)
                update_srcuri= True
            for basepath, param in new_f.items():
                path = param['path']
                logger.info('Adding new file %s%s' % (basepath, dry_run_suffix))
                _move_file(os.path.join(local_files_dir, basepath),
                           os.path.join(files_dir, basepath),
                           dry_run_outdir=dry_run_outdir,
                           base_outdir=recipedir)
                srcuri.append('file://%s' % basepath)
                update_srcuri = True
            if update_srcuri:
                patchfields['SRC_URI'] = ' '.join(srcuri)
            ret = oe.recipeutils.patch_recipe(rd, recipefile, patchfields, redirect_output=dry_run_outdir)
    finally:
        shutil.rmtree(tempdir)
    if not 'git://' in orig_src_uri:
        logger.info('You will need to update SRC_URI within the recipe to '
                    'point to a git repository where you have pushed your '
                    'changes')

    _remove_source_files(appendlayerdir, remove_files, destpath, no_report_remove, dry_run=dry_run_outdir)
    return True, appendfile, remove_files

def _update_recipe_patch(recipename, workspace, srctree, rd, appendlayerdir, wildcard_version, no_remove, no_report_remove, initial_rev, dry_run_outdir=None, force_patch_refresh=False):
    """Implement the 'patch' mode of update-recipe"""
    import bb
    import oe.recipeutils

    recipefile = rd.getVar('FILE')
    recipedir = os.path.dirname(recipefile)
    append = workspace[recipename]['bbappend']
    if not os.path.exists(append):
        raise DevtoolError('unable to find workspace bbappend for recipe %s' %
                           recipename)
    srctreebase = workspace[recipename]['srctreebase']
    relpatchdir = os.path.relpath(srctreebase, srctree)
    if relpatchdir == '.':
        patchdir_params = {}
    else:
        patchdir_params = {'patchdir': relpatchdir}

    def srcuri_entry(basepath, patchdir_params):
        if patchdir_params:
            paramstr = ';' + ';'.join('%s=%s' % (k,v) for k,v in patchdir_params.items())
        else:
            paramstr = ''
        return 'file://%s%s' % (basepath, paramstr)

    initial_revs, update_revs, changed_revs, filter_patches = _get_patchset_revs(srctree, append, initial_rev, force_patch_refresh)
    if not initial_revs:
        raise DevtoolError('Unable to find initial revision - please specify '
                           'it with --initial-rev')

    appendfile = None
    dl_dir = rd.getVar('DL_DIR')
    if not dl_dir.endswith('/'):
        dl_dir += '/'

    dry_run_suffix = ' (dry-run)' if dry_run_outdir else ''

    tempdir = tempfile.mkdtemp(prefix='devtool')
    try:
        local_files_dir = tempfile.mkdtemp(dir=tempdir)
        upd_f, new_f, del_f = _export_local_files(srctree, rd, local_files_dir, srctreebase)

        # Get updated patches from source tree
        patches_dir = tempfile.mkdtemp(dir=tempdir)
        upd_p, new_p, _ = _export_patches(srctree, rd, update_revs,
                                          patches_dir, changed_revs)
        # Get all patches from source tree and check if any should be removed
        all_patches_dir = tempfile.mkdtemp(dir=tempdir)
        _, _, del_p = _export_patches(srctree, rd, initial_revs,
                                      all_patches_dir)
        logger.debug('Pre-filtering: update: %s, new: %s' % (dict(upd_p), dict(new_p)))
        if filter_patches:
            new_p = OrderedDict()
            upd_p = OrderedDict((k,v) for k,v in upd_p.items() if k in filter_patches)
            del_p = OrderedDict((k,v) for k,v in del_p.items() if k in filter_patches)
        remove_files = []
        if not no_remove:
            # Remove deleted local files and  patches
            remove_files = list(del_f.values()) + list(del_p.values())
        updatefiles = False
        updaterecipe = False
        destpath = None
        srcuri = (rd.getVar('SRC_URI', False) or '').split()

        if appendlayerdir:
            files = OrderedDict((os.path.join(local_files_dir, key), val) for
                         key, val in list(upd_f.items()) + list(new_f.items()))
            files.update(OrderedDict((os.path.join(patches_dir, key), val) for
                              key, val in list(upd_p.items()) + list(new_p.items())))

            params = []
            for file, param in files.items():
                patchdir_param = dict(patchdir_params)
                patchdir = param.get('patchdir', ".")
                if patchdir != "." :
                    if patchdir_param:
                       patchdir_param['patchdir'] += patchdir
                    else:
                        patchdir_param['patchdir'] = patchdir
                params.append(patchdir_param)

            if files or remove_files:
                removevalues = None
                if remove_files:
                    removedentries, remaining = _remove_file_entries(
                                                    srcuri, remove_files)
                    if removedentries or remaining:
                        remaining = [srcuri_entry(os.path.basename(item), patchdir_params) for
                                     item in remaining]
                        removevalues = {'SRC_URI': removedentries + remaining}
                appendfile, destpath = oe.recipeutils.bbappend_recipe(
                                rd, appendlayerdir, files,
                                wildcardver=wildcard_version,
                                removevalues=removevalues,
                                redirect_output=dry_run_outdir,
                                params=params)
            else:
                logger.info('No patches or local source files needed updating')
        else:
            # Update existing files
            files_dir = _determine_files_dir(rd)
            for basepath, param in upd_f.items():
                path = param['path']
                logger.info('Updating file %s' % basepath)
                if os.path.isabs(basepath):
                    # Original file (probably with subdir pointing inside source tree)
                    # so we do not want to move it, just copy
                    _copy_file(basepath, path,
                               dry_run_outdir=dry_run_outdir, base_outdir=recipedir)
                else:
                    _move_file(os.path.join(local_files_dir, basepath), path,
                               dry_run_outdir=dry_run_outdir, base_outdir=recipedir)
                updatefiles = True
            for basepath, param in upd_p.items():
                path = param['path']
                patchdir = param.get('patchdir', ".")
                if patchdir != "." :
                    patchdir_param = dict(patchdir_params)
                    if patchdir_param:
                       patchdir_param['patchdir'] += patchdir
                    else:
                        patchdir_param['patchdir'] = patchdir
                patchfn = os.path.join(patches_dir, patchdir, basepath)
                if os.path.dirname(path) + '/' == dl_dir:
                    # This is a a downloaded patch file - we now need to
                    # replace the entry in SRC_URI with our local version
                    logger.info('Replacing remote patch %s with updated local version' % basepath)
                    path = os.path.join(files_dir, basepath)
                    _replace_srcuri_entry(srcuri, basepath, srcuri_entry(basepath, patchdir_param))
                    updaterecipe = True
                else:
                    logger.info('Updating patch %s%s' % (basepath, dry_run_suffix))
                _move_file(patchfn, path,
                           dry_run_outdir=dry_run_outdir, base_outdir=recipedir)
                updatefiles = True
            # Add any new files
            for basepath, param in new_f.items():
                logger.info('Adding new file %s%s' % (basepath, dry_run_suffix))
                _move_file(os.path.join(local_files_dir, basepath),
                           os.path.join(files_dir, basepath),
                           dry_run_outdir=dry_run_outdir,
                           base_outdir=recipedir)
                srcuri.append(srcuri_entry(basepath, patchdir_params))
                updaterecipe = True
            for basepath, param in new_p.items():
                patchdir = param.get('patchdir', ".")
                logger.info('Adding new patch %s%s' % (basepath, dry_run_suffix))
                _move_file(os.path.join(patches_dir, patchdir, basepath),
                           os.path.join(files_dir, basepath),
                           dry_run_outdir=dry_run_outdir,
                           base_outdir=recipedir)
                params = dict(patchdir_params)
                if patchdir != "." :
                    if params:
                       params['patchdir'] += patchdir
                    else:
                        params['patchdir'] = patchdir

                srcuri.append(srcuri_entry(basepath, params))
                updaterecipe = True
            # Update recipe, if needed
            if _remove_file_entries(srcuri, remove_files)[0]:
                updaterecipe = True
            if updaterecipe:
                if not dry_run_outdir:
                    logger.info('Updating recipe %s' % os.path.basename(recipefile))
                ret = oe.recipeutils.patch_recipe(rd, recipefile,
                                                  {'SRC_URI': ' '.join(srcuri)},
                                                  redirect_output=dry_run_outdir)
            elif not updatefiles:
                # Neither patches nor recipe were updated
                logger.info('No patches or files need updating')
                return False, None, []
    finally:
        shutil.rmtree(tempdir)

    _remove_source_files(appendlayerdir, remove_files, destpath, no_report_remove, dry_run=dry_run_outdir)
    return True, appendfile, remove_files

def _guess_recipe_update_mode(srctree, rdata):
    """Guess the recipe update mode to use"""
    src_uri = (rdata.getVar('SRC_URI') or '').split()
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

def _update_recipe(recipename, workspace, rd, mode, appendlayerdir, wildcard_version, no_remove, initial_rev, no_report_remove=False, dry_run_outdir=None, no_overrides=False, force_patch_refresh=False):
    srctree = workspace[recipename]['srctree']
    if mode == 'auto':
        mode = _guess_recipe_update_mode(srctree, rd)

    override_branches = []
    mainbranch = None
    startbranch = None
    if not no_overrides:
        stdout, _ = bb.process.run('git branch', cwd=srctree)
        other_branches = []
        for line in stdout.splitlines():
            branchname = line[2:]
            if line.startswith('* '):
                if 'HEAD' in line:
                    raise DevtoolError('Detached HEAD - please check out a branch, e.g., "devtool"')
                startbranch = branchname
            if branchname.startswith(override_branch_prefix):
                override_branches.append(branchname)
            else:
                other_branches.append(branchname)

        if override_branches:
            logger.debug('_update_recipe: override branches: %s' % override_branches)
            logger.debug('_update_recipe: other branches: %s' % other_branches)
            if startbranch.startswith(override_branch_prefix):
                if len(other_branches) == 1:
                    mainbranch = other_branches[1]
                else:
                    raise DevtoolError('Unable to determine main branch - please check out the main branch in source tree first')
            else:
                mainbranch = startbranch

    checkedout = None
    anyupdated = False
    appendfile = None
    allremoved = []
    if override_branches:
        logger.info('Handling main branch (%s)...' % mainbranch)
        if startbranch != mainbranch:
            bb.process.run('git checkout %s' % mainbranch, cwd=srctree)
        checkedout = mainbranch
    try:
        branchlist = [mainbranch] + override_branches
        for branch in branchlist:
            crd = bb.data.createCopy(rd)
            if branch != mainbranch:
                logger.info('Handling branch %s...' % branch)
                override = branch[len(override_branch_prefix):]
                crd.appendVar('OVERRIDES', ':%s' % override)
                bb.process.run('git checkout %s' % branch, cwd=srctree)
                checkedout = branch

            if mode == 'srcrev':
                updated, appendf, removed = _update_recipe_srcrev(recipename, workspace, srctree, crd, appendlayerdir, wildcard_version, no_remove, no_report_remove, dry_run_outdir)
            elif mode == 'patch':
                updated, appendf, removed = _update_recipe_patch(recipename, workspace, srctree, crd, appendlayerdir, wildcard_version, no_remove, no_report_remove, initial_rev, dry_run_outdir, force_patch_refresh)
            else:
                raise DevtoolError('update_recipe: invalid mode %s' % mode)
            if updated:
                anyupdated = True
            if appendf:
                appendfile = appendf
            allremoved.extend(removed)
    finally:
        if startbranch and checkedout != startbranch:
            bb.process.run('git checkout %s' % startbranch, cwd=srctree)

    return anyupdated, appendfile, allremoved

def update_recipe(args, config, basepath, workspace):
    """Entry point for the devtool 'update-recipe' subcommand"""
    check_workspace_recipe(workspace, args.recipename)

    if args.append:
        if not os.path.exists(args.append):
            raise DevtoolError('bbappend destination layer directory "%s" '
                               'does not exist' % args.append)
        if not os.path.exists(os.path.join(args.append, 'conf', 'layer.conf')):
            raise DevtoolError('conf/layer.conf not found in bbappend '
                               'destination layer "%s"' % args.append)

    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:

        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        dry_run_output = None
        dry_run_outdir = None
        if args.dry_run:
            dry_run_output = tempfile.TemporaryDirectory(prefix='devtool')
            dry_run_outdir = dry_run_output.name
        updated, _, _ = _update_recipe(args.recipename, workspace, rd, args.mode, args.append, args.wildcard_version, args.no_remove, args.initial_rev, dry_run_outdir=dry_run_outdir, no_overrides=args.no_overrides, force_patch_refresh=args.force_patch_refresh)

        if updated:
            rf = rd.getVar('FILE')
            if rf.startswith(config.workspace_path):
                logger.warning('Recipe file %s has been updated but is inside the workspace - you will need to move it (and any associated files next to it) out to the desired layer before using "devtool reset" in order to keep any changes' % rf)
    finally:
        tinfoil.shutdown()

    return 0


def status(args, config, basepath, workspace):
    """Entry point for the devtool 'status' subcommand"""
    if workspace:
        for recipe, value in sorted(workspace.items()):
            recipefile = value['recipefile']
            if recipefile:
                recipestr = ' (%s)' % recipefile
            else:
                recipestr = ''
            print("%s: %s%s" % (recipe, value['srctree'], recipestr))
    else:
        logger.info('No recipes currently in your workspace - you can use "devtool modify" to work on an existing recipe or "devtool add" to add a new one')
    return 0


def _reset(recipes, no_clean, remove_work, config, basepath, workspace):
    """Reset one or more recipes"""
    import oe.path

    def clean_preferred_provider(pn, layerconf_path):
        """Remove PREFERRED_PROVIDER from layer.conf'"""
        import re
        layerconf_file = os.path.join(layerconf_path, 'conf', 'layer.conf')
        new_layerconf_file = os.path.join(layerconf_path, 'conf', '.layer.conf')
        pprovider_found = False
        with open(layerconf_file, 'r') as f:
            lines = f.readlines()
            with open(new_layerconf_file, 'a') as nf:
                for line in lines:
                    pprovider_exp = r'^PREFERRED_PROVIDER_.*? = "' + pn + r'"$'
                    if not re.match(pprovider_exp, line):
                        nf.write(line)
                    else:
                        pprovider_found = True
        if pprovider_found:
            shutil.move(new_layerconf_file, layerconf_file)
        else:
            os.remove(new_layerconf_file)

    if recipes and not no_clean:
        if len(recipes) == 1:
            logger.info('Cleaning sysroot for recipe %s...' % recipes[0])
        else:
            logger.info('Cleaning sysroot for recipes %s...' % ', '.join(recipes))
        # If the recipe file itself was created in the workspace, and
        # it uses BBCLASSEXTEND, then we need to also clean the other
        # variants
        targets = []
        for recipe in recipes:
            targets.append(recipe)
            recipefile = workspace[recipe]['recipefile']
            if recipefile and os.path.exists(recipefile):
                targets.extend(get_bbclassextend_targets(recipefile, recipe))
        try:
            exec_build_env_command(config.init_path, basepath, 'bitbake -c clean %s' % ' '.join(targets))
        except bb.process.ExecutionError as e:
            raise DevtoolError('Command \'%s\' failed, output:\n%s\nIf you '
                                'wish, you may specify -n/--no-clean to '
                                'skip running this command when resetting' %
                                (e.command, e.stdout))

    for pn in recipes:
        _check_preserve(config, pn)

        appendfile = workspace[pn]['bbappend']
        if os.path.exists(appendfile):
            # This shouldn't happen, but is possible if devtool errored out prior to
            # writing the md5 file. We need to delete this here or the recipe won't
            # actually be reset
            os.remove(appendfile)

        preservepath = os.path.join(config.workspace_path, 'attic', pn, pn)
        def preservedir(origdir):
            if os.path.exists(origdir):
                for root, dirs, files in os.walk(origdir):
                    for fn in files:
                        logger.warning('Preserving %s in %s' % (fn, preservepath))
                        _move_file(os.path.join(origdir, fn),
                                   os.path.join(preservepath, fn))
                    for dn in dirs:
                        preservedir(os.path.join(root, dn))
                os.rmdir(origdir)

        recipefile = workspace[pn]['recipefile']
        if recipefile and oe.path.is_path_parent(config.workspace_path, recipefile):
            # This should always be true if recipefile is set, but just in case
            preservedir(os.path.dirname(recipefile))
        # We don't automatically create this dir next to appends, but the user can
        preservedir(os.path.join(config.workspace_path, 'appends', pn))

        srctreebase = workspace[pn]['srctreebase']
        if os.path.isdir(srctreebase):
            if os.listdir(srctreebase):
                    if remove_work:
                        logger.info('-r argument used on %s, removing source tree.'
                                    ' You will lose any unsaved work' %pn)
                        shutil.rmtree(srctreebase)
                    else:
                        # We don't want to risk wiping out any work in progress
                        if srctreebase.startswith(os.path.join(config.workspace_path, 'sources')):
                            from datetime import datetime
                            preservesrc = os.path.join(config.workspace_path, 'attic', 'sources', "{}.{}".format(pn, datetime.now().strftime("%Y%m%d%H%M%S")))
                            logger.info('Preserving source tree in %s\nIf you no '
                                        'longer need it then please delete it manually.\n'
                                        'It is also possible to reuse it via devtool source tree argument.'
                                        % preservesrc)
                            bb.utils.mkdirhier(os.path.dirname(preservesrc))
                            shutil.move(srctreebase, preservesrc)
                        else:
                            logger.info('Leaving source tree %s as-is; if you no '
                                        'longer need it then please delete it manually'
                                        % srctreebase)
            else:
                # This is unlikely, but if it's empty we can just remove it
                os.rmdir(srctreebase)

        clean_preferred_provider(pn, config.workspace_path)

def reset(args, config, basepath, workspace):
    """Entry point for the devtool 'reset' subcommand"""
    import bb
    import shutil

    recipes = ""

    if args.recipename:
        if args.all:
            raise DevtoolError("Recipe cannot be specified if -a/--all is used")
        else:
            for recipe in args.recipename:
                check_workspace_recipe(workspace, recipe, checksrc=False)
    elif not args.all:
        raise DevtoolError("Recipe must be specified, or specify -a/--all to "
                           "reset all recipes")
    if args.all:
        recipes = list(workspace.keys())
    else:
        recipes = args.recipename

    _reset(recipes, args.no_clean, args.remove_work, config, basepath, workspace)

    return 0


def _get_layer(layername, d):
    """Determine the base layer path for the specified layer name/path"""
    layerdirs = d.getVar('BBLAYERS').split()
    layers = {}    # {basename: layer_paths}
    for p in layerdirs:
        bn = os.path.basename(p)
        if bn not in layers:
            layers[bn] = [p]
        else:
            layers[bn].append(p)
    # Provide some shortcuts
    if layername.lower() in ['oe-core', 'openembedded-core']:
        layername = 'meta'
    layer_paths = layers.get(layername, None)
    if not layer_paths:
        return os.path.abspath(layername)
    elif len(layer_paths) == 1:
        return os.path.abspath(layer_paths[0])
    else:
        # multiple layers having the same base name
        logger.warning("Multiple layers have the same base name '%s', use the first one '%s'." % (layername, layer_paths[0]))
        logger.warning("Consider using path instead of base name to specify layer:\n\t\t%s" % '\n\t\t'.join(layer_paths))
        return os.path.abspath(layer_paths[0])


def finish(args, config, basepath, workspace):
    """Entry point for the devtool 'finish' subcommand"""
    import bb
    import oe.recipeutils

    check_workspace_recipe(workspace, args.recipename)

    dry_run_suffix = ' (dry-run)' if args.dry_run else ''

    # Grab the equivalent of COREBASE without having to initialise tinfoil
    corebasedir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..'))

    srctree = workspace[args.recipename]['srctree']
    check_git_repo_op(srctree, [corebasedir])
    dirty = check_git_repo_dirty(srctree)
    if dirty:
        if args.force:
            logger.warning('Source tree is not clean, continuing as requested by -f/--force')
        else:
            raise DevtoolError('Source tree is not clean:\n\n%s\nEnsure you have committed your changes or use -f/--force if you are sure there\'s nothing that needs to be committed' % dirty)

    no_clean = args.no_clean
    remove_work=args.remove_work
    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        destlayerdir = _get_layer(args.destination, tinfoil.config_data)
        recipefile = rd.getVar('FILE')
        recipedir = os.path.dirname(recipefile)
        origlayerdir = oe.recipeutils.find_layerdir(recipefile)

        if not os.path.isdir(destlayerdir):
            raise DevtoolError('Unable to find layer or directory matching "%s"' % args.destination)

        if os.path.abspath(destlayerdir) == config.workspace_path:
            raise DevtoolError('"%s" specifies the workspace layer - that is not a valid destination' % args.destination)

        # If it's an upgrade, grab the original path
        origpath = None
        origfilelist = None
        append = workspace[args.recipename]['bbappend']
        with open(append, 'r') as f:
            for line in f:
                if line.startswith('# original_path:'):
                    origpath = line.split(':')[1].strip()
                elif line.startswith('# original_files:'):
                    origfilelist = line.split(':')[1].split()

        destlayerbasedir = oe.recipeutils.find_layerdir(destlayerdir)

        if origlayerdir == config.workspace_path:
            # Recipe file itself is in workspace, update it there first
            appendlayerdir = None
            origrelpath = None
            if origpath:
                origlayerpath = oe.recipeutils.find_layerdir(origpath)
                if origlayerpath:
                    origrelpath = os.path.relpath(origpath, origlayerpath)
            destpath = oe.recipeutils.get_bbfile_path(rd, destlayerdir, origrelpath)
            if not destpath:
                raise DevtoolError("Unable to determine destination layer path - check that %s specifies an actual layer and %s/conf/layer.conf specifies BBFILES. You may also need to specify a more complete path." % (args.destination, destlayerdir))
            # Warn if the layer isn't in bblayers.conf (the code to create a bbappend will do this in other cases)
            layerdirs = [os.path.abspath(layerdir) for layerdir in rd.getVar('BBLAYERS').split()]
            if not os.path.abspath(destlayerbasedir) in layerdirs:
                bb.warn('Specified destination layer is not currently enabled in bblayers.conf, so the %s recipe will now be unavailable in your current configuration until you add the layer there' % args.recipename)

        elif destlayerdir == origlayerdir:
            # Same layer, update the original recipe
            appendlayerdir = None
            destpath = None
        else:
            # Create/update a bbappend in the specified layer
            appendlayerdir = destlayerdir
            destpath = None

        # Actually update the recipe / bbappend
        removing_original = (origpath and origfilelist and oe.recipeutils.find_layerdir(origpath) == destlayerbasedir)
        dry_run_output = None
        dry_run_outdir = None
        if args.dry_run:
            dry_run_output = tempfile.TemporaryDirectory(prefix='devtool')
            dry_run_outdir = dry_run_output.name
        updated, appendfile, removed = _update_recipe(args.recipename, workspace, rd, args.mode, appendlayerdir, wildcard_version=True, no_remove=False, no_report_remove=removing_original, initial_rev=args.initial_rev, dry_run_outdir=dry_run_outdir, no_overrides=args.no_overrides, force_patch_refresh=args.force_patch_refresh)
        removed = [os.path.relpath(pth, recipedir) for pth in removed]

        # Remove any old files in the case of an upgrade
        if removing_original:
            for fn in origfilelist:
                fnp = os.path.join(origpath, fn)
                if fn in removed or not os.path.exists(os.path.join(recipedir, fn)):
                    logger.info('Removing file %s%s' % (fnp, dry_run_suffix))
                if not args.dry_run:
                    try:
                        os.remove(fnp)
                    except FileNotFoundError:
                        pass

        if origlayerdir == config.workspace_path and destpath:
            # Recipe file itself is in the workspace - need to move it and any
            # associated files to the specified layer
            no_clean = True
            logger.info('Moving recipe file to %s%s' % (destpath, dry_run_suffix))
            for root, _, files in os.walk(recipedir):
                for fn in files:
                    srcpath = os.path.join(root, fn)
                    relpth = os.path.relpath(os.path.dirname(srcpath), recipedir)
                    destdir = os.path.abspath(os.path.join(destpath, relpth))
                    destfp = os.path.join(destdir, fn)
                    _move_file(srcpath, destfp, dry_run_outdir=dry_run_outdir, base_outdir=destpath)

        if dry_run_outdir:
            import difflib
            comparelist = []
            for root, _, files in os.walk(dry_run_outdir):
                for fn in files:
                    outf = os.path.join(root, fn)
                    relf = os.path.relpath(outf, dry_run_outdir)
                    logger.debug('dry-run: output file %s' % relf)
                    if fn.endswith('.bb'):
                        if origfilelist and origpath and destpath:
                            # Need to match this up with the pre-upgrade recipe file
                            for origf in origfilelist:
                                if origf.endswith('.bb'):
                                    comparelist.append((os.path.abspath(os.path.join(origpath, origf)),
                                                        outf,
                                                        os.path.abspath(os.path.join(destpath, relf))))
                                    break
                        else:
                            # Compare to the existing recipe
                            comparelist.append((recipefile, outf, recipefile))
                    elif fn.endswith('.bbappend'):
                        if appendfile:
                            if os.path.exists(appendfile):
                                comparelist.append((appendfile, outf, appendfile))
                            else:
                                comparelist.append((None, outf, appendfile))
                    else:
                        if destpath:
                            recipedest = destpath
                        elif appendfile:
                            recipedest = os.path.dirname(appendfile)
                        else:
                            recipedest = os.path.dirname(recipefile)
                        destfp = os.path.join(recipedest, relf)
                        if os.path.exists(destfp):
                            comparelist.append((destfp, outf, destfp))
            output = ''
            for oldfile, newfile, newfileshow in comparelist:
                if oldfile:
                    with open(oldfile, 'r') as f:
                        oldlines = f.readlines()
                else:
                    oldfile = '/dev/null'
                    oldlines = []
                with open(newfile, 'r') as f:
                    newlines = f.readlines()
                if not newfileshow:
                    newfileshow = newfile
                diff = difflib.unified_diff(oldlines, newlines, oldfile, newfileshow)
                difflines = list(diff)
                if difflines:
                    output += ''.join(difflines)
            if output:
                logger.info('Diff of changed files:\n%s' % output)
    finally:
        tinfoil.shutdown()

    # Everything else has succeeded, we can now reset
    if args.dry_run:
        logger.info('Resetting recipe (dry-run)')
    else:
        _reset([args.recipename], no_clean=no_clean, remove_work=remove_work, config=config, basepath=basepath, workspace=workspace)

    return 0


def get_default_srctree(config, recipename=''):
    """Get the default srctree path"""
    srctreeparent = config.get('General', 'default_source_parent_dir', config.workspace_path)
    if recipename:
        return os.path.join(srctreeparent, 'sources', recipename)
    else:
        return os.path.join(srctreeparent, 'sources')

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""

    defsrctree = get_default_srctree(context.config)
    parser_add = subparsers.add_parser('add', help='Add a new recipe',
                                       description='Adds a new recipe to the workspace to build a specified source tree. Can optionally fetch a remote URI and unpack it to create the source tree.',
                                       group='starting', order=100)
    parser_add.add_argument('recipename', nargs='?', help='Name for new recipe to add (just name - no version, path or extension). If not specified, will attempt to auto-detect it.')
    parser_add.add_argument('srctree', nargs='?', help='Path to external source tree. If not specified, a subdirectory of %s will be used.' % defsrctree)
    parser_add.add_argument('fetchuri', nargs='?', help='Fetch the specified URI and extract it to create the source tree')
    group = parser_add.add_mutually_exclusive_group()
    group.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    group.add_argument('--no-same-dir', help='Force build in a separate build directory', action="store_true")
    parser_add.add_argument('--fetch', '-f', help='Fetch the specified URI and extract it to create the source tree (deprecated - pass as positional argument instead)', metavar='URI')
    parser_add.add_argument('--npm-dev', help='For npm, also fetch devDependencies', action="store_true")
    parser_add.add_argument('--no-pypi', help='Do not inherit pypi class', action="store_true")
    parser_add.add_argument('--version', '-V', help='Version to use within recipe (PV)')
    parser_add.add_argument('--no-git', '-g', help='If fetching source, do not set up source tree as a git repository', action="store_true")
    group = parser_add.add_mutually_exclusive_group()
    group.add_argument('--srcrev', '-S', help='Source revision to fetch if fetching from an SCM such as git (default latest)')
    group.add_argument('--autorev', '-a', help='When fetching from a git repository, set SRCREV in the recipe to a floating revision instead of fixed', action="store_true")
    parser_add.add_argument('--srcbranch', '-B', help='Branch in source repository if fetching from an SCM such as git (default master)')
    parser_add.add_argument('--binary', '-b', help='Treat the source tree as something that should be installed verbatim (no compilation, same directory structure). Useful with binary packages e.g. RPMs.', action='store_true')
    parser_add.add_argument('--also-native', help='Also add native variant (i.e. support building recipe for the build host as well as the target machine)', action='store_true')
    parser_add.add_argument('--src-subdir', help='Specify subdirectory within source tree to use', metavar='SUBDIR')
    parser_add.add_argument('--mirrors', help='Enable PREMIRRORS and MIRRORS for source tree fetching (disable by default).', action="store_true")
    parser_add.add_argument('--provides', '-p', help='Specify an alias for the item provided by the recipe. E.g. virtual/libgl')
    parser_add.set_defaults(func=add, fixed_setup=context.fixed_setup)

    parser_modify = subparsers.add_parser('modify', help='Modify the source for an existing recipe',
                                       description='Sets up the build environment to modify the source for an existing recipe. The default behaviour is to extract the source being fetched by the recipe into a git tree so you can work on it; alternatively if you already have your own pre-prepared source tree you can specify -n/--no-extract.',
                                       group='starting', order=90)
    parser_modify.add_argument('recipename', help='Name of existing recipe to edit (just name - no version, path or extension)')
    parser_modify.add_argument('srctree', nargs='?', help='Path to external source tree. If not specified, a subdirectory of %s will be used.' % defsrctree)
    parser_modify.add_argument('--wildcard', '-w', action="store_true", help='Use wildcard for unversioned bbappend')
    group = parser_modify.add_mutually_exclusive_group()
    group.add_argument('--extract', '-x', action="store_true", help='Extract source for recipe (default)')
    group.add_argument('--no-extract', '-n', action="store_true", help='Do not extract source, expect it to exist')
    group = parser_modify.add_mutually_exclusive_group()
    group.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    group.add_argument('--no-same-dir', help='Force build in a separate build directory', action="store_true")
    parser_modify.add_argument('--branch', '-b', default="devtool", help='Name for development branch to checkout (when not using -n/--no-extract) (default "%(default)s")')
    parser_modify.add_argument('--no-overrides', '-O', action="store_true", help='Do not create branches for other override configurations')
    parser_modify.add_argument('--keep-temp', help='Keep temporary directory (for debugging)', action="store_true")
    parser_modify.set_defaults(func=modify, fixed_setup=context.fixed_setup)

    parser_extract = subparsers.add_parser('extract', help='Extract the source for an existing recipe',
                                       description='Extracts the source for an existing recipe',
                                       group='advanced')
    parser_extract.add_argument('recipename', help='Name of recipe to extract the source for')
    parser_extract.add_argument('srctree', help='Path to where to extract the source tree')
    parser_extract.add_argument('--branch', '-b', default="devtool", help='Name for development branch to checkout (default "%(default)s")')
    parser_extract.add_argument('--no-overrides', '-O', action="store_true", help='Do not create branches for other override configurations')
    parser_extract.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_extract.set_defaults(func=extract, fixed_setup=context.fixed_setup)

    parser_sync = subparsers.add_parser('sync', help='Synchronize the source tree for an existing recipe',
                                       description='Synchronize the previously extracted source tree for an existing recipe',
                                       formatter_class=argparse.ArgumentDefaultsHelpFormatter,
                                       group='advanced')
    parser_sync.add_argument('recipename', help='Name of recipe to sync the source for')
    parser_sync.add_argument('srctree', help='Path to the source tree')
    parser_sync.add_argument('--branch', '-b', default="devtool", help='Name for development branch to checkout')
    parser_sync.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_sync.set_defaults(func=sync, fixed_setup=context.fixed_setup)

    parser_rename = subparsers.add_parser('rename', help='Rename a recipe file in the workspace',
                                       description='Renames the recipe file for a recipe in the workspace, changing the name or version part or both, ensuring that all references within the workspace are updated at the same time. Only works when the recipe file itself is in the workspace, e.g. after devtool add. Particularly useful when devtool add did not automatically determine the correct name.',
                                       group='working', order=10)
    parser_rename.add_argument('recipename', help='Current name of recipe to rename')
    parser_rename.add_argument('newname', nargs='?', help='New name for recipe (optional, not needed if you only want to change the version)')
    parser_rename.add_argument('--version', '-V', help='Change the version (NOTE: this does not change the version fetched by the recipe, just the version in the recipe file name)')
    parser_rename.add_argument('--no-srctree', '-s', action='store_true', help='Do not rename the source tree directory (if the default source tree path has been used) - keeping the old name may be desirable if there are internal/other external references to this path')
    parser_rename.set_defaults(func=rename)

    parser_update_recipe = subparsers.add_parser('update-recipe', help='Apply changes from external source tree to recipe',
                                       description='Applies changes from external source tree to a recipe (updating/adding/removing patches as necessary, or by updating SRCREV). Note that these changes need to have been committed to the git repository in order to be recognised.',
                                       group='working', order=-90)
    parser_update_recipe.add_argument('recipename', help='Name of recipe to update')
    parser_update_recipe.add_argument('--mode', '-m', choices=['patch', 'srcrev', 'auto'], default='auto', help='Update mode (where %(metavar)s is %(choices)s; default is %(default)s)', metavar='MODE')
    parser_update_recipe.add_argument('--initial-rev', help='Override starting revision for patches')
    parser_update_recipe.add_argument('--append', '-a', help='Write changes to a bbappend in the specified layer instead of the recipe', metavar='LAYERDIR')
    parser_update_recipe.add_argument('--wildcard-version', '-w', help='In conjunction with -a/--append, use a wildcard to make the bbappend apply to any recipe version', action='store_true')
    parser_update_recipe.add_argument('--no-remove', '-n', action="store_true", help='Don\'t remove patches, only add or update')
    parser_update_recipe.add_argument('--no-overrides', '-O', action="store_true", help='Do not handle other override branches (if they exist)')
    parser_update_recipe.add_argument('--dry-run', '-N', action="store_true", help='Dry-run (just report changes instead of writing them)')
    parser_update_recipe.add_argument('--force-patch-refresh', action="store_true", help='Update patches in the layer even if they have not been modified (useful for refreshing patch context)')
    parser_update_recipe.set_defaults(func=update_recipe)

    parser_status = subparsers.add_parser('status', help='Show workspace status',
                                          description='Lists recipes currently in your workspace and the paths to their respective external source trees',
                                          group='info', order=100)
    parser_status.set_defaults(func=status)

    parser_reset = subparsers.add_parser('reset', help='Remove a recipe from your workspace',
                                         description='Removes the specified recipe(s) from your workspace (resetting its state back to that defined by the metadata).',
                                         group='working', order=-100)
    parser_reset.add_argument('recipename', nargs='*', help='Recipe to reset')
    parser_reset.add_argument('--all', '-a', action="store_true", help='Reset all recipes (clear workspace)')
    parser_reset.add_argument('--no-clean', '-n', action="store_true", help='Don\'t clean the sysroot to remove recipe output')
    parser_reset.add_argument('--remove-work', '-r', action="store_true", help='Clean the sources directory along with append')
    parser_reset.set_defaults(func=reset)

    parser_finish = subparsers.add_parser('finish', help='Finish working on a recipe in your workspace',
                                         description='Pushes any committed changes to the specified recipe to the specified layer and removes it from your workspace. Roughly equivalent to an update-recipe followed by reset, except the update-recipe step will do the "right thing" depending on the recipe and the destination layer specified. Note that your changes must have been committed to the git repository in order to be recognised.',
                                         group='working', order=-100)
    parser_finish.add_argument('recipename', help='Recipe to finish')
    parser_finish.add_argument('destination', help='Layer/path to put recipe into. Can be the name of a layer configured in your bblayers.conf, the path to the base of a layer, or a partial path inside a layer. %(prog)s will attempt to complete the path based on the layer\'s structure.')
    parser_finish.add_argument('--mode', '-m', choices=['patch', 'srcrev', 'auto'], default='auto', help='Update mode (where %(metavar)s is %(choices)s; default is %(default)s)', metavar='MODE')
    parser_finish.add_argument('--initial-rev', help='Override starting revision for patches')
    parser_finish.add_argument('--force', '-f', action="store_true", help='Force continuing even if there are uncommitted changes in the source tree repository')
    parser_finish.add_argument('--remove-work', '-r', action="store_true", help='Clean the sources directory under workspace')
    parser_finish.add_argument('--no-clean', '-n', action="store_true", help='Don\'t clean the sysroot to remove recipe output')
    parser_finish.add_argument('--no-overrides', '-O', action="store_true", help='Do not handle other override branches (if they exist)')
    parser_finish.add_argument('--dry-run', '-N', action="store_true", help='Dry-run (just report changes instead of writing them)')
    parser_finish.add_argument('--force-patch-refresh', action="store_true", help='Update patches in the layer even if they have not been modified (useful for refreshing patch context)')
    parser_finish.set_defaults(func=finish)
