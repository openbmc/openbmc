# Development tool - package command plugin
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
"""Devtool plugin containing the package subcommands"""

import os
import subprocess
import logging
from bb.process import ExecutionError
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, DevtoolError

logger = logging.getLogger('devtool')

def package(args, config, basepath, workspace):
    """Entry point for the devtool 'package' subcommand"""
    check_workspace_recipe(workspace, args.recipename)

    tinfoil = setup_tinfoil(basepath=basepath)
    try:
        tinfoil.prepare(config_only=True)

        image_pkgtype = config.get('Package', 'image_pkgtype', '')
        if not image_pkgtype:
            image_pkgtype = tinfoil.config_data.getVar('IMAGE_PKGTYPE', True)

        deploy_dir_pkg = tinfoil.config_data.getVar('DEPLOY_DIR_%s' % image_pkgtype.upper(), True)
    finally:
        tinfoil.shutdown()

    package_task = config.get('Package', 'package_task', 'package_write_%s' % image_pkgtype)
    try:
        exec_build_env_command(config.init_path, basepath, 'bitbake -c %s %s' % (package_task, args.recipename), watch=True)
    except bb.process.ExecutionError as e:
        # We've already seen the output since watch=True, so just ensure we return something to the user
        return e.exitcode

    logger.info('Your packages are in %s' % deploy_dir_pkg)

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from the package plugin"""
    if context.fixed_setup:
        parser_package = subparsers.add_parser('package',
                                               help='Build packages for a recipe',
                                               description='Builds packages for a recipe\'s output files',
                                               group='testbuild', order=-5)
        parser_package.add_argument('recipename', help='Recipe to package')
        parser_package.set_defaults(func=package)
