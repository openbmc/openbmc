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
        pkgdata_dir = tinfoil.config_data.getVar('PKGDATA_DIR', True)
        defsummary = tinfoil.config_data.getVar('SUMMARY', False) or ''

        keyword_rc = re.compile(args.keyword)

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
                rd = parse_recipe(config, tinfoil, fn, True)
                summary = rd.getVar('SUMMARY', True)
                if summary == rd.expand(defsummary):
                    summary = ''
                print("%s  %s" % (fn.ljust(20), summary))
    finally:
        tinfoil.shutdown()

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    parser_search = subparsers.add_parser('search', help='Search available recipes',
                                            description='Searches for available target recipes. Matches on recipe name, package name, description and installed files, and prints the recipe name on match.',
                                            group='info')
    parser_search.add_argument('keyword', help='Keyword to search for (regular expression syntax allowed)')
    parser_search.set_defaults(func=search, no_workspace=True)
