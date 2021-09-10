# Development tool - build-image plugin
#
# Copyright (C) 2015 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""Devtool plugin containing the build-image subcommand."""

import os
import errno
import logging

from bb.process import ExecutionError
from devtool import exec_build_env_command, setup_tinfoil, parse_recipe, DevtoolError

logger = logging.getLogger('devtool')

class TargetNotImageError(Exception):
    pass

def _get_packages(tinfoil, workspace, config):
    """Get list of packages from recipes in the workspace."""
    result = []
    for recipe in workspace:
        data = parse_recipe(config, tinfoil, recipe, True)
        if 'class-target' in data.getVar('OVERRIDES').split(':'):
            if recipe in data.getVar('PACKAGES').split():
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

    try:
        if args.add_packages:
            add_packages = args.add_packages.split(',')
        else:
            add_packages = None
        result, outputdir = build_image_task(config, basepath, workspace, image, add_packages)
    except TargetNotImageError:
        if auto_image:
            raise DevtoolError('Unable to determine image to build, please specify one')
        else:
            raise DevtoolError('Specified recipe %s is not an image recipe' % image)

    if result == 0:
        logger.info('Successfully built %s. You can find output files in %s'
                    % (image, outputdir))
    return result

def build_image_task(config, basepath, workspace, image, add_packages=None, task=None, extra_append=None):
    # remove <image>.bbappend to make sure setup_tinfoil doesn't
    # break because of it
    target_basename = config.get('SDK', 'target_basename', '')
    if target_basename:
        appendfile = os.path.join(config.workspace_path, 'appends',
                                  '%s.bbappend' % target_basename)
        try:
            os.unlink(appendfile)
        except OSError as exc:
            if exc.errno != errno.ENOENT:
                raise

    tinfoil = setup_tinfoil(basepath=basepath)
    try:
        rd = parse_recipe(config, tinfoil, image, True)
        if not rd:
            # Error already shown
            return (1, None)
        if not bb.data.inherits_class('image', rd):
            raise TargetNotImageError()

        # Get the actual filename used and strip the .bb and full path
        target_basename = rd.getVar('FILE')
        target_basename = os.path.splitext(os.path.basename(target_basename))[0]
        config.set('SDK', 'target_basename', target_basename)
        config.write()

        appendfile = os.path.join(config.workspace_path, 'appends',
                                '%s.bbappend' % target_basename)

        outputdir = None
        try:
            if workspace or add_packages:
                if add_packages:
                    packages = add_packages
                else:
                    packages = _get_packages(tinfoil, workspace, config)
            else:
                packages = None
            if not task:
                if not packages and not add_packages and workspace:
                    logger.warning('No recipes in workspace, building image %s unmodified', image)
                elif not packages:
                    logger.warning('No packages to add, building image %s unmodified', image)

            if packages or extra_append:
                bb.utils.mkdirhier(os.path.dirname(appendfile))
                with open(appendfile, 'w') as afile:
                    if packages:
                        # include packages from workspace recipes into the image
                        afile.write('IMAGE_INSTALL:append = " %s"\n' % ' '.join(packages))
                        if not task:
                            logger.info('Building image %s with the following '
                                        'additional packages: %s', image, ' '.join(packages))
                    if extra_append:
                        for line in extra_append:
                            afile.write('%s\n' % line)

            if task in ['populate_sdk', 'populate_sdk_ext']:
                outputdir = rd.getVar('SDK_DEPLOY')
            else:
                outputdir = rd.getVar('DEPLOY_DIR_IMAGE')

            tmp_tinfoil = tinfoil
            tinfoil = None
            tmp_tinfoil.shutdown()

            options = ''
            if task:
                options += '-c %s' % task

            # run bitbake to build image (or specified task)
            try:
                exec_build_env_command(config.init_path, basepath,
                                    'bitbake %s %s' % (options, image), watch=True)
            except ExecutionError as err:
                return (err.exitcode, None)
        finally:
            if os.path.isfile(appendfile):
                os.unlink(appendfile)
    finally:
        if tinfoil:
            tinfoil.shutdown()
    return (0, outputdir)


def register_commands(subparsers, context):
    """Register devtool subcommands from the build-image plugin"""
    parser = subparsers.add_parser('build-image',
                                   help='Build image including workspace recipe packages',
                                   description='Builds an image, extending it to include '
                                   'packages from recipes in the workspace',
                                   group='testbuild', order=-10)
    parser.add_argument('imagename', help='Image recipe to build', nargs='?')
    parser.add_argument('-p', '--add-packages', help='Instead of adding packages for the '
                        'entire workspace, specify packages to be added to the image '
                        '(separate multiple packages by commas)',
                        metavar='PACKAGES')
    parser.set_defaults(func=build_image)
