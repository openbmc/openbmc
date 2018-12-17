# Recipe creation tool - edit plugin
#
# This sub-command edits the recipe and appends for the specified target
#
# Example: recipetool edit busybox
#
# Copyright (C) 2018 Mentor Graphics Corporation
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

import argparse
import errno
import logging
import os
import re
import subprocess
import sys
import scriptutils


logger = logging.getLogger('recipetool')
tinfoil = None


def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


def edit(args):
    import oe.recipeutils

    recipe_path = tinfoil.get_recipe_file(args.target)
    appends = tinfoil.get_file_appends(recipe_path)

    return scriptutils.run_editor([recipe_path] + appends, logger)


def register_commands(subparsers):
    parser = subparsers.add_parser('edit',
                                   help='Edit the recipe and appends for the specified target. This obeys $VISUAL if set, otherwise $EDITOR, otherwise vi.')
    parser.add_argument('target', help='Target recipe/provide to edit')
    parser.set_defaults(func=edit, parserecipes=True)
