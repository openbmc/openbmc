# Development tool - upgrade command plugin
#
# Copyright (C) 2014-2017 Intel Corporation
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
#
"""Devtool upgrade plugin"""

import os
import sys
import re
import shutil
import tempfile
import logging
import argparse
import scriptutils
import errno
import bb

devtool_path = os.path.dirname(os.path.realpath(__file__)) + '/../../../meta/lib'
sys.path = sys.path + [devtool_path]

import oe.recipeutils
from devtool import standard
from devtool import exec_build_env_command, setup_tinfoil, DevtoolError, parse_recipe, use_external_build, update_unlockedsigs

logger = logging.getLogger('devtool')

def _run(cmd, cwd=''):
    logger.debug("Running command %s> %s" % (cwd,cmd))
    return bb.process.run('%s' % cmd, cwd=cwd)

def _get_srctree(tmpdir):
    srctree = tmpdir
    dirs = os.listdir(tmpdir)
    if len(dirs) == 1:
        srctree = os.path.join(tmpdir, dirs[0])
    return srctree

def _copy_source_code(orig, dest):
    for path in standard._ls_tree(orig):
        dest_dir = os.path.join(dest, os.path.dirname(path))
        bb.utils.mkdirhier(dest_dir)
        dest_path = os.path.join(dest, path)
        shutil.move(os.path.join(orig, path), dest_path)

def _get_checksums(rf):
    import re
    checksums = {}
    with open(rf) as f:
        for line in f:
            for cs in ['md5sum', 'sha256sum']:
                m = re.match("^SRC_URI\[%s\].*=.*\"(.*)\"" % cs, line)
                if m:
                    checksums[cs] = m.group(1)
    return checksums

def _remove_patch_dirs(recipefolder):
    for root, dirs, files in os.walk(recipefolder):
        for d in dirs:
            shutil.rmtree(os.path.join(root,d))

def _recipe_contains(rd, var):
    rf = rd.getVar('FILE')
    varfiles = oe.recipeutils.get_var_files(rf, [var], rd)
    for var, fn in varfiles.items():
        if fn and fn.startswith(os.path.dirname(rf) + os.sep):
            return True
    return False

def _rename_recipe_dirs(oldpv, newpv, path):
    for root, dirs, files in os.walk(path):
        # Rename directories with the version in their name
        for olddir in dirs:
            if olddir.find(oldpv) != -1:
                newdir = olddir.replace(oldpv, newpv)
                if olddir != newdir:
                    shutil.move(os.path.join(path, olddir), os.path.join(path, newdir))
        # Rename any inc files with the version in their name (unusual, but possible)
        for oldfile in files:
            if oldfile.endswith('.inc'):
                if oldfile.find(oldpv) != -1:
                    newfile = oldfile.replace(oldpv, newpv)
                    if oldfile != newfile:
                        os.rename(os.path.join(path, oldfile), os.path.join(path, newfile))

def _rename_recipe_file(oldrecipe, bpn, oldpv, newpv, path):
    oldrecipe = os.path.basename(oldrecipe)
    if oldrecipe.endswith('_%s.bb' % oldpv):
        newrecipe = '%s_%s.bb' % (bpn, newpv)
        if oldrecipe != newrecipe:
            shutil.move(os.path.join(path, oldrecipe), os.path.join(path, newrecipe))
    else:
        newrecipe = oldrecipe
    return os.path.join(path, newrecipe)

def _rename_recipe_files(oldrecipe, bpn, oldpv, newpv, path):
    _rename_recipe_dirs(oldpv, newpv, path)
    return _rename_recipe_file(oldrecipe, bpn, oldpv, newpv, path)

def _write_append(rc, srctree, same_dir, no_same_dir, rev, copied, workspace, d):
    """Writes an append file"""
    if not os.path.exists(rc):
        raise DevtoolError("bbappend not created because %s does not exist" % rc)

    appendpath = os.path.join(workspace, 'appends')
    if not os.path.exists(appendpath):
        bb.utils.mkdirhier(appendpath)

    brf = os.path.basename(os.path.splitext(rc)[0]) # rc basename

    srctree = os.path.abspath(srctree)
    pn = d.getVar('PN')
    af = os.path.join(appendpath, '%s.bbappend' % brf)
    with open(af, 'w') as f:
        f.write('FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n\n')
        f.write('inherit externalsrc\n')
        f.write(('# NOTE: We use pn- overrides here to avoid affecting'
                 'multiple variants in the case where the recipe uses BBCLASSEXTEND\n'))
        f.write('EXTERNALSRC_pn-%s = "%s"\n' % (pn, srctree))
        b_is_s = use_external_build(same_dir, no_same_dir, d)
        if b_is_s:
            f.write('EXTERNALSRC_BUILD_pn-%s = "%s"\n' % (pn, srctree))
        f.write('\n')
        if rev:
            f.write('# initial_rev: %s\n' % rev)
        if copied:
            f.write('# original_path: %s\n' % os.path.dirname(d.getVar('FILE')))
            f.write('# original_files: %s\n' % ' '.join(copied))
    return af

