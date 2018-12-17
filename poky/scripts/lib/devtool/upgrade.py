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
from devtool import exec_build_env_command, setup_tinfoil, DevtoolError, parse_recipe, use_external_build, update_unlockedsigs, check_prerelease_version

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
        _, _, _, _, _, params = bb.fetch2.decodeurl(uri)
        srcsubdir_rel = params.get('destsuffix', 'git')
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
        srcsubdir_rel = os.path.relpath(tmpsrctree, tmpdir)

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
        filelist = stdout.splitlines()
        pbar = bb.ui.knotty.BBProgress('Adding changed files', len(filelist))
        pbar.start()
        batchsize = 100
        for i in range(0, len(filelist), batchsize):
            batch = filelist[i:i+batchsize]
            __run('git add -A %s' % ' '.join(['"%s"' % item for item in batch]))
            pbar.update(i)
        pbar.finish()

        useroptions = []
        oe.patch.GitApplyTree.gitCommandUserOptions(useroptions, d=rd)
        __run('git %s commit -q -m "Commit of upstream changes at version %s" --allow-empty' % (' '.join(useroptions), newpv))
        __run('git tag -f devtool-base-%s' % newpv)

    (stdout, _) = __run('git rev-parse HEAD')
    rev = stdout.rstrip()

    if no_patch:
        patches = oe.recipeutils.get_recipe_patches(crd)
        if patches:
            logger.warning('By user choice, the following patches will NOT be applied to the new source tree:\n  %s' % '\n  '.join([os.path.basename(patch) for patch in patches]))
    else:
        __run('git checkout devtool-patched -b %s' % branch)
        skiptag = False
        try:
            __run('git rebase %s' % rev)
        except bb.process.ExecutionError as e:
            skiptag = True
            if 'conflict' in e.stdout:
                logger.warning('Command \'%s\' failed:\n%s\n\nYou will need to resolve conflicts in order to complete the upgrade.' % (e.command, e.stdout.rstrip()))
            else:
                logger.warning('Command \'%s\' failed:\n%s' % (e.command, e.stdout))
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

    return (rev, md5, sha256, srcbranch, srcsubdir_rel)

def _add_license_diff_to_recipe(path, diff):
    notice_text = """# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
"""
    commented_diff = "\n".join(["# {}".format(l) for l in diff.split('\n')])
    with open(path, 'rb') as f:
        orig_content = f.read()
    with open(path, 'wb') as f:
        f.write(notice_text.encode())
        f.write(commented_diff.encode())
        f.write("\n#\n\n".encode())
        f.write(orig_content)

def _create_new_recipe(newpv, md5, sha256, srcrev, srcbranch, srcsubdir_old, srcsubdir_new, workspace, tinfoil, rd, license_diff, new_licenses):
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

    # Work out which SRC_URI entries have changed in case the entry uses a name
    crd = rd.createCopy()
    crd.setVar('PV', newpv)
    for var, value in newvalues.items():
        crd.setVar(var, value)
    old_src_uri = (rd.getVar('SRC_URI') or '').split()
    new_src_uri = (crd.getVar('SRC_URI') or '').split()
    newnames = []
    addnames = []
    for newentry in new_src_uri:
        _, _, _, _, _, params = bb.fetch2.decodeurl(newentry)
        if 'name' in params:
            newnames.append(params['name'])
            if newentry not in old_src_uri:
                addnames.append(params['name'])
    # Find what's been set in the original recipe
    oldnames = []
    noname = False
    for varflag in rd.getVarFlags('SRC_URI'):
        if varflag.endswith(('.md5sum', '.sha256sum')):
            name = varflag.rsplit('.', 1)[0]
            if name not in oldnames:
                oldnames.append(name)
        elif varflag in ['md5sum', 'sha256sum']:
            noname = True
    # Even if SRC_URI has named entries it doesn't have to actually use the name
    if noname and addnames and addnames[0] not in oldnames:
        addnames = []
    # Drop any old names (the name actually might include ${PV})
    for name in oldnames:
        if name not in newnames:
            newvalues['SRC_URI[%s.md5sum]' % name] = None
            newvalues['SRC_URI[%s.sha256sum]' % name] = None

    if md5 and sha256:
        if addnames:
            nameprefix = '%s.' % addnames[0]
        else:
            nameprefix = ''
        newvalues['SRC_URI[%smd5sum]' % nameprefix] = md5
        newvalues['SRC_URI[%ssha256sum]' % nameprefix] = sha256

    if srcsubdir_new != srcsubdir_old:
        s_subdir_old = os.path.relpath(os.path.abspath(rd.getVar('S')), rd.getVar('WORKDIR'))
        s_subdir_new = os.path.relpath(os.path.abspath(crd.getVar('S')), crd.getVar('WORKDIR'))
        if srcsubdir_old == s_subdir_old and srcsubdir_new != s_subdir_new:
            # Subdir for old extracted source matches what S points to (it should!)
            # but subdir for new extracted source doesn't match what S will be
            newvalues['S'] = '${WORKDIR}/%s' % srcsubdir_new.replace(newpv, '${PV}')
            if crd.expand(newvalues['S']) == crd.expand('${WORKDIR}/${BP}'):
                # It's the default, drop it
                # FIXME what if S is being set in a .inc?
                newvalues['S'] = None
                logger.info('Source subdirectory has changed, dropping S value since it now matches the default ("${WORKDIR}/${BP}")')
            else:
                logger.info('Source subdirectory has changed, updating S value')

    if license_diff:
        newlicchksum = " ".join(["file://{}".format(l['path']) +
                                 (";beginline={}".format(l['beginline']) if l['beginline'] else "") +
                                 (";endline={}".format(l['endline']) if l['endline'] else "") +
                                 (";md5={}".format(l['actual_md5'])) for l in new_licenses])
        newvalues["LIC_FILES_CHKSUM"] = newlicchksum
        _add_license_diff_to_recipe(fullpath, license_diff)

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

