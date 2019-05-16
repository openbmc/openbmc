# Development tool - utility commands plugin
#
# Copyright (C) 2015-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""Devtool utility plugins"""

import os
import sys
import shutil
import tempfile
import logging
import argparse
import subprocess
import scriptutils
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, DevtoolError
from devtool import parse_recipe

logger = logging.getLogger('devtool')

def _find_recipe_path(args, config, basepath, workspace):
    if args.any_recipe:
        logger.warning('-a/--any-recipe option is now always active, and thus the option will be removed in a future release')
    if args.recipename in workspace:
        recipefile = workspace[args.recipename]['recipefile']
    else:
        recipefile = None
    if not recipefile:
        tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
        try:
            rd = parse_recipe(config, tinfoil, args.recipename, True)
            if not rd:
                raise DevtoolError("Failed to find specified recipe")
            recipefile = rd.getVar('FILE')
        finally:
            tinfoil.shutdown()
    return recipefile


def find_recipe(args, config, basepath, workspace):
    """Entry point for the devtool 'find-recipe' subcommand"""
    recipefile = _find_recipe_path(args, config, basepath, workspace)
    print(recipefile)
    return 0


def edit_recipe(args, config, basepath, workspace):
    """Entry point for the devtool 'edit-recipe' subcommand"""
    return scriptutils.run_editor(_find_recipe_path(args, config, basepath, workspace), logger)


def configure_help(args, config, basepath, workspace):
    """Entry point for the devtool 'configure-help' subcommand"""
    import oe.utils

    check_workspace_recipe(workspace, args.recipename)
    tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, appends=True, filter_workspace=False)
        if not rd:
            return 1
        b = rd.getVar('B')
        s = rd.getVar('S')
        configurescript = os.path.join(s, 'configure')
        confdisabled = 'noexec' in rd.getVarFlags('do_configure') or 'do_configure' not in (rd.getVar('__BBTASKS', False) or [])
        configureopts = oe.utils.squashspaces(rd.getVar('CONFIGUREOPTS') or '')
        extra_oeconf = oe.utils.squashspaces(rd.getVar('EXTRA_OECONF') or '')
        extra_oecmake = oe.utils.squashspaces(rd.getVar('EXTRA_OECMAKE') or '')
        do_configure = rd.getVar('do_configure') or ''
        do_configure_noexpand = rd.getVar('do_configure', False) or ''
        packageconfig = rd.getVarFlags('PACKAGECONFIG') or []
        autotools = bb.data.inherits_class('autotools', rd) and ('oe_runconf' in do_configure or 'autotools_do_configure' in do_configure)
        cmake = bb.data.inherits_class('cmake', rd) and ('cmake_do_configure' in do_configure)
        cmake_do_configure = rd.getVar('cmake_do_configure')
        pn = rd.getVar('PN')
    finally:
        tinfoil.shutdown()

    if 'doc' in packageconfig:
        del packageconfig['doc']

    if autotools and not os.path.exists(configurescript):
        logger.info('Running do_configure to generate configure script')
        try:
            stdout, _ = exec_build_env_command(config.init_path, basepath,
                                               'bitbake -c configure %s' % args.recipename,
                                               stderr=subprocess.STDOUT)
        except bb.process.ExecutionError:
            pass

    if confdisabled or do_configure.strip() in ('', ':'):
        raise DevtoolError("do_configure task has been disabled for this recipe")
    elif args.no_pager and not os.path.exists(configurescript):
        raise DevtoolError("No configure script found and no other information to display")
    else:
        configopttext = ''
        if autotools and configureopts:
            configopttext = '''
Arguments currently passed to the configure script:

%s

Some of those are fixed.''' % (configureopts + ' ' + extra_oeconf)
            if extra_oeconf:
                configopttext += ''' The ones that are specified through EXTRA_OECONF (which you can change or add to easily):

%s''' % extra_oeconf

        elif cmake:
            in_cmake = False
            cmake_cmd = ''
            for line in cmake_do_configure.splitlines():
                if in_cmake:
                    cmake_cmd = cmake_cmd + ' ' + line.strip().rstrip('\\')
                    if not line.endswith('\\'):
                        break
                if line.lstrip().startswith('cmake '):
                    cmake_cmd = line.strip().rstrip('\\')
                    if line.endswith('\\'):
                        in_cmake = True
                    else:
                        break
            if cmake_cmd:
                configopttext = '''
The current cmake command line:

%s

Arguments specified through EXTRA_OECMAKE (which you can change or add to easily)

%s''' % (oe.utils.squashspaces(cmake_cmd), extra_oecmake)
            else:
                configopttext = '''
The current implementation of cmake_do_configure:

cmake_do_configure() {
%s
}

Arguments specified through EXTRA_OECMAKE (which you can change or add to easily)

%s''' % (cmake_do_configure.rstrip(), extra_oecmake)

        elif do_configure:
            configopttext = '''
The current implementation of do_configure:

do_configure() {
%s
}''' % do_configure.rstrip()
            if '${EXTRA_OECONF}' in do_configure_noexpand:
                configopttext += '''

Arguments specified through EXTRA_OECONF (which you can change or add to easily):

%s''' % extra_oeconf

        if packageconfig:
            configopttext += '''

Some of these options may be controlled through PACKAGECONFIG; for more details please see the recipe.'''

        if args.arg:
            helpargs = ' '.join(args.arg)
        elif cmake:
            helpargs = '-LH'
        else:
            helpargs = '--help'

        msg = '''configure information for %s
------------------------------------------
%s''' % (pn, configopttext)

        if cmake:
            msg += '''

The cmake %s output for %s follows. After "-- Cache values" you should see a list of variables you can add to EXTRA_OECMAKE (prefixed with -D and suffixed with = followed by the desired value, without any spaces).
------------------------------------------''' % (helpargs, pn)
        elif os.path.exists(configurescript):
            msg += '''

The ./configure %s output for %s follows.
------------------------------------------''' % (helpargs, pn)

        olddir = os.getcwd()
        tmppath = tempfile.mkdtemp()
        with tempfile.NamedTemporaryFile('w', delete=False) as tf:
            if not args.no_header:
                tf.write(msg + '\n')
            tf.close()
            try:
                try:
                    cmd = 'cat %s' % tf.name
                    if cmake:
                        cmd += '; cmake %s %s 2>&1' % (helpargs, s)
                        os.chdir(b)
                    elif os.path.exists(configurescript):
                        cmd += '; %s %s' % (configurescript, helpargs)
                    if sys.stdout.isatty() and not args.no_pager:
                        pager = os.environ.get('PAGER', 'less')
                        cmd = '(%s) | %s' % (cmd, pager)
                    subprocess.check_call(cmd, shell=True)
                except subprocess.CalledProcessError as e:
                    return e.returncode
            finally:
                os.chdir(olddir)
                shutil.rmtree(tmppath)
                os.remove(tf.name)


