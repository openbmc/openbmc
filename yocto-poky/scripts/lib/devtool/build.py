# Development tool - build command plugin
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
"""Devtool build plugin"""

import os
import bb
import logging
import argparse
import tempfile
from devtool import exec_build_env_command, check_workspace_recipe, DevtoolError

logger = logging.getLogger('devtool')

def plugin_init(pluginlist):
    """Plugin initialization"""
    pass

def _create_conf_file(values, conf_file=None):
    if not conf_file:
        fd, conf_file = tempfile.mkstemp(suffix='.conf')
    elif not os.path.exists(os.path.dirname(conf_file)):
        logger.debug("Creating folder %s" % os.path.dirname(conf_file))
        bb.utils.mkdirhier(os.path.dirname(conf_file))
    with open(conf_file, 'w') as f:
        for key, value in values.iteritems():
            f.write('%s = "%s"\n' % (key, value))
    return conf_file

def build(args, config, basepath, workspace):
    """Entry point for the devtool 'build' subcommand"""
    check_workspace_recipe(workspace, args.recipename)

    build_task = config.get('Build', 'build_task', 'populate_sysroot')

    postfile_param = ""
    postfile = ""
    if args.disable_parallel_make:
        logger.info("Disabling 'make' parallelism")
        postfile = os.path.join(basepath, 'conf', 'disable_parallelism.conf')
        _create_conf_file({'PARALLEL_MAKE':''}, postfile)
        postfile_param = "-R %s" % postfile
    try:
        exec_build_env_command(config.init_path, basepath, 'bitbake -c %s %s %s' % (build_task, postfile_param, args.recipename), watch=True)
    except bb.process.ExecutionError as e:
        # We've already seen the output since watch=True, so just ensure we return something to the user
        return e.exitcode
    finally:
        if postfile:
            logger.debug('Removing postfile')
            os.remove(postfile)

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_build = subparsers.add_parser('build', help='Build a recipe',
                                         description='Builds the specified recipe using bitbake',
                                         formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_build.add_argument('recipename', help='Recipe to build')
    parser_build.add_argument('-s', '--disable-parallel-make', action="store_true", help='Disable make parallelism')
    parser_build.set_defaults(func=build)