def _extract_licenses(srcpath, recipe_licenses):
    licenses = []
    for url in recipe_licenses.split():
        license = {}
        (type, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
        license['path'] = path
        license['md5'] = parm.get('md5', '')
        license['beginline'], license['endline'] = 0, 0
        if 'beginline' in parm:
            license['beginline'] = int(parm['beginline'])
        if 'endline' in parm:
            license['endline'] = int(parm['endline'])
        license['text'] = []
        with open(os.path.join(srcpath, path), 'rb') as f:
            import hashlib
            actual_md5 = hashlib.md5()
            lineno = 0
            for line in f:
                lineno += 1
                if (lineno >= license['beginline']) and ((lineno <= license['endline']) or not license['endline']):
                    license['text'].append(line.decode(errors='ignore'))
                    actual_md5.update(line)
        license['actual_md5'] = actual_md5.hexdigest()
        licenses.append(license)
    return licenses

def _generate_license_diff(old_licenses, new_licenses):
    need_diff = False
    for l in new_licenses:
        if l['md5'] != l['actual_md5']:
            need_diff = True
            break
    if need_diff == False:
        return None

    import difflib
    diff = ''
    for old, new in zip(old_licenses, new_licenses):
        for line in difflib.unified_diff(old['text'], new['text'], old['path'], new['path']):
            diff = diff + line
    return diff

def upgrade(args, config, basepath, workspace):
    """Entry point for the devtool 'upgrade' subcommand"""

    if args.recipename in workspace:
        raise DevtoolError("recipe %s is already in your workspace" % args.recipename)
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

        # try to automatically discover latest version and revision if not provided on command line
        if not args.version and not args.srcrev:
            version_info = oe.recipeutils.get_recipe_upstream_version(rd)
            if version_info['version'] and not version_info['version'].endswith("new-commits-available"):
                args.version = version_info['version']
            if version_info['revision']:
                args.srcrev = version_info['revision']
        if not args.version and not args.srcrev:
            raise DevtoolError("Automatic discovery of latest version/revision failed - you must provide a version using the --version/-V option, or for recipes that fetch from an SCM such as git, the --srcrev/-S option.")

        standard._check_compatible_recipe(pn, rd)
        old_srcrev = rd.getVar('SRCREV')
        if old_srcrev == 'INVALID':
            old_srcrev = None
        if old_srcrev and not args.srcrev:
            raise DevtoolError("Recipe specifies a SRCREV value; you must specify a new one when upgrading")
        old_ver = rd.getVar('PV')
        if old_ver == args.version and old_srcrev == args.srcrev:
            raise DevtoolError("Current and upgrade versions are the same version")
        if args.version:
            if bb.utils.vercmp_string(args.version, old_ver) < 0:
                logger.warning('Upgrade version %s compares as less than the current version %s. If you are using a package feed for on-target upgrades or providing this recipe for general consumption, then you should increment PE in the recipe (or if there is no current PE value set, set it to "1")' % (args.version, old_ver))
            check_prerelease_version(args.version, 'devtool upgrade')

        rf = None
        license_diff = None
        try:
            logger.info('Extracting current version source...')
            rev1, srcsubdir1 = standard._extract_source(srctree, False, 'devtool-orig', False, config, basepath, workspace, args.fixed_setup, rd, tinfoil, no_overrides=args.no_overrides)
            old_licenses = _extract_licenses(srctree, rd.getVar('LIC_FILES_CHKSUM'))
            logger.info('Extracting upgraded version source...')
            rev2, md5, sha256, srcbranch, srcsubdir2 = _extract_new_source(args.version, srctree, args.no_patch,
                                                    args.srcrev, args.srcbranch, args.branch, args.keep_temp,
                                                    tinfoil, rd)
            new_licenses = _extract_licenses(srctree, rd.getVar('LIC_FILES_CHKSUM'))
            license_diff = _generate_license_diff(old_licenses, new_licenses)
            rf, copied = _create_new_recipe(args.version, md5, sha256, args.srcrev, srcbranch, srcsubdir1, srcsubdir2, config.workspace_path, tinfoil, rd, license_diff, new_licenses)
        except bb.process.CmdError as e:
            _upgrade_error(e, rf, srctree)
        except DevtoolError as e:
            _upgrade_error(e, rf, srctree)
        standard._add_md5(config, pn, os.path.dirname(rf))

        af = _write_append(rf, srctree, args.same_dir, args.no_same_dir, rev2,
                        copied, config.workspace_path, rd)
        standard._add_md5(config, pn, af)

        update_unlockedsigs(basepath, workspace, args.fixed_setup, [pn])

        logger.info('Upgraded source extracted to %s' % srctree)
        logger.info('New recipe is %s' % rf)
        if license_diff:
            logger.info('License checksums have been updated in the new recipe; please refer to it for the difference between the old and the new license texts.')
    finally:
        tinfoil.shutdown()
    return 0

def latest_version(args, config, basepath, workspace):
    """Entry point for the devtool 'latest_version' subcommand"""
    tinfoil = setup_tinfoil(basepath=basepath, tracking=True)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, True)
        if not rd:
            return 1
        version_info = oe.recipeutils.get_recipe_upstream_version(rd)
        # "new-commits-available" is an indication that upstream never issues version tags
        if not version_info['version'].endswith("new-commits-available"):
            logger.info("Current version: {}".format(version_info['current_version']))
            logger.info("Latest version: {}".format(version_info['version']))
            if version_info['revision']:
                logger.info("Latest version's commit: {}".format(version_info['revision']))
        else:
            logger.info("Latest commit: {}".format(version_info['revision']))
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
    parser_upgrade.add_argument('--version', '-V', help='Version to upgrade to (PV). If omitted, latest upstream version will be determined and used, if possible.')
    parser_upgrade.add_argument('--srcrev', '-S', help='Source revision to upgrade to (useful when fetching from an SCM such as git)')
    parser_upgrade.add_argument('--srcbranch', '-B', help='Branch in source repository containing the revision to use (if fetching from an SCM such as git)')
    parser_upgrade.add_argument('--branch', '-b', default="devtool", help='Name for new development branch to checkout (default "%(default)s")')
    parser_upgrade.add_argument('--no-patch', action="store_true", help='Do not apply patches from the recipe to the new source code')
    parser_upgrade.add_argument('--no-overrides', '-O', action="store_true", help='Do not create branches for other override configurations')
    group = parser_upgrade.add_mutually_exclusive_group()
    group.add_argument('--same-dir', '-s', help='Build in same directory as source', action="store_true")
    group.add_argument('--no-same-dir', help='Force build in a separate build directory', action="store_true")
    parser_upgrade.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_upgrade.set_defaults(func=upgrade, fixed_setup=context.fixed_setup)

    parser_latest_version = subparsers.add_parser('latest-version', help='Report the latest version of an existing recipe',
                                                  description='Queries the upstream server for what the latest upstream release is (for git, tags are checked, for tarballs, a list of them is obtained, and one with the highest version number is reported)',
                                                  group='info')
    parser_latest_version.add_argument('recipename', help='Name of recipe to query (just name - no version, path or extension)')
    parser_latest_version.set_defaults(func=latest_version)
