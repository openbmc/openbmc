# Development tool - import command plugin
#
# Copyright (C) 2014-2017 Intel Corporation
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
"""Devtool import plugin"""

import os
import tarfile
import logging
import collections
import json
import fnmatch

from devtool import standard, setup_tinfoil, replace_from_file, DevtoolError
from devtool import export

logger = logging.getLogger('devtool')

def devimport(args, config, basepath, workspace):
    """Entry point for the devtool 'import' subcommand"""

    def get_pn(name):
        """ Returns the filename of a workspace recipe/append"""
        metadata = name.split('/')[-1]
        fn, _ = os.path.splitext(metadata)
        return fn

    if not os.path.exists(args.file):
        raise DevtoolError('Tar archive %s does not exist. Export your workspace using "devtool export"' % args.file)

    with tarfile.open(args.file) as tar:
        # Get exported metadata
        export_workspace_path = export_workspace = None
        try:
            metadata = tar.getmember(export.metadata)
        except KeyError as ke:
            raise DevtoolError('The export metadata file created by "devtool export" was not found. "devtool import" can only be used to import tar archives created by "devtool export".')

        tar.extract(metadata)
        with open(metadata.name) as fdm:
            export_workspace_path, export_workspace = json.load(fdm)
        os.unlink(metadata.name)

        members = tar.getmembers()

        # Get appends and recipes from the exported archive, these
        # will be needed to find out those appends without corresponding
        # recipe pair
        append_fns, recipe_fns = set(), set()
        for member in members:
            if member.name.startswith('appends'):
                append_fns.add(get_pn(member.name))
            elif member.name.startswith('recipes'):
                recipe_fns.add(get_pn(member.name))

        # Setup tinfoil, get required data and shutdown
        tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
        try:
            current_fns = [os.path.basename(recipe[0]) for recipe in tinfoil.cooker.recipecaches[''].pkg_fn.items()]
        finally:
            tinfoil.shutdown()

        # Find those appends that do not have recipes in current metadata
        non_importables = []
        for fn in append_fns - recipe_fns:
            # Check on current metadata (covering those layers indicated in bblayers.conf)
            for current_fn in current_fns:
                if fnmatch.fnmatch(current_fn, '*' + fn.replace('%', '') + '*'):
                    break
            else:
                non_importables.append(fn)
                logger.warning('No recipe to append %s.bbapppend, skipping' % fn)

        # Extract
        imported = []
        for member in members:
            if member.name == export.metadata:
                continue

            for nonimp in non_importables:
                pn = nonimp.split('_')[0]
                # do not extract data from non-importable recipes or metadata
                if member.name.startswith('appends/%s' % nonimp) or \
                        member.name.startswith('recipes/%s' % nonimp) or \
                        member.name.startswith('sources/%s' % pn):
                    break
            else:
                path = os.path.join(config.workspace_path, member.name)
                if os.path.exists(path):
                    # by default, no file overwrite is done unless -o is given by the user
                    if args.overwrite:
                        try:
                            tar.extract(member, path=config.workspace_path)
                        except PermissionError as pe:
                            logger.warning(pe)
                    else:
                        logger.warning('File already present. Use --overwrite/-o to overwrite it: %s' % member.name)
                        continue
                else:
                    tar.extract(member, path=config.workspace_path)

                # Update EXTERNALSRC and the devtool md5 file
                if member.name.startswith('appends'):
                    if export_workspace_path:
                        # appends created by 'devtool modify' just need to update the workspace
                        replace_from_file(path, export_workspace_path, config.workspace_path)

                        # appends created by 'devtool add' need replacement of exported source tree
                        pn = get_pn(member.name).split('_')[0]
                        exported_srctree = export_workspace[pn]['srctree']
                        if exported_srctree:
                            replace_from_file(path, exported_srctree, os.path.join(config.workspace_path, 'sources', pn))

                    standard._add_md5(config, pn, path)
                    imported.append(pn)

    if imported:
        logger.info('Imported recipes into workspace %s: %s' % (config.workspace_path, ', '.join(imported)))
    else:
        logger.warning('No recipes imported into the workspace')

    return 0

def register_commands(subparsers, context):
    """Register devtool import subcommands"""
    parser = subparsers.add_parser('import',
                                   help='Import exported tar archive into workspace',
                                   description='Import tar archive previously created by "devtool export" into workspace',
                                   group='advanced')
    parser.add_argument('file', metavar='FILE', help='Name of the tar archive to import')
    parser.add_argument('--overwrite', '-o', action="store_true", help='Overwrite files when extracting')
    parser.set_defaults(func=devimport)
