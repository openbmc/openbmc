# Development tool - build-image plugin
#
# Copyright (C) 2015 Intel Corporation
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

"""Devtool plugin containing the build-image subcommand."""

import os
import logging

from bb.process import ExecutionError
from devtool import exec_build_env_command, setup_tinfoil, parse_recipe, DevtoolError

logger = logging.getLogger('devtool')

def _get_packages(tinfoil, workspace, config):
    """Get list of packages from recipes in the workspace."""
    result = []
    for recipe in workspace:
        data = parse_recipe(config, tinfoil, recipe, True)
        if 'class-target' in data.getVar('OVERRIDES', True).split(':'):
            if recipe in data.getVar('PACKAGES', True):
                result.append(recipe)
            else:
                logger.warning("Skipping recipe %s as it doesn't produce a "
                               "package with the same name", recipe)
    return result

def build_image(args, config, basepath, workspace):
    """Entry point for the devtool 'build-image' subcommand."""

    image = args.imagename
    auto_image = False
    if not image:
        sdk_targets = config.get('SDK', 'sdk_targets', '').split()
        if sdk_targets:
            image = sdk_targets[0]
            auto_image = True
    if not image:
        raise DevtoolError('Unable to determine image to build, please specify one')

    appendfile = os.path.join(config.workspace_path, 'appends',
                              '%s.bbappend' % image)

    # remove <image>.bbappend to make sure setup_tinfoil doesn't
    # break because of it
    if os.path.isfile(appendfile):
        os.unlink(appendfile)

    tinfoil = setup_tinfoil(basepath=basepath)
    rd = parse_recipe(config, tinfoil, image, True)
    if not rd:
        # Error already shown
        return 1
    if not bb.data.inherits_class('image', rd):
        if auto_image:
            raise DevtoolError('Unable to determine image to build, please specify one')
        else:
            raise DevtoolError('Specified recipe %s is not an image recipe' % image)

    try:
        if workspace:
            packages = _get_packages(tinfoil, workspace, config)
            if packages:
                with open(appendfile, 'w') as afile:
                    # include packages from workspace recipes into the image
                    afile.write('IMAGE_INSTALL_append = " %s"\n' % ' '.join(packages))
                    logger.info('Building image %s with the following '
                                'additional packages: %s', image, ' '.join(packages))
            else:
                logger.warning('No packages to add, building image %s unmodified', image)
        else:
            logger.warning('No recipes in workspace, building image %s unmodified', image)

        deploy_dir_image = tinfoil.config_data.getVar('DEPLOY_DIR_IMAGE', True)

        tinfoil.shutdown()

        # run bitbake to build image
        try:
            exec_build_env_command(config.init_path, basepath,
                                'bitbake %s' % image, watch=True)
        except ExecutionError as err:
            return err.exitcode
    finally:
        if os.path.isfile(appendfile):
            os.unlink(appendfile)

    logger.info('Successfully built %s. You can find output files in %s'
                % (image, deploy_dir_image))

def register_commands(subparsers, context):
    """Register devtool subcommands from the build-image plugin"""
    parser = subparsers.add_parser('build-image',
                                   help='Build image including workspace recipe packages',
                                   description='Builds an image, extending it to include '
                                   'packages from recipes in the workspace')
    parser.add_argument('imagename', help='Image recipe to build', nargs='?')
    parser.set_defaults(func=build_image)
