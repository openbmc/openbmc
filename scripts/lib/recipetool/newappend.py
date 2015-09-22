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
import sys


logger = logging.getLogger('recipetool')
tinfoil = None


def plugin_init(pluginlist):
    # Don't need to do anything here right now, but plugins must have this function defined
    pass


def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


def _provide_to_pn(cooker, provide):
    """Get the name of the preferred recipe for the specified provide."""
    import bb.providers
    filenames = cooker.recipecache.providers[provide]
    eligible, foundUnique = bb.providers.filterProviders(filenames, provide, cooker.expanded_data, cooker.recipecache)
    filename = eligible[0]
    pn = cooker.recipecache.pkg_fn[filename]
    return pn


def _get_recipe_file(cooker, pn):
    import oe.recipeutils
    recipefile = oe.recipeutils.pn_to_recipe(cooker, pn)
    if not recipefile:
        skipreasons = oe.recipeutils.get_unavailable_reasons(cooker, pn)
        if skipreasons:
            logger.error('\n'.join(skipreasons))
        else:
            logger.error("Unable to find any recipe file matching %s" % pn)
    return recipefile


def layer(layerpath):
    if not os.path.exists(os.path.join(layerpath, 'conf', 'layer.conf')):
        raise argparse.ArgumentTypeError('{0!r} must be a path to a valid layer'.format(layerpath))
    return layerpath


def newappend(args):
    import oe.recipeutils

    pn = _provide_to_pn(tinfoil.cooker, args.target)
    recipe_path = _get_recipe_file(tinfoil.cooker, pn)

    rd = tinfoil.config_data.createCopy()
    rd.setVar('FILE', recipe_path)
    append_path, path_ok = oe.recipeutils.get_bbappend_path(rd, args.destlayer, args.wildcard_version)
    if not append_path:
        logger.error('Unable to determine layer directory containing %s', recipe_path)
        return 1

    if not path_ok:
        logger.warn('Unable to determine correct subdirectory path for bbappend file - check that what %s adds to BBFILES also matches .bbappend files. Using %s for now, but until you fix this the bbappend will not be applied.', os.path.join(destlayerdir, 'conf', 'layer.conf'), os.path.dirname(appendpath))

    layerdirs = [os.path.abspath(layerdir) for layerdir in rd.getVar('BBLAYERS', True).split()]
    if not os.path.abspath(args.destlayer) in layerdirs:
        logger.warn('Specified layer is not currently enabled in bblayers.conf, you will need to add it before this bbappend will be active')

    if not os.path.exists(append_path):
        bb.utils.mkdirhier(os.path.dirname(append_path))

        try:
            open(append_path, 'a')
        except (OSError, IOError) as exc:
            logger.critical(str(exc))
            return 1

    print(append_path)


def register_command(subparsers):
    parser = subparsers.add_parser('newappend',
                                   help='Create a bbappend for the specified target in the specified layer')
    parser.add_argument('-w', '--wildcard-version', help='Use wildcard to make the bbappend apply to any recipe version', action='store_true')
    parser.add_argument('destlayer', help='Base directory of the destination layer to write the bbappend to', type=layer)
    parser.add_argument('target', help='Target recipe/provide to append')
    parser.set_defaults(func=newappend, parserecipes=True)
