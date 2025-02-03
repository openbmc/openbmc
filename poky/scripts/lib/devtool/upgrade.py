# Development tool - upgrade command plugin
#
# Copyright (C) 2014-2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
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
    else:
        raise DevtoolError("Cannot determine where the source tree is after unpacking in {}: {}".format(tmpdir,dirs))
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
                        bb.utils.rename(os.path.join(path, oldfile),
                              os.path.join(path, newfile))

def _rename_recipe_file(oldrecipe, pn, oldpv, newpv, path):
    oldrecipe = os.path.basename(oldrecipe)
    if oldrecipe.endswith('_%s.bb' % oldpv):
        newrecipe = '%s_%s.bb' % (pn, newpv)
        if oldrecipe != newrecipe:
            shutil.move(os.path.join(path, oldrecipe), os.path.join(path, newrecipe))
    else:
        newrecipe = oldrecipe
    return os.path.join(path, newrecipe)

def _rename_recipe_files(oldrecipe, pn, oldpv, newpv, path):
    _rename_recipe_dirs(oldpv, newpv, path)
    return _rename_recipe_file(oldrecipe, pn, oldpv, newpv, path)

def _write_append(rc, srctreebase, srctree, same_dir, no_same_dir, revs, copied, workspace, d):
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
        f.write('FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n\n')
        # Local files can be modified/tracked in separate subdir under srctree
        # Mostly useful for packages with S != WORKDIR
        f.write('FILESPATH:prepend := "%s:"\n' %
                os.path.join(srctreebase, 'oe-local-files'))
        f.write('# srctreebase: %s\n' % srctreebase)
        f.write('inherit externalsrc\n')
        f.write(('# NOTE: We use pn- overrides here to avoid affecting'
                 'multiple variants in the case where the recipe uses BBCLASSEXTEND\n'))
        f.write('EXTERNALSRC:pn-%s = "%s"\n' % (pn, srctree))
        b_is_s = use_external_build(same_dir, no_same_dir, d)
        if b_is_s:
            f.write('EXTERNALSRC_BUILD:pn-%s = "%s"\n' % (pn, srctree))
        f.write('\n')
        if revs:
            for name, rev in revs.items():
                f.write('# initial_rev %s: %s\n' % (name, rev))
        if copied:
            f.write('# original_path: %s\n' % os.path.dirname(d.getVar('FILE')))
            f.write('# original_files: %s\n' % ' '.join(copied))
    return af

def _cleanup_on_error(rd, srctree):
    if os.path.exists(rd):
        shutil.rmtree(rd)
    srctree = os.path.abspath(srctree)
    if os.path.exists(srctree):
        shutil.rmtree(srctree)

