#!/usr/bin/env python

# Development tool - utility functions for plugins
#
# Copyright (C) 2014 Intel Corporation
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
"""Devtool plugins module"""

import os
import sys
import subprocess
import logging
import re

logger = logging.getLogger('devtool')


class DevtoolError(Exception):
    """Exception for handling devtool errors"""
    pass


def exec_build_env_command(init_path, builddir, cmd, watch=False, **options):
    """Run a program in bitbake build context"""
    import bb
    if not 'cwd' in options:
        options["cwd"] = builddir
    if init_path:
        # As the OE init script makes use of BASH_SOURCE to determine OEROOT,
        # and can't determine it when running under dash, we need to set
        # the executable to bash to correctly set things up
        if not 'executable' in options:
            options['executable'] = 'bash'
        logger.debug('Executing command: "%s" using init path %s' % (cmd, init_path))
        init_prefix = '. %s %s > /dev/null && ' % (init_path, builddir)
    else:
        logger.debug('Executing command "%s"' % cmd)
        init_prefix = ''
    if watch:
        if sys.stdout.isatty():
            # Fool bitbake into thinking it's outputting to a terminal (because it is, indirectly)
            cmd = 'script -e -q -c "%s" /dev/null' % cmd
        return exec_watch('%s%s' % (init_prefix, cmd), **options)
    else:
        return bb.process.run('%s%s' % (init_prefix, cmd), **options)

def exec_watch(cmd, **options):
    """Run program with stdout shown on sys.stdout"""
    import bb
    if isinstance(cmd, basestring) and not "shell" in options:
        options["shell"] = True

    process = subprocess.Popen(
        cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, **options
    )

    buf = ''
    while True:
        out = process.stdout.read(1)
        if out:
            sys.stdout.write(out)
            sys.stdout.flush()
            buf += out
        elif out == '' and process.poll() != None:
            break

    if process.returncode != 0:
        raise bb.process.ExecutionError(cmd, process.returncode, buf, None)

    return buf, None

def exec_fakeroot(d, cmd, **kwargs):
    """Run a command under fakeroot (pseudo, in fact) so that it picks up the appropriate file permissions"""
    # Grab the command and check it actually exists
    fakerootcmd = d.getVar('FAKEROOTCMD', True)
    if not os.path.exists(fakerootcmd):
        logger.error('pseudo executable %s could not be found - have you run a build yet? pseudo-native should install this and if you have run any build then that should have been built')
        return 2
    # Set up the appropriate environment
    newenv = dict(os.environ)
    fakerootenv = d.getVar('FAKEROOTENV', True)
    for varvalue in fakerootenv.split():
        if '=' in varvalue:
            splitval = varvalue.split('=', 1)
            newenv[splitval[0]] = splitval[1]
    return subprocess.call("%s %s" % (fakerootcmd, cmd), env=newenv, **kwargs)

def setup_tinfoil(config_only=False, basepath=None, tracking=False):
    """Initialize tinfoil api from bitbake"""
    import scriptpath
    orig_cwd = os.path.abspath(os.curdir)
    try:
        if basepath:
            os.chdir(basepath)
        bitbakepath = scriptpath.add_bitbake_lib_path()
        if not bitbakepath:
            logger.error("Unable to find bitbake by searching parent directory of this script or PATH")
            sys.exit(1)

        import bb.tinfoil
        tinfoil = bb.tinfoil.Tinfoil(tracking=tracking)
        tinfoil.prepare(config_only)
        tinfoil.logger.setLevel(logger.getEffectiveLevel())
    finally:
        os.chdir(orig_cwd)
    return tinfoil

def get_recipe_file(cooker, pn):
    """Find recipe file corresponding a package name"""
    import oe.recipeutils
    recipefile = oe.recipeutils.pn_to_recipe(cooker, pn)
    if not recipefile:
        skipreasons = oe.recipeutils.get_unavailable_reasons(cooker, pn)
        if skipreasons:
            logger.error('\n'.join(skipreasons))
        else:
            logger.error("Unable to find any recipe file matching %s" % pn)
    return recipefile

