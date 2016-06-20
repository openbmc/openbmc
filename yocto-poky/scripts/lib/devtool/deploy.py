# Development tool - deploy/undeploy command plugin
#
# Copyright (C) 2014-2016 Intel Corporation
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
"""Devtool plugin containing the deploy subcommands"""

import os
import subprocess
import logging
import tempfile
import shutil
import argparse_oe
from devtool import exec_fakeroot, setup_tinfoil, check_workspace_recipe, DevtoolError

logger = logging.getLogger('devtool')

deploylist_path = '/.devtool'

def _prepare_remote_script(deploy, verbose=False, dryrun=False, undeployall=False, nopreserve=False, nocheckspace=False):
    """
    Prepare a shell script for running on the target to
    deploy/undeploy files. We have to be careful what we put in this
    script - only commands that are likely to be available on the
    target are suitable (the target might be constrained, e.g. using
    busybox rather than bash with coreutils).
    """
    lines = []
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
        lines.append('            rm $file')
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
            lines.append('freespace=`df -P $checkpath | sed "1d" | awk \'{ print $4 }\'`')
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
            lines.append('    tar xv -C $2 -f - | tee $manifest')
        else:
            lines.append('    tar xv -C $2 -f - > $manifest')
        lines.append('sed -i "s!^./!$2!" $manifest')
    elif not dryrun:
        # Put any preserved files back
        lines.append('if [ -d $preservedir ] ; then')
        lines.append('    cd $preservedir')
        lines.append('    find . -type f -exec mv {} /{} \;')
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


