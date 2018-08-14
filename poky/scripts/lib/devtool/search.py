# Development tool - search command plugin
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

"""Devtool search plugin"""

import os
import bb
import logging
import argparse
import re
from devtool import setup_tinfoil, parse_recipe, DevtoolError

logger = logging.getLogger('devtool')

def search(args, config, basepath, workspace):
    """Entry point for the devtool 'search' subcommand"""

    tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
    try:
        pkgdata_dir = tinfoil.config_data.getVar('PKGDATA_DIR')
        defsummary = tinfoil.config_data.getVar('SUMMARY', False) or ''

        keyword_rc = re.compile(args.keyword)

        def print_match(pn):
            rd = parse_recipe(config, tinfoil, pn, True)
            if not rd:
                return
            summary = rd.getVar('SUMMARY')
            if summary == rd.expand(defsummary):
                summary = ''
            print("%s  %s" % (pn.ljust(20), summary))


        matches = []
        if os.path.exists(pkgdata_dir):
            for fn in os.listdir(pkgdata_dir):
                pfn = os.path.join(pkgdata_dir, fn)
                if not os.path.isfile(pfn):
                    continue

                packages = []
                match = False
                if keyword_rc.search(fn):
                    match = True

                if not match:
                    with open(pfn, 'r') as f:
                        for line in f:
                            if line.startswith('PACKAGES:'):
                                packages = line.split(':', 1)[1].strip().split()

                    for pkg in packages:
                        if keyword_rc.search(pkg):
                            match = True
                            break
                        if os.path.exists(os.path.join(pkgdata_dir, 'runtime', pkg + '.packaged')):
                            with open(os.path.join(pkgdata_dir, 'runtime', pkg), 'r') as f:
                                for line in f:
                                    if ': ' in line:
                                        splitline = line.split(':', 1)
                                        key = splitline[0]
                                        value = splitline[1].strip()
                                    if key in ['PKG_%s' % pkg, 'DESCRIPTION', 'FILES_INFO'] or key.startswith('FILERPROVIDES_'):
                                        if keyword_rc.search(value):
                                            match = True
                                            break
                if match:
                    print_match(fn)
                    matches.append(fn)
        else:
            logger.warning('Package data is not available, results may be limited')

        for recipe in tinfoil.all_recipes():
            if args.fixed_setup and 'nativesdk' in recipe.inherits():
                continue

            match = False
            if keyword_rc.search(recipe.pn):
                match = True
            else:
                for prov in recipe.provides:
                    if keyword_rc.search(prov):
                        match = True
                        break
                if not match:
                    for rprov in recipe.rprovides:
                        if keyword_rc.search(rprov):
                            match = True
                            break
            if match and not recipe.pn in matches:
                print_match(recipe.pn)
    finally:
        tinfoil.shutdown()

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_search = subparsers.add_parser('search', help='Search available recipes',
                                            description='Searches for available recipes. Matches on recipe name, package name, description and installed files, and prints the recipe name and summary on match.',
                                            group='info')
    parser_search.add_argument('keyword', help='Keyword to search for (regular expression syntax allowed, use quotes to avoid shell expansion)')
    parser_search.set_defaults(func=search, no_workspace=True, fixed_setup=context.fixed_setup)