def _cleanup_on_error(rf, srctree):
    rfp = os.path.split(rf)[0] # recipe folder
    rfpp = os.path.split(rfp)[0] # recipes folder
    if os.path.exists(rfp):
        shutil.rmtree(b)
    if not len(os.listdir(rfpp)):
        os.rmdir(rfpp)
    srctree = os.path.abspath(srctree)
    if os.path.exists(srctree):
        shutil.rmtree(srctree)

def _upgrade_error(e, rf, srctree):
    if rf:
        cleanup_on_error(rf, srctree)
    logger.error(e)
    raise DevtoolError(e)

def _get_uri(rd):
    srcuris = rd.getVar('SRC_URI').split()
    if not len(srcuris):
        raise DevtoolError('SRC_URI not found on recipe')
    # Get first non-local entry in SRC_URI - usually by convention it's
    # the first entry, but not always!
    srcuri = None
    for entry in srcuris:
        if not entry.startswith('file://'):
            srcuri = entry
            break
    if not srcuri:
        raise DevtoolError('Unable to find non-local entry in SRC_URI')
    srcrev = '${AUTOREV}'
    if '://' in srcuri:
        # Fetch a URL
        rev_re = re.compile(';rev=([^;]+)')
        res = rev_re.search(srcuri)
        if res:
            srcrev = res.group(1)
            srcuri = rev_re.sub('', srcuri)
    return srcuri, srcrev

def _extract_new_source(newpv, srctree, no_patch, srcrev, srcbranch, branch, keep_temp, tinfoil, rd):
    """Extract sources of a recipe with a new version"""

    def __run(cmd):
        """Simple wrapper which calls _run with srctree as cwd"""
        return _run(cmd, srctree)

    crd = rd.createCopy()

    pv = crd.getVar('PV')
    crd.setVar('PV', newpv)

    tmpsrctree = None
    uri, rev = _get_uri(crd)
    if srcrev:
        rev = srcrev
    if uri.startswith('git://'):
        __run('git fetch')
        __run('git checkout %s' % rev)
        __run('git tag -f devtool-base-new')
        md5 = None
        sha256 = None
        if not srcbranch:
            check_branch, check_branch_err = __run('git branch -r --contains %s' % srcrev)
            get_branch = [x.strip() for x in check_branch.splitlines()]
            # Remove HEAD reference point and drop remote prefix
            get_branch = [x.split('/', 1)[1] for x in get_branch if not x.startswith('origin/HEAD')]
            if 'master' in get_branch:
                # If it is master, we do not need to append 'branch=master' as this is default.
                # Even with the case where get_branch has multiple objects, if 'master' is one
                # of them, we should default take from 'master'
                srcbranch = ''
            elif len(get_branch) == 1:
                # If 'master' isn't in get_branch and get_branch contains only ONE object, then store result into 'srcbranch'
                srcbranch = get_branch[0]
            else:
                # If get_branch contains more than one objects, then display error and exit.
                mbrch = '\n  ' + '\n  '.join(get_branch)
                raise DevtoolError('Revision %s was found on multiple branches: %s\nPlease provide the correct branch in the devtool command with "--srcbranch" or "-B" option.' % (srcrev, mbrch))
    else:
        __run('git checkout devtool-base -b devtool-%s' % newpv)

        tmpdir = tempfile.mkdtemp(prefix='devtool')
        try:
            checksums, ftmpdir = scriptutils.fetch_url(tinfoil, uri, rev, tmpdir, logger, preserve_tmp=keep_temp)
        except scriptutils.FetchUrlFailure as e:
            raise DevtoolError(e)

        if ftmpdir and keep_temp:
            logger.info('Fetch temp directory is %s' % ftmpdir)

        md5 = checksums['md5sum']
        sha256 = checksums['sha256sum']

        tmpsrctree = _get_srctree(tmpdir)
        srctree = os.path.abspath(srctree)

        # Delete all sources so we ensure no stray files are left over
        for item in os.listdir(srctree):
            if item in ['.git', 'oe-local-files']:
                continue
            itempath = os.path.join(srctree, item)
            if os.path.isdir(itempath):
                shutil.rmtree(itempath)
            else:
                os.remove(itempath)

        # Copy in new ones
        _copy_source_code(tmpsrctree, srctree)

        (stdout,_) = __run('git ls-files --modified --others --exclude-standard')
        for f in stdout.splitlines():
            __run('git add "%s"' % f)

        useroptions = []
        oe.patch.GitApplyTree.gitCommandUserOptions(useroptions, d=rd)
        __run('git %s commit -q -m "Commit of upstream changes at version %s" --allow-empty' % (' '.join(useroptions), newpv))
        __run('git tag -f devtool-base-%s' % newpv)

    (stdout, _) = __run('git rev-parse HEAD')
    rev = stdout.rstrip()

    if no_patch:
        patches = oe.recipeutils.get_recipe_patches(crd)
        if len(patches):
            logger.warn('By user choice, the following patches will NOT be applied')
            for patch in patches:
                logger.warn("%s" % os.path.basename(patch))
    else:
        __run('git checkout devtool-patched -b %s' % branch)
        skiptag = False
        try:
            __run('git rebase %s' % rev)
        except bb.process.ExecutionError as e:
            skiptag = True
            if 'conflict' in e.stdout:
                logger.warn('Command \'%s\' failed:\n%s\n\nYou will need to resolve conflicts in order to complete the upgrade.' % (e.command, e.stdout.rstrip()))
            else:
                logger.warn('Command \'%s\' failed:\n%s' % (e.command, e.stdout))
        if not skiptag:
            if uri.startswith('git://'):
                suffix = 'new'
            else:
                suffix = newpv
            __run('git tag -f devtool-patched-%s' % suffix)

    if tmpsrctree:
        if keep_temp:
            logger.info('Preserving temporary directory %s' % tmpsrctree)
        else:
            shutil.rmtree(tmpsrctree)

    return (rev, md5, sha256, srcbranch)

