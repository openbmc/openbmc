# Development tool - deploy/undeploy command plugin
#
# Copyright (C) 2014-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool plugin containing the deploy subcommands"""

import logging
import os
import shutil
import shlex
import subprocess
import sys
import tempfile

import bb.utils
import argparse_oe
import oe.types
import oe.package

from devtool import exec_fakeroot_no_d, build_fakeroot_env_no_d, setup_tinfoil, check_workspace_recipe, DevtoolError

logger = logging.getLogger('devtool')

deploylist_dirname = '.devtool'

def _prepare_remote_script(deploy, destdir='/', verbose=False, dryrun=False, undeployall=False, nopreserve=False, nocheckspace=False):
    """
    Prepare a shell script for running on the target to
    deploy/undeploy files. We have to be careful what we put in this
    script - only commands that are likely to be available on the
    target are suitable (the target might be constrained, e.g. using
    busybox rather than bash with coreutils).
    """
    lines = []
    deploylist_path = os.path.join(destdir, deploylist_dirname)
    lines.append('#!/bin/sh')
    lines.append('set -e')
    if undeployall:
        # Yes, I know this is crude - but it does work
        lines.append('for entry in %s/*.list; do' % deploylist_path)
        lines.append('[ ! -f $entry ] && exit')
        lines.append('set `basename $entry | sed "s/.list//"`')
    if dryrun:
        if not deploy:
            lines.append('echo "Previously deployed files for $1:"')
    lines.append('manifest="%s/$1.list"' % deploylist_path)
    lines.append('preservedir="%s/$1.preserve"' % deploylist_path)
    lines.append('if [ -f $manifest ] ; then')
    # Read manifest in reverse and delete files / remove empty dirs
    lines.append('    sed \'1!G;h;$!d\' $manifest | while read file')
    lines.append('    do')
    if dryrun:
        lines.append('        if [ ! -d $file ] ; then')
        lines.append('            echo $file')
        lines.append('        fi')
    else:
        lines.append('        if [ -d $file ] ; then')
        # Avoid deleting a preserved directory in case it has special perms
        lines.append('            if [ ! -d $preservedir/$file ] ; then')
        lines.append('                rmdir $file > /dev/null 2>&1 || true')
        lines.append('            fi')
        lines.append('        else')
        lines.append('            rm -f $file')
        lines.append('        fi')
    lines.append('    done')
    if not dryrun:
        lines.append('    rm $manifest')
    if not deploy and not dryrun:
        # May as well remove all traces
        lines.append('    rmdir `dirname $manifest` > /dev/null 2>&1 || true')
    lines.append('fi')

    if deploy:
        if not nocheckspace:
            # Check for available space
            # FIXME This doesn't take into account files spread across multiple
            # partitions, but doing that is non-trivial
            # Find the part of the destination path that exists
            lines.append('checkpath="$2"')
            lines.append('while [ "$checkpath" != "/" ] && [ ! -e $checkpath ]')
            lines.append('do')
            lines.append('    checkpath=`dirname "$checkpath"`')
            lines.append('done')
            lines.append(r'freespace=$(df -P $checkpath | sed -nre "s/^(\S+\s+){3}([0-9]+).*/\2/p")')
            # First line of the file is the total space
            lines.append('total=`head -n1 $3`')
            lines.append('if [ $total -gt $freespace ] ; then')
            lines.append('    echo "ERROR: insufficient space on target (available ${freespace}, needed ${total})"')
            lines.append('    exit 1')
            lines.append('fi')
        if not nopreserve:
            # Preserve any files that exist. Note that this will add to the
            # preserved list with successive deployments if the list of files
            # deployed changes, but because we've deleted any previously
            # deployed files at this point it will never preserve anything
            # that was deployed, only files that existed prior to any deploying
            # (which makes the most sense)
            lines.append('cat $3 | sed "1d" | while read file fsize')
            lines.append('do')
            lines.append('    if [ -e $file ] ; then')
            lines.append('    dest="$preservedir/$file"')
            lines.append('    mkdir -p `dirname $dest`')
            lines.append('    mv $file $dest')
            lines.append('    fi')
            lines.append('done')
            lines.append('rm $3')
        lines.append('mkdir -p `dirname $manifest`')
        lines.append('mkdir -p $2')
        if verbose:
            # -m avoids "time stamp is in the future" warnings if the target's clock is behind
            lines.append('    tar xvm -C $2 -f - | tee $manifest')
        else:
            lines.append('    tar xvm -C $2 -f - > $manifest')
        lines.append('sed -i "s!^./!$2!" $manifest')
    elif not dryrun:
        # Put any preserved files back
        lines.append('if [ -d $preservedir ] ; then')
        lines.append('    cd $preservedir')
        # find from busybox might not have -exec, so we don't use that
        lines.append('    find . -type f | while read file')
        lines.append('    do')
        lines.append('        mv $file /$file')
        lines.append('    done')
        lines.append('    cd /')
        lines.append('    rm -rf $preservedir')
        lines.append('fi')

    if undeployall:
        if not dryrun:
            lines.append('echo "NOTE: Successfully undeployed $1"')
        lines.append('done')

    # Delete the script itself
    lines.append('rm $0')
    lines.append('')

    return '\n'.join(lines)

