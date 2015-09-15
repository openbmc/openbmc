#!/usr/bin/env python

# Development tool - utility functions for plugins
#
# Copyright (C) 2014 Intel Corporation
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
"""Devtool plugins module"""

import os
import sys
import subprocess
import logging

logger = logging.getLogger('devtool')


class DevtoolError(Exception):
    """Exception for handling devtool errors"""
    pass


def exec_build_env_command(init_path, builddir, cmd, watch=False, **options):
    """Run a program in bitbake build context"""
    import bb
    if not 'cwd' in options:
        options["cwd"] = builddir
    if init_path:
        # As the OE init script makes use of BASH_SOURCE to determine OEROOT,
        # and can't determine it when running under dash, we need to set
        # the executable to bash to correctly set things up
        if not 'executable' in options:
            options['executable'] = 'bash'
        logger.debug('Executing command: "%s" using init path %s' % (cmd, init_path))
        init_prefix = '. %s %s > /dev/null && ' % (init_path, builddir)
    else:
        logger.debug('Executing command "%s"' % cmd)
        init_prefix = ''
    if watch:
        if sys.stdout.isatty():
            # Fool bitbake into thinking it's outputting to a terminal (because it is, indirectly)
            cmd = 'script -e -q -c "%s" /dev/null' % cmd
        return exec_watch('%s%s' % (init_prefix, cmd), **options)
    else:
        return bb.process.run('%s%s' % (init_prefix, cmd), **options)

def exec_watch(cmd, **options):
    """Run program with stdout shown on sys.stdout"""
    import bb
    if isinstance(cmd, basestring) and not "shell" in options:
        options["shell"] = True

    process = subprocess.Popen(
        cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, **options
    )

    buf = ''
    while True:
        out = process.stdout.read(1)
        if out:
            sys.stdout.write(out)
            sys.stdout.flush()
            buf += out
        elif out == '' and process.poll() != None:
            break

    if process.returncode != 0:
        raise bb.process.ExecutionError(cmd, process.returncode, buf, None)

    return buf, None

def exec_fakeroot(d, cmd, **kwargs):
    """Run a command under fakeroot (pseudo, in fact) so that it picks up the appropriate file permissions"""
    # Grab the command and check it actually exists
    fakerootcmd = d.getVar('FAKEROOTCMD', True)
    if not os.path.exists(fakerootcmd):
        logger.error('pseudo executable %s could not be found - have you run a build yet? pseudo-native should install this and if you have run any build then that should have been built')
        return 2
    # Set up the appropriate environment
    newenv = dict(os.environ)
    fakerootenv = d.getVar('FAKEROOTENV', True)
    for varvalue in fakerootenv.split():
        if '=' in varvalue:
            splitval = varvalue.split('=', 1)
            newenv[splitval[0]] = splitval[1]
    return subprocess.call("%s %s" % (fakerootcmd, cmd), env=newenv, **kwargs)

def setup_tinfoil(config_only=False):
    """Initialize tinfoil api from bitbake"""
    import scriptpath
    bitbakepath = scriptpath.add_bitbake_lib_path()
    if not bitbakepath:
        logger.error("Unable to find bitbake by searching parent directory of this script or PATH")
        sys.exit(1)

    import bb.tinfoil
    tinfoil = bb.tinfoil.Tinfoil()
    tinfoil.prepare(config_only)
    tinfoil.logger.setLevel(logger.getEffectiveLevel())
    return tinfoil

def get_recipe_file(cooker, pn):
    """Find recipe file corresponding a package name"""
    import oe.recipeutils
    recipefile = oe.recipeutils.pn_to_recipe(cooker, pn)
    if not recipefile:
        skipreasons = oe.recipeutils.get_unavailable_reasons(cooker, pn)
        if skipreasons:
            logger.error('\n'.join(skipreasons))
        else:
            logger.error("Unable to find any recipe file matching %s" % pn)
    return recipefile

def parse_recipe(config, tinfoil, pn, appends):
    """Parse recipe of a package"""
    import oe.recipeutils
    recipefile = get_recipe_file(tinfoil.cooker, pn)
    if not recipefile:
        # Error already logged
        return None
    if appends:
        append_files = tinfoil.cooker.collection.get_file_appends(recipefile)
        # Filter out appends from the workspace
        append_files = [path for path in append_files if
                        not path.startswith(config.workspace_path)]
    return oe.recipeutils.parse_recipe(recipefile, append_files,
                                       tinfoil.config_data)
