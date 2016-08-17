# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2013, Intel Corporation.
# All rights reserved.
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
#
# DESCRIPTION
# This module provides a place to collect various wic-related utils
# for the OpenEmbedded Image Tools.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#
"""Miscellaneous functions."""

import os
from collections import defaultdict

from wic import msger
from wic.utils import runner

# executable -> recipe pairs for exec_native_cmd
NATIVE_RECIPES = {"mcopy": "mtools",
                  "mkdosfs": "dosfstools",
                  "mkfs.btrfs": "btrfs-tools",
                  "mkfs.ext2": "e2fsprogs",
                  "mkfs.ext3": "e2fsprogs",
                  "mkfs.ext4": "e2fsprogs",
                  "mkfs.vfat": "dosfstools",
                  "mksquashfs": "squashfs-tools",
                  "mkswap": "util-linux",
                  "parted": "parted",
                  "sgdisk": "gptfdisk",
                  "syslinux": "syslinux"
                 }

def _exec_cmd(cmd_and_args, as_shell=False, catch=3):
    """
    Execute command, catching stderr, stdout

    Need to execute as_shell if the command uses wildcards
    """
    msger.debug("_exec_cmd: %s" % cmd_and_args)
    args = cmd_and_args.split()
    msger.debug(args)

    if as_shell:
        ret, out = runner.runtool(cmd_and_args, catch)
    else:
        ret, out = runner.runtool(args, catch)
    out = out.strip()
    msger.debug("_exec_cmd: output for %s (rc = %d): %s" % \
                (cmd_and_args, ret, out))

    return (ret, out)


def exec_cmd(cmd_and_args, as_shell=False, catch=3):
    """
    Execute command, catching stderr, stdout

    Exits if rc non-zero
    """
    ret, out = _exec_cmd(cmd_and_args, as_shell, catch)

    if ret != 0:
        msger.error("exec_cmd: %s returned '%s' instead of 0" % \
                    (cmd_and_args, ret))

    return out

def cmd_in_path(cmd, path):
    import scriptpath

    scriptpath.add_bitbake_lib_path()

    return bb.utils.which(path, cmd) != "" or False

def exec_native_cmd(cmd_and_args, native_sysroot, catch=3, pseudo=""):
    """
    Execute native command, catching stderr, stdout

    Need to execute as_shell if the command uses wildcards

    Always need to execute native commands as_shell
    """
    # The reason -1 is used is because there may be "export" commands.
    args = cmd_and_args.split(';')[-1].split()
    msger.debug(args)

    if pseudo:
        cmd_and_args = pseudo + cmd_and_args
    native_paths = \
        "%s/sbin:%s/usr/sbin:%s/usr/bin" % \
        (native_sysroot, native_sysroot, native_sysroot)
    native_cmd_and_args = "export PATH=%s:$PATH;%s" % \
                           (native_paths, cmd_and_args)
    msger.debug("exec_native_cmd: %s" % cmd_and_args)

    # If the command isn't in the native sysroot say we failed.
    if cmd_in_path(args[0], native_paths):
        ret, out = _exec_cmd(native_cmd_and_args, True, catch)
    else:
        ret = 127

    prog = args[0]
    # shell command-not-found
    if ret == 127 \
       or (pseudo and ret == 1 and out == "Can't find '%s' in $PATH." % prog):
        msg = "A native program %s required to build the image "\
              "was not found (see details above).\n\n" % prog
        recipe = NATIVE_RECIPES.get(prog)
        if recipe:
            msg += "Please bake it with 'bitbake %s-native' "\
                   "and try again.\n" % recipe
        else:
            msg += "Wic failed to find a recipe to build native %s. Please "\
                   "file a bug against wic.\n" % prog
        msger.error(msg)
    if out:
        msger.debug('"%s" output: %s' % (args[0], out))

    if ret != 0:
        msger.error("exec_cmd: '%s' returned '%s' instead of 0" % \
                    (cmd_and_args, ret))

    return ret, out

BOOTDD_EXTRA_SPACE = 16384

class BitbakeVars(defaultdict):
    """
    Container for Bitbake variables.
    """
    def __init__(self):
        defaultdict.__init__(self, dict)

        # default_image and vars_dir attributes should be set from outside
        self.default_image = None
        self.vars_dir = None

    def _parse_line(self, line, image):
        """
        Parse one line from bitbake -e output or from .env file.
        Put result key-value pair into the storage.
        """
        if "=" not in line:
            return
        try:
            key, val = line.split("=")
        except ValueError:
            return
        key = key.strip()
        val = val.strip()
        if key.replace('_', '').isalnum():
            self[image][key] = val.strip('"')

    def get_var(self, var, image=None):
        """
        Get bitbake variable from 'bitbake -e' output or from .env file.
        This is a lazy method, i.e. it runs bitbake or parses file only when
        only when variable is requested. It also caches results.
        """
        if not image:
            image = self.default_image

        if image not in self:
            if image and self.vars_dir:
                fname = os.path.join(self.vars_dir, image + '.env')
                if os.path.isfile(fname):
                    # parse .env file
                    with open(fname) as varsfile:
                        for line in varsfile:
                            self._parse_line(line, image)
                else:
                    print "Couldn't get bitbake variable from %s." % fname
                    print "File %s doesn't exist." % fname
                    return
            else:
                # Get bitbake -e output
                cmd = "bitbake -e"
                if image:
                    cmd += " %s" % image

                log_level = msger.get_loglevel()
                msger.set_loglevel('normal')
                ret, lines = _exec_cmd(cmd)
                msger.set_loglevel(log_level)

                if ret:
                    print "Couldn't get '%s' output." % cmd
                    print "Bitbake failed with error:\n%s\n" % lines
                    return

                # Parse bitbake -e output
                for line in lines.split('\n'):
                    self._parse_line(line, image)

            # Make first image a default set of variables
            images = [key for key in self if key]
            if len(images) == 1:
                self[None] = self[image]

        return self[image].get(var)

# Create BB_VARS singleton
BB_VARS = BitbakeVars()

def get_bitbake_var(var, image=None):
    """
    Provide old get_bitbake_var API by wrapping
    get_var method of BB_VARS singleton.
    """
    return BB_VARS.get_var(var, image)

def parse_sourceparams(sourceparams):
    """
    Split sourceparams string of the form key1=val1[,key2=val2,...]
    into a dict.  Also accepts valueless keys i.e. without =.

    Returns dict of param key/val pairs (note that val may be None).
    """
    params_dict = {}

    params = sourceparams.split(',')
    if params:
        for par in params:
            if not par:
                continue
            if not '=' in par:
                key = par
                val = None
            else:
                key, val = par.split('=')
            params_dict[key] = val

    return params_dict