def parse_file_globs_arg(entries, recipename=None):
    """Flatten --file-glob entries ("[RECIPE:]GLOB") into glob patterns for recipename.

    E.g. ["other:/usr/bin/*", "/etc/*"] with recipename="myrecipe" -> ["/etc/*"].
    """
    globs = []
    for entry in entries or []:
        recipe, spec = entry.split(':', 1) if ':' in entry else (None, entry)
        if recipe is not None and recipename is not None and recipe != recipename:
            continue
        if spec:
            globs.append(spec)
    return globs

def _match_deploy_files(recipe_outdir, file_globs, recipename=None):
    """Match file_globs (FILES-variable-style patterns) against recipe_outdir.

    Returns the matched files (relative to recipe_outdir), or None if no glob
    applies to recipename.
    """
    file_globs = parse_file_globs_arg(file_globs, recipename)
    if not file_globs:
        return None
    cwd = os.getcwd()
    os.chdir(recipe_outdir)
    try:
        matched, _ = oe.package.files_from_filevars(file_globs)
    finally:
        os.chdir(cwd)
    return {os.path.normpath(f) for f in matched}

def parse_packages_arg(entries, recipename=None):
    """Flatten --package entries ("[RECIPE:]PKG[,PKG...]") into package names.

    E.g. with recipename="myrecipe": "myrecipe:,-doc,-ptest" ->
    ["myrecipe", "myrecipe-doc", "myrecipe-ptest"]; "other:foo" -> [].
    """
    packages = []
    for entry in entries or []:
        recipe, spec = entry.split(':', 1) if ':' in entry else (None, entry)
        if recipe is not None and recipename is not None and recipe != recipename:
            continue
        for item in spec.split(','):
            if not item:
                if recipe is not None:
                    packages.append(recipe)
            elif item.startswith('-'):
                if recipe is None:
                    raise DevtoolError('Package suffix "%s" requires a "RECIPE:" prefix, '
                                    'e.g. "RECIPE:%s"' % (item, item))
                packages.append(recipe + item)
            else:
                packages.append(item)
    return packages

def is_default_excluded_package(pkg):
    """True for packages left out of a deploy"""
    return pkg.endswith(('-dbg', '-src', '-staticdev'))

def _match_package_files(recipe_outdir, packages_files, packages, recipename=None):
    """Resolve packages (see parse_packages_arg) into their files under recipe_outdir."""
    packages = parse_packages_arg(packages, recipename)
    all_package_names = [pkg for pkg, _ in packages_files]
    for pkg in packages:
        if pkg not in all_package_names:
            raise DevtoolError('Package "%s" is not one of the packages produced '
                            'by this recipe (PACKAGES: %s)' % (pkg, ' '.join(all_package_names)))
    cwd = os.getcwd()
    os.chdir(recipe_outdir)
    try:
        seen = set()
        result = set() if packages else None
        default_excluded = set()
        for pkg, files_var in packages_files:
            matched, _ = oe.package.files_from_filevars((files_var or '').split())
            matched = {os.path.normpath(f) for f in matched} - seen
            seen |= matched
            if pkg in packages:
                if result is not None:
                    result |= matched
            elif is_default_excluded_package(pkg):
                default_excluded |= matched
    finally:
        os.chdir(cwd)
    return result, default_excluded

