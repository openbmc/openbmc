# Development tool - build-sdk command plugin
#
# Copyright (C) 2015-2016 Intel Corporation
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

import os
import subprocess
import logging
import glob
import shutil
import errno
import sys
import tempfile
from devtool import exec_build_env_command, setup_tinfoil, parse_recipe, DevtoolError
from devtool import build_image

logger = logging.getLogger('devtool')


def build_sdk(args, config, basepath, workspace):
    """Entry point for the devtool build-sdk command"""

    sdk_targets = config.get('SDK', 'sdk_targets', '').split()
    if sdk_targets:
        image = sdk_targets[0]
    else:
        raise DevtoolError('Unable to determine image to build SDK for')

    extra_append = ['SDK_DERIVATIVE = "1"']
    try:
        result, outputdir = build_image.build_image_task(config,
                                                         basepath,
                                                         workspace,
                                                         image,
                                                         task='populate_sdk_ext',
                                                         extra_append=extra_append)
    except build_image.TargetNotImageError:
        raise DevtoolError('Unable to determine image to build SDK for')

    if result == 0:
        logger.info('Successfully built SDK. You can find output files in %s'
                    % outputdir)
    return result


def register_commands(subparsers, context):
    """Register devtool subcommands"""
    if context.fixed_setup:
        parser_build_sdk = subparsers.add_parser('build-sdk',
                                                 help='Build a derivative SDK of this one',
                                                 description='Builds an extensible SDK based upon this one and the items in your workspace',
                                                 group='advanced')
        parser_build_sdk.set_defaults(func=build_sdk)