def _create_new_recipe(newpv, md5, sha256, srcrev, srcbranch, workspace, tinfoil, rd):
    """Creates the new recipe under workspace"""

    bpn = rd.getVar('BPN')
    path = os.path.join(workspace, 'recipes', bpn)
    bb.utils.mkdirhier(path)
    copied, _ = oe.recipeutils.copy_recipe_files(rd, path, all_variants=True)
    if not copied:
        raise DevtoolError('Internal error - no files were copied for recipe %s' % bpn)
    logger.debug('Copied %s to %s' % (copied, path))

    oldpv = rd.getVar('PV')
    if not newpv:
        newpv = oldpv
    origpath = rd.getVar('FILE')
    fullpath = _rename_recipe_files(origpath, bpn, oldpv, newpv, path)
    logger.debug('Upgraded %s => %s' % (origpath, fullpath))

    newvalues = {}
    if _recipe_contains(rd, 'PV') and newpv != oldpv:
        newvalues['PV'] = newpv

    if srcrev:
        newvalues['SRCREV'] = srcrev

    if srcbranch:
        src_uri = oe.recipeutils.split_var_value(rd.getVar('SRC_URI', False) or '')
        changed = False
        replacing = True
        new_src_uri = []
        for entry in src_uri:
            scheme, network, path, user, passwd, params = bb.fetch2.decodeurl(entry)
            if replacing and scheme in ['git', 'gitsm']:
                branch = params.get('branch', 'master')
                if rd.expand(branch) != srcbranch:
                    # Handle case where branch is set through a variable
                    res = re.match(r'\$\{([^}@]+)\}', branch)
                    if res:
                        newvalues[res.group(1)] = srcbranch
                        # We know we won't change SRC_URI now, so break out
                        break
                    else:
                        params['branch'] = srcbranch
                        entry = bb.fetch2.encodeurl((scheme, network, path, user, passwd, params))
                        changed = True
                replacing = False
            new_src_uri.append(entry)
        if changed:
            newvalues['SRC_URI'] = ' '.join(new_src_uri)

    newvalues['PR'] = None

    if md5 and sha256:
        newvalues['SRC_URI[md5sum]'] = md5
        newvalues['SRC_URI[sha256sum]'] = sha256

    rd = tinfoil.parse_recipe_file(fullpath, False)
    oe.recipeutils.patch_recipe(rd, fullpath, newvalues)

    return fullpath, copied


