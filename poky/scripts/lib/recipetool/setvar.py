# Recipe creation tool - set variable plugin
#
# Copyright (C) 2015 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import os
import argparse
import glob
import fnmatch
import re
import logging
import scriptutils

logger = logging.getLogger('recipetool')

tinfoil = None
plugins = None

def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance

def setvar(args):
    import oe.recipeutils

    if args.delete:
        if args.value:
            logger.error('-D/--delete and specifying a value are mutually exclusive')
            return 1
        value = None
    else:
        if args.value is None:
            logger.error('You must specify a value if not using -D/--delete')
            return 1
        value = args.value
    varvalues = {args.varname: value}

    if args.recipe_only:
        patches = [oe.recipeutils.patch_recipe_file(args.recipefile, varvalues, patch=args.patch)]
    else:
        rd = tinfoil.parse_recipe_file(args.recipefile, False)
        if not rd:
            return 1
        patches = oe.recipeutils.patch_recipe(rd, args.recipefile, varvalues, patch=args.patch)
    if args.patch:
        for patch in patches:
            for line in patch:
                sys.stdout.write(line)
    return 0


def register_commands(subparsers):
    parser_setvar = subparsers.add_parser('setvar',
                                          help='Set a variable within a recipe',
                                          description='Adds/updates the value a variable is set to in a recipe')
    parser_setvar.add_argument('recipefile', help='Recipe file to update')
    parser_setvar.add_argument('varname', help='Variable name to set')
    parser_setvar.add_argument('value', nargs='?', help='New value to set the variable to')
    parser_setvar.add_argument('--recipe-only', '-r', help='Do not set variable in any include file if present', action='store_true')
    parser_setvar.add_argument('--patch', '-p', help='Create a patch to make the change instead of modifying the recipe', action='store_true')
    parser_setvar.add_argument('--delete', '-D', help='Delete the specified value instead of setting it', action='store_true')
    parser_setvar.set_defaults(func=setvar)