def deploy(args, config, basepath, workspace):
    """Entry point for the devtool 'deploy' subcommand"""
    import oe.utils

    check_workspace_recipe(workspace, args.recipename, checksrc=False)

    tinfoil = setup_tinfoil(basepath=basepath)
    try:
        try:
            rd = tinfoil.parse_recipe(args.recipename)
        except Exception as e:
            raise DevtoolError('Exception parsing recipe %s: %s' %
                            (args.recipename, e))

        srcdir = rd.getVar('D')
        workdir = rd.getVar('WORKDIR')
        path = rd.getVar('PATH')
        strip_cmd = rd.getVar('STRIP')
        libdir = rd.getVar('libdir')
        base_libdir = rd.getVar('base_libdir')
        max_process = oe.utils.get_bb_number_threads(rd)
        fakerootcmd = rd.getVar('FAKEROOTCMD')
        fakerootenv = rd.getVar('FAKEROOTENV')
        packages_files = [(pkg, rd.getVar('FILES:' + pkg) or '')
                        for pkg in (rd.getVar('PACKAGES') or '').split()]
    finally:
        tinfoil.shutdown()

    return deploy_no_d(srcdir, workdir, path, strip_cmd, libdir, base_libdir, max_process, fakerootcmd, fakerootenv, args, file_globs=args.file_globs, packages_files=packages_files)

def deploy_no_d(srcdir, workdir, path, strip_cmd, libdir, base_libdir, max_process, fakerootcmd, fakerootenv, args, file_globs=None, packages_files=None):
    import math

    if os.path.isabs(args.target):
        # A local pseudo-managed rootfs directory (e.g. NFS-exported)
        destdir = os.path.realpath(args.target)
    else:
        try:
            host, destdir = args.target.split(':')
        except ValueError:
            destdir = '/'
        else:
            args.target = host
    # Canonical form used throughout: no trailing slash (except root itself).
    destdir = destdir.rstrip('/') or '/'

    recipe_outdir = srcdir
    if not os.path.exists(recipe_outdir) or not os.listdir(recipe_outdir):
        raise DevtoolError('No files to deploy - have you built the %s '
                        'recipe? If so, the install step has not installed '
                        'any files.' % args.recipename)

    if args.strip and not args.dry_run:
        # Fakeroot copy to new destination
        srcdir = recipe_outdir
        recipe_outdir = os.path.join(workdir, 'devtool-deploy-target-stripped')
        if os.path.isdir(recipe_outdir):
            exec_fakeroot_no_d(fakerootcmd, fakerootenv, path, "rm -rf %s" % recipe_outdir,
                                env_overrides={'PSEUDO_INCLUDE_PATHS': recipe_outdir}, shell=True)
        exec_fakeroot_no_d(fakerootcmd, fakerootenv, path, "cp -af %s %s" % (os.path.join(srcdir, '.'), recipe_outdir),
                            env_overrides={'PSEUDO_INCLUDE_PATHS': '%s,%s' % (srcdir, recipe_outdir)}, shell=True)

        # Strip under pseudo so that it records any inode replacements made by
        # the strip tool before the deployment tar reads this directory.
        strip_script = (
            'import sys\n'
            'sys.path[:] = %r\n'
            'import oe.package\n'
            'oe.package.strip_execs(%r, %r, %r, %r, %r, %r)\n'
        ) % (sys.path, args.recipename, recipe_outdir, strip_cmd, libdir,
             base_libdir, max_process)
        ret = exec_fakeroot_no_d(
            fakerootcmd, fakerootenv, path,
            '%s -c %s' % (shlex.quote(sys.executable), shlex.quote(strip_script)),
            env_overrides={'PSEUDO_INCLUDE_PATHS': recipe_outdir}, shell=True)
        if ret != 0:
            raise DevtoolError('Failed to strip files for deployment')

    allowed_files = None
    default_excluded_files = set()
    file_sets = []
    if file_globs:
        deploy_files = _match_deploy_files(recipe_outdir, file_globs, getattr(args, 'recipename', None))
        if deploy_files is not None:
            file_sets.append(deploy_files)
    if packages_files:
        package_files, default_excluded_files = _match_package_files(recipe_outdir, packages_files, getattr(args, 'package', None),
                                            getattr(args, 'recipename', None))
        if package_files is not None:
            file_sets.append(package_files)
    if file_sets:
        allowed_files = set().union(*file_sets)

    filelist = []
    tar_relpaths = []
    inodes = set({})
    ftotalsize = 0
    for root, _, files in os.walk(recipe_outdir):
        for fn in files:
            relpath = os.path.normpath(os.path.join(os.path.relpath(root, recipe_outdir), fn))
            if allowed_files is not None:
                if relpath not in allowed_files:
                    continue
            elif relpath in default_excluded_files:
                # No explicit --package/--file-glob filter was given: still leave
                # out packages like -staticdev that aren't needed on a live target.
                continue
            fstat = os.lstat(os.path.join(root, fn))
            # Get the size in kiB (since we'll be comparing it to the output of du -k)
            # MUST use lstat() here not stat() or getfilesize() since we don't want to
            # dereference symlinks
            if fstat.st_ino in inodes:
                fsize = 0
            else:
                fsize = int(math.ceil(float(fstat.st_size)/1024))
            inodes.add(fstat.st_ino)
            ftotalsize += fsize
            # The path as it would appear on the target
            fpath = os.path.join(destdir, os.path.relpath(root, recipe_outdir), fn)
            filelist.append((fpath, fsize))
            tar_relpaths.append(relpath)

    if allowed_files is not None and not filelist:
        raise DevtoolError('No files to deploy for %s - the --package/--file-glob '
                        'filter(s) did not match any of the files installed by this '
                        'recipe.' % args.recipename)

    if args.dry_run:
        print('Files to be deployed for %s on target %s:' % (args.recipename, args.target))
        for item, _ in filelist:
            print('  %s' % item)
        return 0

    if os.path.isabs(args.target):
        # A local directory (e.g. an NFS-exported rootfs) rather than a
        # user@host ssh target: copy the files in directly, no network needed.
        return _deploy_local(args, destdir, filelist, ftotalsize, tar_relpaths,
                            allowed_files, fakerootcmd, fakerootenv, path, recipe_outdir)

    return _deploy_ssh(args, destdir, filelist, ftotalsize, tar_relpaths, allowed_files,
                       fakerootcmd, fakerootenv, path, recipe_outdir)