def deploy(args, config, basepath, workspace):
    """Entry point for the devtool 'deploy' subcommand"""
    import re
    import math
    import oe.recipeutils

    check_workspace_recipe(workspace, args.recipename, checksrc=False)

    try:
        host, destdir = args.target.split(':')
    except ValueError:
        destdir = '/'
    else:
        args.target = host
    if not destdir.endswith('/'):
        destdir += '/'

    tinfoil = setup_tinfoil(basepath=basepath)
    try:
        rd = oe.recipeutils.parse_recipe_simple(tinfoil.cooker, args.recipename, tinfoil.config_data)
    except Exception as e:
        raise DevtoolError('Exception parsing recipe %s: %s' %
                           (args.recipename, e))
    recipe_outdir = rd.getVar('D', True)
    if not os.path.exists(recipe_outdir) or not os.listdir(recipe_outdir):
        raise DevtoolError('No files to deploy - have you built the %s '
                           'recipe? If so, the install step has not installed '
                           'any files.' % args.recipename)

    filelist = []
    ftotalsize = 0
    for root, _, files in os.walk(recipe_outdir):
        for fn in files:
            # Get the size in kiB (since we'll be comparing it to the output of du -k)
            # MUST use lstat() here not stat() or getfilesize() since we don't want to
            # dereference symlinks
            fsize = int(math.ceil(float(os.lstat(os.path.join(root, fn)).st_size)/1024))
            ftotalsize += fsize
            # The path as it would appear on the target
            fpath = os.path.join(destdir, os.path.relpath(root, recipe_outdir), fn)
            filelist.append((fpath, fsize))

    if args.dry_run:
        print('Files to be deployed for %s on target %s:' % (args.recipename, args.target))
        for item, _ in filelist:
            print('  %s' % item)
        return 0


    extraoptions = ''
    if args.no_host_check:
        extraoptions += '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
    if not args.show_status:
        extraoptions += ' -q'

    # In order to delete previously deployed files and have the manifest file on
    # the target, we write out a shell script and then copy it to the target
    # so we can then run it (piping tar output to it).
    # (We cannot use scp here, because it doesn't preserve symlinks.)
    tmpdir = tempfile.mkdtemp(prefix='devtool')
    try:
        tmpscript = '/tmp/devtool_deploy.sh'
        tmpfilelist = os.path.join(os.path.dirname(tmpscript), 'devtool_deploy.list')
        shellscript = _prepare_remote_script(deploy=True,
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
        ret = subprocess.call("scp %s %s/* %s:%s" % (extraoptions, tmpdir, args.target, os.path.dirname(tmpscript)), shell=True)
        if ret != 0:
            raise DevtoolError('Failed to copy script to %s - rerun with -s to '
                            'get a complete error message' % args.target)
    finally:
        shutil.rmtree(tmpdir)

    # Now run the script
    ret = exec_fakeroot(rd, 'tar cf - . | ssh %s %s \'sh %s %s %s %s\'' % (extraoptions, args.target, tmpscript, args.recipename, destdir, tmpfilelist), cwd=recipe_outdir, shell=True)
    if ret != 0:
        raise DevtoolError('Deploy failed - rerun with -s to get a complete '
                           'error message')

    logger.info('Successfully deployed %s' % recipe_outdir)

    files_list = []
    for root, _, files in os.walk(recipe_outdir):
        for filename in files:
            filename = os.path.relpath(os.path.join(root, filename), recipe_outdir)
            files_list.append(os.path.join(destdir, filename))

    return 0

def undeploy(args, config, basepath, workspace):
    """Entry point for the devtool 'undeploy' subcommand"""
    if args.all and args.recipename:
        raise argparse_oe.ArgumentUsageError('Cannot specify -a/--all with a recipe name', 'undeploy-target')
    elif not args.recipename and not args.all:
        raise argparse_oe.ArgumentUsageError('If you don\'t specify a recipe, you must specify -a/--all', 'undeploy-target')

    extraoptions = ''
    if args.no_host_check:
        extraoptions += '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
    if not args.show_status:
        extraoptions += ' -q'

    args.target = args.target.split(':')[0]

    tmpdir = tempfile.mkdtemp(prefix='devtool')
    try:
        tmpscript = '/tmp/devtool_undeploy.sh'
        shellscript = _prepare_remote_script(deploy=False, dryrun=args.dry_run, undeployall=args.all)
        # Write out the script to a file
        with open(os.path.join(tmpdir, os.path.basename(tmpscript)), 'w') as f:
            f.write(shellscript)
        # Copy it to the target
        ret = subprocess.call("scp %s %s/* %s:%s" % (extraoptions, tmpdir, args.target, os.path.dirname(tmpscript)), shell=True)
        if ret != 0:
            raise DevtoolError('Failed to copy script to %s - rerun with -s to '
                                'get a complete error message' % args.target)
    finally:
        shutil.rmtree(tmpdir)

    # Now run the script
    ret = subprocess.call('ssh %s %s \'sh %s %s\'' % (extraoptions, args.target, tmpscript, args.recipename), shell=True)
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
                                          description='Deploys a recipe\'s build output (i.e. the output of the do_install task) to a live target machine over ssh. By default, any existing files will be preserved instead of being overwritten and will be restored if you run devtool undeploy-target. Note: this only deploys the recipe itself and not any runtime dependencies, so it is assumed that those have been installed on the target beforehand.',
                                          group='testbuild')
    parser_deploy.add_argument('recipename', help='Recipe to deploy')
    parser_deploy.add_argument('target', help='Live target machine running an ssh server: user@hostname[:destdir]')
    parser_deploy.add_argument('-c', '--no-host-check', help='Disable ssh host key checking', action='store_true')
    parser_deploy.add_argument('-s', '--show-status', help='Show progress/status output', action='store_true')
    parser_deploy.add_argument('-n', '--dry-run', help='List files to be deployed only', action='store_true')
    parser_deploy.add_argument('-p', '--no-preserve', help='Do not preserve existing files', action='store_true')
    parser_deploy.add_argument('--no-check-space', help='Do not check for available space before deploying', action='store_true')
    parser_deploy.set_defaults(func=deploy)

    parser_undeploy = subparsers.add_parser('undeploy-target',
                                            help='Undeploy recipe output files in live target machine',
                                            description='Un-deploys recipe output files previously deployed to a live target machine by devtool deploy-target.',
                                            group='testbuild')
    parser_undeploy.add_argument('recipename', help='Recipe to undeploy (if not using -a/--all)', nargs='?')
    parser_undeploy.add_argument('target', help='Live target machine running an ssh server: user@hostname')
    parser_undeploy.add_argument('-c', '--no-host-check', help='Disable ssh host key checking', action='store_true')
    parser_undeploy.add_argument('-s', '--show-status', help='Show progress/status output', action='store_true')
    parser_undeploy.add_argument('-a', '--all', help='Undeploy all recipes deployed on the target', action='store_true')
    parser_undeploy.add_argument('-n', '--dry-run', help='List files to be undeployed only', action='store_true')
    parser_undeploy.set_defaults(func=undeploy)
