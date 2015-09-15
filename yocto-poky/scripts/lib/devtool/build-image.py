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
from devtool import exec_build_env_command, setup_tinfoil, parse_recipe

logger = logging.getLogger('devtool')

def _get_recipes(workspace, config):
    """Get list of target recipes from the workspace."""
    result = []
    tinfoil = setup_tinfoil()
    for recipe in workspace:
        data = parse_recipe(config, tinfoil, recipe, True)
        if 'class-target' in data.getVar('OVERRIDES', True).split(':'):
            if recipe in data.getVar('PACKAGES', True):
                result.append(recipe)
            else:
                logger.warning("Skipping recipe %s as it doesn't produce "
                               "package with the same name", recipe)
    tinfoil.shutdown()
    return result

def build_image(args, config, basepath, workspace):
    """Entry point for the devtool 'build-image' subcommand."""
    image = args.recipe
    appendfile = os.path.join(config.workspace_path, 'appends',
                              '%s.bbappend' % image)

    # remove <image>.bbapend to make sure setup_tinfoil doesn't
    # breake because of it
    if os.path.isfile(appendfile):
        os.unlink(appendfile)

    recipes = _get_recipes(workspace, config)
    if recipes:
        with open(appendfile, 'w') as afile:
            # include selected recipes into the image
            afile.write('IMAGE_INSTALL_append = " %s"\n' % ' '.join(recipes))

            # Generate notification callback devtool_warn_image_extended
            afile.write('do_rootfs[prefuncs] += "devtool_warn_image_extended"\n\n')
            afile.write("python devtool_warn_image_extended() {\n")
            afile.write("    bb.plain('NOTE: %%s: building with additional '\n"
                        "             'packages due to \"devtool build-image\"'"
                        "              %% d.getVar('PN', True))\n"
                        "    bb.plain('NOTE: delete %%s to clear this' %% \\\n"
                        "             '%s')\n" % os.path.relpath(appendfile, basepath))
            afile.write("}\n")

            logger.info('Building image %s with the following '
                        'additional packages: %s', image, ' '.join(recipes))
    else:
        logger.warning('No recipes in workspace, building image %s unmodified', image)

    # run bitbake to build image
    try:
        exec_build_env_command(config.init_path, basepath,
                               'bitbake %s' % image, watch=True)
    except ExecutionError as err:
        return err.exitcode

    logger.info('Successfully built %s', image)

def register_commands(subparsers, context):
    """Register devtool subcommands from the build-image plugin"""
    parser = subparsers.add_parser('build-image',
                                   help='Build image including workspace recipe packages',
                                   description='Builds an image, extending it to include '
                                   'packages from recipes in the workspace')
    parser.add_argument('recipe', help='Image recipe to build')
    parser.set_defaults(func=build_image)