def _deploy_local(args, destdir, filelist, ftotalsize, tar_relpaths, allowed_files,
                fakerootcmd, fakerootenv, path, recipe_outdir):
    """Copy files directly into destdir instead of over ssh/scp.

    destdir is the local pseudo-managed rootfs directory itself (no trailing
    slash), not the real filesystem root.
    """
    if not os.path.isdir(destdir):
        raise DevtoolError('Target directory %s does not exist' % destdir)
    state_dir = destdir + '.pseudo_state'
    if not os.path.isdir(state_dir):
        raise DevtoolError(
            '%s does not exist - %s does not look like a pseudo-managed rootfs '
            '(e.g. one extracted by runqemu-extract-sdk).' % (state_dir, destdir))

    if not args.no_check_space:
        freespace = shutil.disk_usage(destdir).free // 1024
        if ftotalsize > freespace:
            raise DevtoolError('Deploy failed - insufficient space on target '
                            '(available %d, needed %d)' % (freespace, ftotalsize))

    shellscript = _prepare_remote_script(deploy=True,
                                        destdir=destdir,
                                        verbose=args.show_status,
                                        nopreserve=args.no_preserve,
                                        nocheckspace=True)

    tmpdir = tempfile.mkdtemp(prefix='devtool')
    tar_send_filelist_path = None
    try:
        script_path = os.path.join(tmpdir, 'devtool_deploy.sh')
        with open(script_path, 'w') as f:
            f.write(shellscript)
        filelist_path = os.path.join(tmpdir, 'devtool_deploy.list')
        with open(filelist_path, 'w') as f:
            f.write('%d\n' % ftotalsize)
            for fpath, fsize in filelist:
                f.write('%s %d\n' % (fpath, fsize))

        # tar_send_* builds up the sending side of the pipe: the plain tar
        # invocation, then wrapped to capture its own exit status (only the
        # last stage of a shell pipeline is visible to subprocess), then
        # wrapped again to run under its own pseudo instance.
        if allowed_files is not None:
            tar_send_fd, tar_send_filelist_path = tempfile.mkstemp(prefix='devtool-deploy-filelist-')
            with os.fdopen(tar_send_fd, 'w') as f:
                for relpath in tar_relpaths:
                    f.write('./' + relpath + '\n')
            tar_send_argv = 'tar cf - -T %s' % shlex.quote(tar_send_filelist_path)
        else:
            tar_send_argv = 'tar cf - .'

        tar_send_status_path = os.path.join(tmpdir, 'devtool_deploy.tar_status')
        tar_send_script = 'sh -c %s' % shlex.quote(
            '%s; echo $? > %s' % (tar_send_argv, shlex.quote(tar_send_status_path)))
        tar_send_cmd = 'PSEUDO_INCLUDE_PATHS=%s %s %s' % (
            shlex.quote(recipe_outdir), shlex.quote(fakerootcmd), tar_send_script)

        # tar_receive_cmd is the other side of the pipe: extracts into destdir
        # under the target rootfs's own pseudo database (state_dir/destdir,
        # not the recipe's).
        # $2 needs a trailing slash: the script's manifest substitution
        # (sed 's!^./!$2!') turns tar's './relative' entries into absolute paths.
        tar_receive_cmd = 'PSEUDO_LOCALSTATEDIR=%s PSEUDO_INCLUDE_PATHS=%s %s sh %s %s %s %s' % (
            shlex.quote(state_dir), shlex.quote(destdir), shlex.quote(fakerootcmd),
            shlex.quote(script_path), shlex.quote(args.recipename),
            shlex.quote(destdir.rstrip('/') + '/'), shlex.quote(filelist_path))

        if not os.path.exists(fakerootcmd):
            logger.error('pseudo executable %s could not be found - have you run a build '
                        'yet? pseudo-native should install this and if you have run any '
                        'build then that should have been built' % fakerootcmd)
            ret = 2
        else:
            shell_env = build_fakeroot_env_no_d(fakerootenv, path)
            ret = subprocess.call('%s | %s' % (tar_send_cmd, tar_receive_cmd), env=shell_env,
                                cwd=recipe_outdir, shell=True)
            if ret == 0 and os.path.exists(tar_send_status_path):
                with open(tar_send_status_path) as f:
                    ret = int(f.read().strip() or 0)
    finally:
        if tar_send_filelist_path:
            os.remove(tar_send_filelist_path)
        shutil.rmtree(tmpdir)

    if ret != 0:
        raise DevtoolError('Deploy failed - rerun with -s to get a complete '
                        'error message')

    logger.info('Successfully deployed %s to %s' % (recipe_outdir, destdir))
    return 0