def _check_git_config():
    def getconfig(name):
        try:
            value = bb.process.run('git config --global %s' % name)[0].strip()
        except bb.process.ExecutionError as e:
            if e.exitcode == 1:
                value = None
            else:
                raise
        return value

    username = getconfig('user.name')
    useremail = getconfig('user.email')
    configerr = []
    if not username:
        configerr.append('Please set your name using:\n  git config --global user.name')
    if not useremail:
        configerr.append('Please set your email using:\n  git config --global user.email')
    if configerr:
        raise DevtoolError('Your git configuration is incomplete which will prevent rebases from working:\n' + '\n'.join(configerr))


def upgrade(args, config, basepath, workspace):
    """Entry point for the devtool 'upgrade' subcommand"""

    if args.recipename in workspace:
        raise DevtoolError("recipe %s is already in your workspace" % args.recipename)
    if not args.version and not args.srcrev:
        raise DevtoolError("You must provide a version using the --version/-V option, or for recipes that fetch from an SCM such as git, the --srcrev/-S option")
    if args.srcbranch and not args.srcrev:
        raise DevtoolError("If you specify --srcbranch/-B then you must use --srcrev/-S to specify the revision" % args.recipename)

    _check_git_config()

    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1

        pn = rd.getVar('PN')
        if pn != args.recipename:
            logger.info('Mapping %s to %s' % (args.recipename, pn))
        if pn in workspace:
            raise DevtoolError("recipe %s is already in your workspace" % pn)

        if args.srctree:
            srctree = os.path.abspath(args.srctree)
        else:
            srctree = standard.get_default_srctree(config, pn)

        standard._check_compatible_recipe(pn, rd)
        old_srcrev = rd.getVar('SRCREV')
        if old_srcrev == 'INVALID':
            old_srcrev = None
        if old_srcrev and not args.srcrev:
            raise DevtoolError("Recipe specifies a SRCREV value; you must specify a new one when upgrading")
        if rd.getVar('PV') == args.version and old_srcrev == args.srcrev:
            raise DevtoolError("Current and upgrade versions are the same version")

        rf = None
        try:
            rev1 = standard._extract_source(srctree, False, 'devtool-orig', False, config, basepath, workspace, args.fixed_setup, rd, tinfoil)
            rev2, md5, sha256, srcbranch = _extract_new_source(args.version, srctree, args.no_patch,
                                                    args.srcrev, args.srcbranch, args.branch, args.keep_temp,
                                                    tinfoil, rd)
            rf, copied = _create_new_recipe(args.version, md5, sha256, args.srcrev, srcbranch, config.workspace_path, tinfoil, rd)
        except bb.process.CmdError as e:
            _upgrade_error(e, rf, srctree)
        except DevtoolError as e:
            _upgrade_error(e, rf, srctree)
        standard._add_md5(config, pn, os.path.dirname(rf))

        af = _write_append(rf, srctree, args.same_dir, args.no_same_dir, rev2,
                        copied, config.workspace_path, rd)
        standard._add_md5(config, pn, af)

        update_unlockedsigs(basepath, workspace, [pn], args.fixed_setup)

        logger.info('Upgraded source extracted to %s' % srctree)
        logger.info('New recipe is %s' % rf)
    finally:
        tinfoil.shutdown()
    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""

    defsrctree = standard.get_default_srctree(context.config)

    parser_upgrade = subparsers.add_parser('upgrade', help='Upgrade an existing recipe',
                                           description='Upgrades an existing recipe to a new upstream version. Puts the upgraded recipe file into the workspace along with any associated files, and extracts the source tree to a specified location (in case patches need rebasing or adding to as a result of the upgrade).',
                                           group='starting')
    parser_upgrade.add_argument('recipename', help='Name of recipe to upgrade (just name - no version, path or extension)')
    parser_upgrade.add_argument('srctree',  nargs='?', help='Path to where to extract the source tree. If not specified, a subdirectory of %s will be used.' % defsrctree)
    parser_upgrade.add_argument('--version', '-V', help='Version to upgrade to (PV)')
    parser_upgrade.add_argument('--srcrev', '-S', help='Source revision to upgrade to (required if fetching from an SCM such as git)')
    parser_upgrade.add_argument('--srcbranch', '-B', help='Branch in source repository containing the revision to use (if fetching from an SCM such as git)')
    parser_upgrade.add_argument('--branch', '-b', default="devtool", help='Name for new development branch to checkout (default "%(default)s")')
    parser_upgrade.add_argument('--no-patch', action="store_true", help='Do not apply patches from the recipe to the new source code')
    group = parser_upgrade.add_mutually_exclusive_group()
    group.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    group.add_argument('--no-same-dir', help='Force build in a separate build directory', action="store_true")
    parser_upgrade.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_upgrade.set_defaults(func=upgrade, fixed_setup=context.fixed_setup)
