# Recipe creation tool - newappend plugin
#
# This sub-command creates a bbappend for the specified target and prints the
# path to the bbappend.
#
# Example: recipetool newappend meta-mylayer busybox
#
# Copyright (C) 2015 Christopher Larson <kergoth@gmail.com>
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


def layer(layerpath):
    if not os.path.exists(os.path.join(layerpath, 'conf', 'layer.conf')):
        raise argparse.ArgumentTypeError('{0!r} must be a path to a valid layer'.format(layerpath))
    return layerpath


def newappend(args):
    import oe.recipeutils

    recipe_path = tinfoil.get_recipe_file(args.target)

    rd = tinfoil.config_data.createCopy()
    rd.setVar('FILE', recipe_path)
    append_path, path_ok = oe.recipeutils.get_bbappend_path(rd, args.destlayer, args.wildcard_version)
    if not append_path:
        logger.error('Unable to determine layer directory containing %s', recipe_path)
        return 1

    if not path_ok:
        logger.warn('Unable to determine correct subdirectory path for bbappend file - check that what %s adds to BBFILES also matches .bbappend files. Using %s for now, but until you fix this the bbappend will not be applied.', os.path.join(args.destlayer, 'conf', 'layer.conf'), os.path.dirname(append_path))

    layerdirs = [os.path.abspath(layerdir) for layerdir in rd.getVar('BBLAYERS').split()]
    if not os.path.abspath(args.destlayer) in layerdirs:
        logger.warn('Specified layer is not currently enabled in bblayers.conf, you will need to add it before this bbappend will be active')

    if not os.path.exists(append_path):
        bb.utils.mkdirhier(os.path.dirname(append_path))

        try:
            open(append_path, 'a').close()
        except (OSError, IOError) as exc:
            logger.critical(str(exc))
            return 1

    if args.edit:
        return scriptutils.run_editor([append_path, recipe_path], logger)
    else:
        print(append_path)


def register_commands(subparsers):
    parser = subparsers.add_parser('newappend',
                                   help='Create a bbappend for the specified target in the specified layer')
    parser.add_argument('-e', '--edit', help='Edit the new append. This obeys $VISUAL if set, otherwise $EDITOR, otherwise vi.', action='store_true')
    parser.add_argument('-w', '--wildcard-version', help='Use wildcard to make the bbappend apply to any recipe version', action='store_true')
    parser.add_argument('destlayer', help='Base directory of the destination layer to write the bbappend to', type=layer)
    parser.add_argument('target', help='Target recipe/provide to append')
    parser.set_defaults(func=newappend, parserecipes=True)