def _deploy_ssh(args, destdir, filelist, ftotalsize, tar_relpaths, allowed_files,
                fakerootcmd, fakerootenv, path, recipe_outdir):
    """Copy files to target_dir over ssh/scp (user@hostname[:destdir])."""
    extraoptions = ''
    if args.no_host_check:
        extraoptions += '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
    if not args.show_status:
        extraoptions += ' -q'

    scp_sshexec = ''
    ssh_sshexec = 'ssh'
    if args.ssh_exec:
        scp_sshexec = "-S %s" % args.ssh_exec
        ssh_sshexec = args.ssh_exec
    scp_port = ''
    ssh_port = ''
    if args.port:
        scp_port = "-P %s" % args.port
        ssh_port = "-p %s" % args.port

    if args.key:
        extraoptions += ' -i %s' % args.key

    # In order to delete previously deployed files and have the manifest file on
    # the target, we write out a shell script and then copy it to the target
    # so we can then run it (piping tar output to it).
    # (We cannot use scp here, because it doesn't preserve symlinks.)
    tmpdir = tempfile.mkdtemp(prefix='devtool')
    try:
        tmpscript = '/tmp/devtool_deploy.sh'
        tmpfilelist = os.path.join(os.path.dirname(tmpscript), 'devtool_deploy.list')
        shellscript = _prepare_remote_script(deploy=True,
                                            destdir=destdir,
                                            verbose=args.show_status,
                                            nopreserve=args.no_preserve,
                                            nocheckspace=args.no_check_space)
        # Write out the script to a file
        with open(os.path.join(tmpdir, os.path.basename(tmpscript)), 'w') as f:
            f.write(shellscript)
        # Write out the file list
        with open(os.path.join(tmpdir, os.path.basename(tmpfilelist)), 'w') as f:
            f.write('%d\n' % ftotalsize)
            for fpath, fsize in filelist:
                f.write('%s %d\n' % (fpath, fsize))
        # Copy them to the target
        ret = subprocess.call("scp %s %s %s %s/* %s:%s" % (scp_sshexec, scp_port, extraoptions, tmpdir, args.target, os.path.dirname(tmpscript)), shell=True)
        if ret != 0:
            raise DevtoolError('Failed to copy script to %s - rerun with -s to '
                            'get a complete error message' % args.target)
    finally:
        shutil.rmtree(tmpdir)

    # Now run the script. When a package/glob filter narrowed down filelist,
    # tar is given an explicit list of relative paths (-T) instead of packing
    # the whole recipe_outdir tree.
    tar_filelist_path = None
    try:
        if allowed_files is not None:
            tar_fd, tar_filelist_path = tempfile.mkstemp(prefix='devtool-deploy-filelist-')
            with os.fdopen(tar_fd, 'w') as f:
                for relpath in tar_relpaths:
                    # './' prefix matches what 'tar cf - .' itself would produce, which
                    # the remote script's manifest handling (sed "s!^./!$2!") relies on.
                    f.write('./' + relpath + '\n')
            tar_cmd = 'tar cf - -T %s' % shlex.quote(tar_filelist_path)
        else:
            tar_cmd = 'tar cf - .'
        # $2 needs a trailing slash: the script's manifest substitution
        # (sed 's!^./!$2!') turns tar's './relative' entries into absolute paths.
        remote_cmd = '%s | %s  %s %s %s \'sh %s %s %s %s\'' % (
            tar_cmd, ssh_sshexec, ssh_port, extraoptions, args.target,
            tmpscript, args.recipename, destdir.rstrip('/') + '/', tmpfilelist)
        ret = exec_fakeroot_no_d(fakerootcmd, fakerootenv, path, remote_cmd, cwd=recipe_outdir,
                                env_overrides={'PSEUDO_INCLUDE_PATHS': recipe_outdir}, shell=True)
    finally:
        if tar_filelist_path:
            os.remove(tar_filelist_path)
    if ret != 0:
        raise DevtoolError('Deploy failed - rerun with -s to get a complete '
                        'error message')

    logger.info('Successfully deployed %s' % recipe_outdir)

    return 0

