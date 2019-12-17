# Development tool - build command plugin
#
# Copyright (C) 2014-2015 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool build plugin"""

import os
import bb
import logging
import argparse
import tempfile
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, DevtoolError
from devtool import parse_recipe

logger = logging.getLogger('devtool')


def _set_file_values(fn, values):
    remaining = list(values.keys())

    def varfunc(varname, origvalue, op, newlines):
        newvalue = values.get(varname, origvalue)
        remaining.remove(varname)
        return (newvalue, '=', 0, True)

    with open(fn, 'r') as f:
        (updated, newlines) = bb.utils.edit_metadata(f, values, varfunc)

    for item in remaining:
        updated = True
        newlines.append('%s = "%s"' % (item, values[item]))

    if updated:
        with open(fn, 'w') as f:
            f.writelines(newlines)
    return updated

def _get_build_tasks(config):
    tasks = config.get('Build', 'build_task', 'populate_sysroot,packagedata').split(',')
    return ['do_%s' % task.strip() for task in tasks]

def build(args, config, basepath, workspace):
    """Entry point for the devtool 'build' subcommand"""
    workspacepn = check_workspace_recipe(workspace, args.recipename, bbclassextend=True)
    tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
    try:
        rd = parse_recipe(config, tinfoil, args.recipename, appends=True, filter_workspace=False)
        if not rd:
            return 1
        deploytask = 'do_deploy' in rd.getVar('__BBTASKS')
    finally:
        tinfoil.shutdown()

    if args.clean:
        # use clean instead of cleansstate to avoid messing things up in eSDK
        build_tasks = ['do_clean']
    else:
        build_tasks = _get_build_tasks(config)
        if deploytask:
            build_tasks.append('do_deploy')

    bbappend = workspace[workspacepn]['bbappend']
    if args.disable_parallel_make:
        logger.info("Disabling 'make' parallelism")
        _set_file_values(bbappend, {'PARALLEL_MAKE': ''})
    try:
        bbargs = []
        for task in build_tasks:
            if args.recipename.endswith('-native') and 'package' in task:
                continue
            bbargs.append('%s:%s' % (args.recipename, task))
        exec_build_env_command(config.init_path, basepath, 'bitbake %s' % ' '.join(bbargs), watch=True)
    except bb.process.ExecutionError as e:
        # We've already seen the output since watch=True, so just ensure we return something to the user
        return e.exitcode
    finally:
        if args.disable_parallel_make:
            _set_file_values(bbappend, {'PARALLEL_MAKE': None})

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_build = subparsers.add_parser('build', help='Build a recipe',
                                         description='Builds the specified recipe using bitbake (up to and including %s)' % ', '.join(_get_build_tasks(context.config)),
                                         group='working', order=50)
    parser_build.add_argument('recipename', help='Recipe to build')
    parser_build.add_argument('-s', '--disable-parallel-make', action="store_true", help='Disable make parallelism')
    parser_build.add_argument('-c', '--clean', action='store_true', help='clean up recipe building results')
    parser_build.set_defaults(func=build)