def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_edit_recipe = subparsers.add_parser('edit-recipe', help='Edit a recipe file',
                                         description='Runs the default editor (as specified by the EDITOR variable) on the specified recipe. Note that this will be quicker for recipes in the workspace as the cache does not need to be loaded in that case.',
                                         group='working')
    parser_edit_recipe.add_argument('recipename', help='Recipe to edit')
    # FIXME drop -a at some point in future
    parser_edit_recipe.add_argument('--any-recipe', '-a', action="store_true", help='Does nothing (exists for backwards-compatibility)')
    parser_edit_recipe.set_defaults(func=edit_recipe)

    # Find-recipe
    parser_find_recipe = subparsers.add_parser('find-recipe', help='Find a recipe file',
                                         description='Finds a recipe file. Note that this will be quicker for recipes in the workspace as the cache does not need to be loaded in that case.',
                                         group='working')
    parser_find_recipe.add_argument('recipename', help='Recipe to find')
    # FIXME drop -a at some point in future
    parser_find_recipe.add_argument('--any-recipe', '-a', action="store_true", help='Does nothing (exists for backwards-compatibility)')
    parser_find_recipe.set_defaults(func=find_recipe)

    # NOTE: Needed to override the usage string here since the default
    # gets the order wrong - recipename must come before --arg
    parser_configure_help = subparsers.add_parser('configure-help', help='Get help on configure script options',
                                         usage='devtool configure-help [options] recipename [--arg ...]',
                                         description='Displays the help for the configure script for the specified recipe (i.e. runs ./configure --help) prefaced by a header describing the current options being specified. Output is piped through less (or whatever PAGER is set to, if set) for easy browsing.',
                                         group='working')
    parser_configure_help.add_argument('recipename', help='Recipe to show configure help for')
    parser_configure_help.add_argument('-p', '--no-pager', help='Disable paged output', action="store_true")
    parser_configure_help.add_argument('-n', '--no-header', help='Disable explanatory header text', action="store_true")
    parser_configure_help.add_argument('--arg', help='Pass remaining arguments to the configure script instead of --help (useful if the script has additional help options)', nargs=argparse.REMAINDER)
    parser_configure_help.set_defaults(func=configure_help)