def undeploy(args, config, basepath, workspace):
    """Entry point for the devtool 'undeploy' subcommand"""
    if args.all and args.recipename:
        raise argparse_oe.ArgumentUsageError('Cannot specify -a/--all with a recipe name', 'undeploy-target')
    elif not args.recipename and not args.all:
        raise argparse_oe.ArgumentUsageError('If you don\'t specify a recipe, you must specify -a/--all', 'undeploy-target')

    if os.path.isabs(args.target):
        # A local directory (e.g. an NFS-exported rootfs) rather than a
        # user@host ssh target: remove the files in directly, no network needed.
        tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
        try:
            fakerootcmd = tinfoil.config_data.getVar('FAKEROOTCMD')
            fakerootenv = tinfoil.config_data.getVar('FAKEROOTENV')
            path = tinfoil.config_data.getVar('PATH')
        finally:
            tinfoil.shutdown()
        return _undeploy_local(args, os.path.realpath(args.target), fakerootcmd, fakerootenv, path)

    return _undeploy_ssh(args)

def _undeploy_local(args, target_dir, fakerootcmd, fakerootenv, path):
    """Remove files directly from target_dir instead of over ssh."""
    if not os.path.isdir(target_dir):
        raise DevtoolError('Target directory %s does not exist' % target_dir)
    state_dir = target_dir + '.pseudo_state'
    if not os.path.isdir(state_dir):
        raise DevtoolError(
            '%s does not exist - %s does not look like a pseudo-managed rootfs '
            '(e.g. one extracted by runqemu-extract-sdk).' % (state_dir, target_dir))

    # deploy=False here: the generated script never touches $2, so target_dir
    # doesn't need the trailing slash _with_trailing_slash() adds for deploy.
    shellscript = _prepare_remote_script(deploy=False, destdir=target_dir, dryrun=args.dry_run, undeployall=args.all)

    tmpdir = tempfile.mkdtemp(prefix='devtool')
    try:
        script_path = os.path.join(tmpdir, 'devtool_undeploy.sh')
        with open(script_path, 'w') as f:
            f.write(shellscript)

        environment = dict(os.environ)
        environment['PATH'] = path
        for varvalue in (fakerootenv or '').split():
            if '=' in varvalue:
                key, value = varvalue.split('=', 1)
                environment[key] = value
        # Use target_dir's own pseudo database, not the ambient one from FAKEROOTENV,
        # so file removals stay consistent with what was recorded on deploy/extract.
        environment['PSEUDO_LOCALSTATEDIR'] = state_dir
        environment['PSEUDO_INCLUDE_PATHS'] = target_dir
        command = [fakerootcmd, 'sh', script_path, args.recipename or '']
        ret = subprocess.call(command, env=environment)
    finally:
        shutil.rmtree(tmpdir)

    if ret != 0:
        # Unlike the ssh case there is nothing -s could add here, the script
        # runs locally and its output is already on the console.
        raise DevtoolError('Undeploy failed - see the output above for details')

    if not args.all and not args.dry_run:
        logger.info('Successfully undeployed %s' % args.recipename)
    return 0