def _upgrade_error(e, rd, srctree, keep_failure=False, extramsg=None):
    if not keep_failure:
        _cleanup_on_error(rd, srctree)
    logger.error(e)
    if extramsg:
        logger.error(extramsg)
    if keep_failure:
        logger.info('Preserving failed upgrade files (--keep-failure)')
    sys.exit(1)

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
    import oe.patch

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
    paths = [srctree]
    if uri.startswith('git://') or uri.startswith('gitsm://'):
        __run('git fetch')
        __run('git checkout %s' % rev)
        __run('git tag -f devtool-base-new')
        __run('git submodule update --recursive')
        __run('git submodule foreach \'git tag -f devtool-base-new\'')
        (stdout, _) = __run('git submodule --quiet foreach \'echo $sm_path\'')
        paths += [os.path.join(srctree, p) for p in stdout.splitlines()]
        checksums = {}
        _, _, _, _, _, params = bb.fetch2.decodeurl(uri)
        srcsubdir_rel = params.get('destsuffix', 'git')
        if not srcbranch:
            check_branch, check_branch_err = __run('git branch -r --contains %s' % srcrev)
            get_branch = [x.strip() for x in check_branch.splitlines()]
            # Remove HEAD reference point and drop remote prefix
            get_branch = [x.split('/', 1)[1] for x in get_branch if not x.startswith('origin/HEAD')]
            if len(get_branch) == 1:
                # If srcrev is on only ONE branch, then use that branch
                srcbranch = get_branch[0]
            elif 'main' in get_branch:
                # If srcrev is on multiple branches, then choose 'main' if it is one of them
                srcbranch = 'main'
            elif 'master' in get_branch:
                # Otherwise choose 'master' if it is one of the branches
                srcbranch = 'master'
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

        (stdout,_) = __run('git ls-files --modified --others')
        filelist = stdout.splitlines()
        pbar = bb.ui.knotty.BBProgress('Adding changed files', len(filelist))
        pbar.start()
        batchsize = 100
        for i in range(0, len(filelist), batchsize):
            batch = filelist[i:i+batchsize]
            __run('git add -f -A %s' % ' '.join(['"%s"' % item for item in batch]))
            pbar.update(i)
        pbar.finish()

        useroptions = []
        oe.patch.GitApplyTree.gitCommandUserOptions(useroptions, d=rd)
        __run('git %s commit -q -m "Commit of upstream changes at version %s" --allow-empty' % (' '.join(useroptions), newpv))
        __run('git tag -f devtool-base-%s' % newpv)

    revs = {}
    for path in paths:
        (stdout, _) = _run('git rev-parse HEAD', cwd=path)
        revs[os.path.relpath(path, srctree)] = stdout.rstrip()

    if no_patch:
        patches = oe.recipeutils.get_recipe_patches(crd)
        if patches:
            logger.warning('By user choice, the following patches will NOT be applied to the new source tree:\n  %s' % '\n  '.join([os.path.basename(patch) for patch in patches]))
    else:
        for path in paths:
            _run('git checkout devtool-patched -b %s' % branch, cwd=path)
            (stdout, _) = _run('git branch --list devtool-override-*', cwd=path)
            branches_to_rebase = [branch] + stdout.split()
            target_branch = revs[os.path.relpath(path, srctree)]

            # There is a bug (or feature?) in git rebase where if a commit with
            # a note is fully rebased away by being part of an old commit, the
            # note is still attached to the old commit. Avoid this by making
            # sure all old devtool related commits have a note attached to them
            # (this assumes git config notes.rewriteMode is set to ignore).
            (stdout, _) = __run('git rev-list devtool-base..%s' % target_branch)
            for rev in stdout.splitlines():
                if not oe.patch.GitApplyTree.getNotes(path, rev):
                    oe.patch.GitApplyTree.addNote(path, rev, "dummy")

            for b in branches_to_rebase:
                logger.info("Rebasing {} onto {}".format(b, target_branch))
                _run('git checkout %s' % b, cwd=path)
                try:
                    _run('git rebase %s' % target_branch, cwd=path)
                except bb.process.ExecutionError as e:
                    if 'conflict' in e.stdout:
                        logger.warning('Command \'%s\' failed:\n%s\n\nYou will need to resolve conflicts in order to complete the upgrade.' % (e.command, e.stdout.rstrip()))
                        _run('git rebase --abort', cwd=path)
                    else:
                        logger.warning('Command \'%s\' failed:\n%s' % (e.command, e.stdout))

            # Remove any dummy notes added above.
            (stdout, _) = __run('git rev-list devtool-base..%s' % target_branch)
            for rev in stdout.splitlines():
                oe.patch.GitApplyTree.removeNote(path, rev, "dummy")

            _run('git checkout %s' % branch, cwd=path)

    if tmpsrctree:
        if keep_temp:
            logger.info('Preserving temporary directory %s' % tmpsrctree)
        else:
            shutil.rmtree(tmpsrctree)
            if tmpdir != tmpsrctree:
                shutil.rmtree(tmpdir)

    return (revs, checksums, srcbranch, srcsubdir_rel)

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

