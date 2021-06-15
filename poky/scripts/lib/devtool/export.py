# Development tool - export command plugin
#
# Copyright (C) 2014-2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool export plugin"""

import os
import argparse
import tarfile
import logging
import datetime
import json

logger = logging.getLogger('devtool')

# output files
default_arcname_prefix = "workspace-export"
metadata = '.export_metadata'

def export(args, config, basepath, workspace):
    """Entry point for the devtool 'export' subcommand"""

    def add_metadata(tar):
        """Archive the workspace object"""
        # finally store the workspace metadata
        with open(metadata, 'w') as fd:
            fd.write(json.dumps((config.workspace_path, workspace)))
        tar.add(metadata)
        os.unlink(metadata)

    def add_recipe(tar, recipe, data):
        """Archive recipe with proper arcname"""
        # Create a map of name/arcnames
        arcnames = []
        for key, name in data.items():
            if name:
                if key == 'srctree':
                    # all sources, no matter where are located, goes into the sources directory
                    arcname = 'sources/%s' % recipe
                else:
                    arcname = name.replace(config.workspace_path, '')
                arcnames.append((name, arcname))

        for name, arcname in arcnames:
            tar.add(name, arcname=arcname)


    # Make sure workspace is non-empty and possible listed include/excluded recipes are in workspace
    if not workspace:
        logger.info('Workspace contains no recipes, nothing to export')
        return 0
    else:
        for param, recipes in {'include':args.include,'exclude':args.exclude}.items():
            for recipe in recipes:
                if recipe not in workspace:
                    logger.error('Recipe (%s) on %s argument not in the current workspace' % (recipe, param))
                    return 1

    name = args.file

    default_name = "%s-%s.tar.gz" % (default_arcname_prefix, datetime.datetime.now().strftime('%Y%m%d%H%M%S'))
    if not name:
        name = default_name
    else:
        # if name is a directory, append the default name
        if os.path.isdir(name):
            name = os.path.join(name, default_name)

    if os.path.exists(name) and not args.overwrite:
        logger.error('Tar archive %s exists. Use --overwrite/-o to overwrite it')
        return 1

    # if all workspace is excluded, quit
    if not len(set(workspace.keys()).difference(set(args.exclude))):
        logger.warning('All recipes in workspace excluded, nothing to export')
        return 0

    exported = []
    with tarfile.open(name, 'w:gz') as tar:
        if args.include:
            for recipe in args.include:
                add_recipe(tar, recipe, workspace[recipe])
                exported.append(recipe)
        else:
            for recipe, data in workspace.items():
                if recipe not in args.exclude:
                    add_recipe(tar, recipe, data)
                    exported.append(recipe)

        add_metadata(tar)

    logger.info('Tar archive created at %s with the following recipes: %s' % (name, ', '.join(exported)))
    return 0

def register_commands(subparsers, context):
    """Register devtool export subcommands"""
    parser = subparsers.add_parser('export',
                                   help='Export workspace into a tar archive',
                                   description='Export one or more recipes from current workspace into a tar archive',
                                   group='advanced')

    parser.add_argument('--file', '-f', help='Output archive file name')
    parser.add_argument('--overwrite', '-o', action="store_true", help='Overwrite previous export tar archive')
    group = parser.add_mutually_exclusive_group()
    group.add_argument('--include', '-i', nargs='+', default=[], help='Include recipes into the tar archive')
    group.add_argument('--exclude', '-e', nargs='+', default=[], help='Exclude recipes into the tar archive')
    parser.set_defaults(func=export)