def _undeploy_ssh(args):
    """Run the undeploy script on the target over ssh/scp (user@hostname[:destdir])."""
    extraoptions = ''
    if args.no_host_check:
        extraoptions += '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
    if not args.show_status:
        extraoptions += ' -q'

    scp_sshexec = ''
    ssh_sshexec = 'ssh'
    if args.ssh_exec:
        scp_sshexec = "-S %s" % args.ssh_exec
        ssh_sshexec = args.ssh_exec
    scp_port = ''
    ssh_port = ''
    if args.port:
        scp_port = "-P %s" % args.port
        ssh_port = "-p %s" % args.port

    try:
        host, destdir = args.target.split(':')
    except ValueError:
        destdir = '/'
    else:
        args.target = host

    tmpdir = tempfile.mkdtemp(prefix='devtool')
    try:
        tmpscript = '/tmp/devtool_undeploy.sh'
        shellscript = _prepare_remote_script(deploy=False, destdir=destdir, dryrun=args.dry_run, undeployall=args.all)
        # Write out the script to a file
        with open(os.path.join(tmpdir, os.path.basename(tmpscript)), 'w') as f:
            f.write(shellscript)
        # Copy it to the target
        ret = subprocess.call("scp %s %s %s %s/* %s:%s" % (scp_sshexec, scp_port, extraoptions, tmpdir, args.target, os.path.dirname(tmpscript)), shell=True)
        if ret != 0:
            raise DevtoolError('Failed to copy script to %s - rerun with -s to '
                                'get a complete error message' % args.target)
    finally:
        shutil.rmtree(tmpdir)

    # Now run the script
    ret = subprocess.call('%s %s %s %s \'sh %s %s\'' % (ssh_sshexec, ssh_port, extraoptions, args.target, tmpscript, args.recipename), shell=True)
    if ret != 0:
        raise DevtoolError('Undeploy failed - rerun with -s to get a complete '
                           'error message')

    if not args.all and not args.dry_run:
        logger.info('Successfully undeployed %s' % args.recipename)
    return 0