def _create_new_recipe(newpv, checksums, srcrev, srcbranch, srcsubdir_old, srcsubdir_new, workspace, tinfoil, rd, license_diff, new_licenses, srctree, keep_failure):
    """Creates the new recipe under workspace"""

    pn = rd.getVar('PN')
    path = os.path.join(workspace, 'recipes', pn)
    bb.utils.mkdirhier(path)
    copied, _ = oe.recipeutils.copy_recipe_files(rd, path, all_variants=True)
    if not copied:
        raise DevtoolError('Internal error - no files were copied for recipe %s' % pn)
    logger.debug('Copied %s to %s' % (copied, path))

    oldpv = rd.getVar('PV')
    if not newpv:
        newpv = oldpv
    origpath = rd.getVar('FILE')
    fullpath = _rename_recipe_files(origpath, pn, oldpv, newpv, path)
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
            try:
                scheme, network, path, user, passwd, params = bb.fetch2.decodeurl(entry)
            except bb.fetch2.MalformedUrl as e:
                raise DevtoolError("Could not decode SRC_URI: {}".format(e))
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
    oldsums = []
    noname = False
    for varflag in rd.getVarFlags('SRC_URI'):
        for checksum in checksums:
            if varflag.endswith('.' + checksum):
                name = varflag.rsplit('.', 1)[0]
                if name not in oldnames:
                    oldnames.append(name)
                oldsums.append(checksum)
            elif varflag == checksum:
                noname = True
                oldsums.append(checksum)
    # Even if SRC_URI has named entries it doesn't have to actually use the name
    if noname and addnames and addnames[0] not in oldnames:
        addnames = []
    # Drop any old names (the name actually might include ${PV})
    for name in oldnames:
        if name not in newnames:
            for checksum in oldsums:
                newvalues['SRC_URI[%s.%s]' % (name, checksum)] = None

    nameprefix = '%s.' % addnames[0] if addnames else ''

    # md5sum is deprecated, remove any traces of it. If it was the only old
    # checksum, then replace it with the default checksums.
    if 'md5sum' in oldsums:
        newvalues['SRC_URI[%smd5sum]' % nameprefix] = None
        oldsums.remove('md5sum')
        if not oldsums:
            oldsums = ["%ssum" % s for s in bb.fetch2.SHOWN_CHECKSUM_LIST]

    for checksum in oldsums:
        newvalues['SRC_URI[%s%s]' % (nameprefix, checksum)] = checksums[checksum]

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

    tinfoil.modified_files()
    try:
        rd = tinfoil.parse_recipe_file(fullpath, False)
    except bb.tinfoil.TinfoilCommandFailed as e:
        _upgrade_error(e, os.path.dirname(fullpath), srctree, keep_failure, 'Parsing of upgraded recipe failed')
    oe.recipeutils.patch_recipe(rd, fullpath, newvalues)

    return fullpath, copied


def _check_git_config():
    def getconfig(name):
        try:
            value = bb.process.run('git config %s' % name)[0].strip()
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