def parse_recipe(config, tinfoil, pn, appends, filter_workspace=True):
    """Parse recipe of a package"""
    import oe.recipeutils
    recipefile = get_recipe_file(tinfoil.cooker, pn)
    if not recipefile:
        # Error already logged
        return None
    if appends:
        append_files = tinfoil.cooker.collection.get_file_appends(recipefile)
        if filter_workspace:
            # Filter out appends from the workspace
            append_files = [path for path in append_files if
                            not path.startswith(config.workspace_path)]
    else:
        append_files = None
    return oe.recipeutils.parse_recipe(recipefile, append_files,
                                       tinfoil.config_data)

def check_workspace_recipe(workspace, pn, checksrc=True, bbclassextend=False):
    """
    Check that a recipe is in the workspace and (optionally) that source
    is present.
    """

    workspacepn = pn

    for recipe, value in workspace.iteritems():
        if recipe == pn:
            break
        if bbclassextend:
            recipefile = value['recipefile']
            if recipefile:
                targets = get_bbclassextend_targets(recipefile, recipe)
                if pn in targets:
                    workspacepn = recipe
                    break
    else:
        raise DevtoolError("No recipe named '%s' in your workspace" % pn)

    if checksrc:
        srctree = workspace[workspacepn]['srctree']
        if not os.path.exists(srctree):
            raise DevtoolError("Source tree %s for recipe %s does not exist" % (srctree, workspacepn))
        if not os.listdir(srctree):
            raise DevtoolError("Source tree %s for recipe %s is empty" % (srctree, workspacepn))

    return workspacepn

def use_external_build(same_dir, no_same_dir, d):
    """
    Determine if we should use B!=S (separate build and source directories) or not
    """
    b_is_s = True
    if no_same_dir:
        logger.info('Using separate build directory since --no-same-dir specified')
        b_is_s = False
    elif same_dir:
        logger.info('Using source tree as build directory since --same-dir specified')
    elif bb.data.inherits_class('autotools-brokensep', d):
        logger.info('Using source tree as build directory since recipe inherits autotools-brokensep')
    elif d.getVar('B', True) == os.path.abspath(d.getVar('S', True)):
        logger.info('Using source tree as build directory since that would be the default for this recipe')
    else:
        b_is_s = False
    return b_is_s

def setup_git_repo(repodir, version, devbranch, basetag='devtool-base'):
    """
    Set up the git repository for the source tree
    """
    import bb.process
    if not os.path.exists(os.path.join(repodir, '.git')):
        bb.process.run('git init', cwd=repodir)
        bb.process.run('git add .', cwd=repodir)
        commit_cmd = ['git', 'commit', '-q']
        stdout, _ = bb.process.run('git status --porcelain', cwd=repodir)
        if not stdout:
            commit_cmd.append('--allow-empty')
            commitmsg = "Initial empty commit with no upstream sources"
        elif version:
            commitmsg = "Initial commit from upstream at version %s" % version
        else:
            commitmsg = "Initial commit from upstream"
        commit_cmd += ['-m', commitmsg]
        bb.process.run(commit_cmd, cwd=repodir)

    bb.process.run('git checkout -b %s' % devbranch, cwd=repodir)
    bb.process.run('git tag -f %s' % basetag, cwd=repodir)

def recipe_to_append(recipefile, config, wildcard=False):
    """
    Convert a recipe file to a bbappend file path within the workspace.
    NOTE: if the bbappend already exists, you should be using
    workspace[args.recipename]['bbappend'] instead of calling this
    function.
    """
    appendname = os.path.splitext(os.path.basename(recipefile))[0]
    if wildcard:
        appendname = re.sub(r'_.*', '_%', appendname)
    appendpath = os.path.join(config.workspace_path, 'appends')
    appendfile = os.path.join(appendpath, appendname + '.bbappend')
    return appendfile

def get_bbclassextend_targets(recipefile, pn):
    """
    Cheap function to get BBCLASSEXTEND and then convert that to the
    list of targets that would result.
    """
    import bb.utils

    values = {}
    def get_bbclassextend_varfunc(varname, origvalue, op, newlines):
        values[varname] = origvalue
        return origvalue, None, 0, True
    with open(recipefile, 'r') as f:
        bb.utils.edit_metadata(f, ['BBCLASSEXTEND'], get_bbclassextend_varfunc)

    targets = []
    bbclassextend = values.get('BBCLASSEXTEND', '').split()
    if bbclassextend:
        for variant in bbclassextend:
            if variant == 'nativesdk':
                targets.append('%s-%s' % (variant, pn))
            elif variant in ['native', 'cross', 'crosssdk']:
                targets.append('%s-%s' % (pn, variant))
    return targets