def register_commands(subparsers, context):
    """Register devtool subcommands from the deploy plugin"""

    parser_deploy = subparsers.add_parser('deploy-target',
                                          help='Deploy recipe output files to live target machine',
                                          description='Deploys a recipe\'s build output (i.e. the output of '
                                                      'the do_install task) to a live target machine over ssh, '
                                                      'or directly into a local pseudo-managed rootfs directory '
                                                      '(e.g. one extracted for NFS booting). Existing files are '
                                                      'preserved by default and restored by devtool '
                                                      'undeploy-target. Only the recipe itself is deployed, not '
                                                      'its runtime dependencies. Use --package/--file-glob to '
                                                      'deploy only a subset of the recipe\'s installed files.',
                                          group='testbuild')
    parser_deploy.add_argument('recipename', help='Recipe to deploy')
    parser_deploy.add_argument('target',
                               help='Either a live target machine running an ssh server: '
                               'user@hostname[:destdir]; or an absolute path to a local '
                               'pseudo-managed rootfs directory (e.g. one extracted by '
                               'runqemu-extract-sdk) to copy the files into directly, without ssh.')
    parser_deploy.add_argument('-c', '--no-host-check', help='Disable ssh host key checking', action='store_true')
    parser_deploy.add_argument('-s', '--show-status', help='Show progress/status output', action='store_true')
    parser_deploy.add_argument('-n', '--dry-run', help='List files to be deployed only', action='store_true')
    parser_deploy.add_argument('-p', '--no-preserve', help='Do not preserve existing files', action='store_true')
    parser_deploy.add_argument('--no-check-space', help='Do not check for available space before deploying', action='store_true')
    parser_deploy.add_argument('-e', '--ssh-exec', help='Executable to use in place of ssh')
    parser_deploy.add_argument('-P', '--port', help='Specify port to use for connection to the target')
    parser_deploy.add_argument('-I', '--key',
                               help='Specify ssh private key for connection to the target')
    parser_deploy.add_argument('--package', action='append', metavar='PACKAGE',
                               help='Only deploy files belonging to PACKAGE, as defined by that '
                                    'package\'s FILES variable in the recipe metadata. May be a '
                                    'comma-separated list and/or specified multiple times to '
                                    'include several packages. May be prefixed with "RECIPE:" (must '
                                    'match RECIPENAME), e.g. "RECIPE:,-doc,-ptest" is short for '
                                    '"RECIPE,RECIPE-doc,RECIPE-ptest".')
    parser_deploy.add_argument('--file-glob', action='append', dest='file_globs', metavar='GLOB',
                               help='Only deploy files whose installed path matches this glob '
                                    'pattern (e.g. "/usr/bin/*" or "${bindir}/myprog"). May be '
                                    'specified multiple times. Combined with --package if both are given.')

    strip_opts = parser_deploy.add_mutually_exclusive_group(required=False)
    strip_opts.add_argument('-S', '--strip',
                               help='Strip executables prior to deploying (default: %(default)s). '
                                    'The default value of this option can be controlled by setting the strip option in the [Deploy] section to True or False.',
                               default=oe.types.boolean(context.config.get('Deploy', 'strip', default='0')),
                               action='store_true')
    strip_opts.add_argument('--no-strip', help='Do not strip executables prior to deploy', dest='strip', action='store_false')

    parser_deploy.set_defaults(func=deploy)

    parser_undeploy = subparsers.add_parser('undeploy-target',
                                            help='Undeploy recipe output files in live target machine',
                                            description='Un-deploys recipe output files previously deployed to a live target machine or local '
                                                        'pseudo-managed rootfs directory by devtool deploy-target.',
                                            group='testbuild')
    parser_undeploy.add_argument('recipename', help='Recipe to undeploy (if not using -a/--all)', nargs='?')
    parser_undeploy.add_argument('target',
                               help='Either a live target machine running an ssh server: '
                               'user@hostname; or an absolute path to the local pseudo-managed '
                               'rootfs directory previously used with deploy-target, to remove '
                               'the files directly, without ssh.')
    parser_undeploy.add_argument('-c', '--no-host-check', help='Disable ssh host key checking', action='store_true')
    parser_undeploy.add_argument('-s', '--show-status', help='Show progress/status output', action='store_true')
    parser_undeploy.add_argument('-a', '--all', help='Undeploy all recipes deployed on the target', action='store_true')
    parser_undeploy.add_argument('-n', '--dry-run', help='List files to be undeployed only', action='store_true')
    parser_undeploy.add_argument('-e', '--ssh-exec', help='Executable to use in place of ssh')
    parser_undeploy.add_argument('-P', '--port', help='Specify port to use for connection to the target')
    parser_undeploy.add_argument('-I', '--key',
                               help='Specify ssh private key for connection to the target')

    parser_undeploy.set_defaults(func=undeploy)
