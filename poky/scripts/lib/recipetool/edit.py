# Recipe creation tool - edit plugin
#
# This sub-command edits the recipe and appends for the specified target
#
# Example: recipetool edit busybox
#
# Copyright (C) 2018 Mentor Graphics Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

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

    return scriptutils.run_editor([recipe_path] + list(appends), logger)


def register_commands(subparsers):
    parser = subparsers.add_parser('edit',
                                   help='Edit the recipe and appends for the specified target. This obeys $VISUAL if set, otherwise $EDITOR, otherwise vi.')
    parser.add_argument('target', help='Target recipe/provide to edit')
    parser.set_defaults(func=edit, parserecipes=True)