def _run_recipe_upgrade_extra_tasks(pn, rd, tinfoil):
    tasks = []
    for task in (rd.getVar('RECIPE_UPGRADE_EXTRA_TASKS') or '').split():
        logger.info('Running extra recipe upgrade task: %s' % task)
        res = tinfoil.build_targets(pn, task, handle_events=True)

        if not res:
            raise DevtoolError('Running extra recipe upgrade task %s for %s failed' % (task, pn))

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

        srctree_s = standard.get_real_srctree(srctree, rd.getVar('S'), rd.getVar('WORKDIR'))

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
            old_licenses = _extract_licenses(srctree_s, (rd.getVar('LIC_FILES_CHKSUM') or ""))
            logger.info('Extracting upgraded version source...')
            rev2, checksums, srcbranch, srcsubdir2 = _extract_new_source(args.version, srctree, args.no_patch,
                                                    args.srcrev, args.srcbranch, args.branch, args.keep_temp,
                                                    tinfoil, rd)
            new_licenses = _extract_licenses(srctree_s, (rd.getVar('LIC_FILES_CHKSUM') or ""))
            license_diff = _generate_license_diff(old_licenses, new_licenses)
            rf, copied = _create_new_recipe(args.version, checksums, args.srcrev, srcbranch, srcsubdir1, srcsubdir2, config.workspace_path, tinfoil, rd, license_diff, new_licenses, srctree, args.keep_failure)
        except (bb.process.CmdError, DevtoolError) as e:
            recipedir = os.path.join(config.workspace_path, 'recipes', rd.getVar('PN'))
            _upgrade_error(e, recipedir, srctree, args.keep_failure)
        standard._add_md5(config, pn, os.path.dirname(rf))

        af = _write_append(rf, srctree, srctree_s, args.same_dir, args.no_same_dir, rev2,
                        copied, config.workspace_path, rd)
        standard._add_md5(config, pn, af)

        _run_recipe_upgrade_extra_tasks(pn, rd, tinfoil)

        update_unlockedsigs(basepath, workspace, args.fixed_setup, [pn])

        logger.info('Upgraded source extracted to %s' % srctree)
        logger.info('New recipe is %s' % rf)
        if license_diff:
            logger.info('License checksums have been updated in the new recipe; please refer to it for the difference between the old and the new license texts.')
        preferred_version = rd.getVar('PREFERRED_VERSION_%s' % rd.getVar('PN'))
        if preferred_version:
            logger.warning('Version is pinned to %s via PREFERRED_VERSION; it may need adjustment to match the new version before any further steps are taken' % preferred_version)
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

def check_upgrade_status(args, config, basepath, workspace):
    def _print_status(recipe):
        print("{:25} {:15} {:15} {} {} {}".format(   recipe['pn'],
                                                               recipe['cur_ver'],
                                                               recipe['status'] if recipe['status'] != 'UPDATE' else (recipe['next_ver'] if not recipe['next_ver'].endswith("new-commits-available") else "new commits"),
                                                               recipe['maintainer'],
                                                               recipe['revision'] if recipe['revision'] != 'N/A' else "",
                                                               "cannot be updated due to: %s" %(recipe['no_upgrade_reason']) if recipe['no_upgrade_reason'] else ""))
    if not args.recipe:
        logger.info("Checking the upstream status for all recipes may take a few minutes")
    results = oe.recipeutils.get_recipe_upgrade_status(args.recipe)
    for recipegroup in results:
        upgrades = [r for r in recipegroup if r['status'] != 'MATCH']
        currents = [r for r in recipegroup if r['status'] == 'MATCH']
        if len(upgrades) > 1:
            print("These recipes need to be upgraded together {")
        for r in upgrades:
            _print_status(r)
        if len(upgrades) > 1:
            print("}")
        for r in currents:
            if args.all:
                _print_status(r)

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
    parser_upgrade.add_argument('--keep-failure', action="store_true", help='Keep failed upgrade recipe and associated files  (for debugging)')
    parser_upgrade.set_defaults(func=upgrade, fixed_setup=context.fixed_setup)

    parser_latest_version = subparsers.add_parser('latest-version', help='Report the latest version of an existing recipe',
                                                  description='Queries the upstream server for what the latest upstream release is (for git, tags are checked, for tarballs, a list of them is obtained, and one with the highest version number is reported)',
                                                  group='info')
    parser_latest_version.add_argument('recipename', help='Name of recipe to query (just name - no version, path or extension)')
    parser_latest_version.set_defaults(func=latest_version)

    parser_check_upgrade_status = subparsers.add_parser('check-upgrade-status', help="Report upgradability for multiple (or all) recipes",
                                                        description="Prints a table of recipes together with versions currently provided by recipes, and latest upstream versions, when there is a later version available",
                                                        group='info')
    parser_check_upgrade_status.add_argument('recipe', help='Name of the recipe to report (omit to report upgrade info for all recipes)', nargs='*')
    parser_check_upgrade_status.add_argument('--all', '-a', help='Show all recipes, not just recipes needing upgrade', action="store_true")
    parser_check_upgrade_status.set_defaults(func=check_upgrade_status)
