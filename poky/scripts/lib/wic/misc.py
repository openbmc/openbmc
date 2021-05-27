#
# Copyright (c) 2013, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This module provides a place to collect various wic-related utils
# for the OpenEmbedded Image Tools.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#
"""Miscellaneous functions."""

import logging
import os
import re
import subprocess

from collections import defaultdict
from distutils import spawn

from wic import WicError

logger = logging.getLogger('wic')

# executable -> recipe pairs for exec_native_cmd
NATIVE_RECIPES = {"bmaptool": "bmap-tools",
                  "dumpe2fs": "e2fsprogs",
                  "grub-mkimage": "grub-efi",
                  "isohybrid": "syslinux",
                  "mcopy": "mtools",
                  "mdel" : "mtools",
                  "mdeltree" : "mtools",
                  "mdir" : "mtools",
                  "mkdosfs": "dosfstools",
                  "mkisofs": "cdrtools",
                  "mkfs.btrfs": "btrfs-tools",
                  "mkfs.ext2": "e2fsprogs",
                  "mkfs.ext3": "e2fsprogs",
                  "mkfs.ext4": "e2fsprogs",
                  "mkfs.vfat": "dosfstools",
                  "mksquashfs": "squashfs-tools",
                  "mkswap": "util-linux",
                  "mmd": "mtools",
                  "parted": "parted",
                  "sfdisk": "util-linux",
                  "sgdisk": "gptfdisk",
                  "syslinux": "syslinux"
                 }

def runtool(cmdln_or_args):
    """ wrapper for most of the subprocess calls
    input:
        cmdln_or_args: can be both args and cmdln str (shell=True)
    return:
        rc, output
    """
    if isinstance(cmdln_or_args, list):
        cmd = cmdln_or_args[0]
        shell = False
    else:
        import shlex
        cmd = shlex.split(cmdln_or_args)[0]
        shell = True

    sout = subprocess.PIPE
    serr = subprocess.STDOUT

    try:
        process = subprocess.Popen(cmdln_or_args, stdout=sout,
                                   stderr=serr, shell=shell)
        sout, serr = process.communicate()
        # combine stdout and stderr, filter None out and decode
        out = ''.join([out.decode('utf-8') for out in [sout, serr] if out])
    except OSError as err:
        if err.errno == 2:
            # [Errno 2] No such file or directory
            raise WicError('Cannot run command: %s, lost dependency?' % cmd)
        else:
            raise # relay

    return process.returncode, out

def _exec_cmd(cmd_and_args, as_shell=False):
    """
    Execute command, catching stderr, stdout

    Need to execute as_shell if the command uses wildcards
    """
    logger.debug("_exec_cmd: %s", cmd_and_args)
    args = cmd_and_args.split()
    logger.debug(args)

    if as_shell:
        ret, out = runtool(cmd_and_args)
    else:
        ret, out = runtool(args)
    out = out.strip()
    if ret != 0:
        raise WicError("_exec_cmd: %s returned '%s' instead of 0\noutput: %s" % \
                       (cmd_and_args, ret, out))

    logger.debug("_exec_cmd: output for %s (rc = %d): %s",
                 cmd_and_args, ret, out)

    return ret, out


def exec_cmd(cmd_and_args, as_shell=False):
    """
    Execute command, return output
    """
    return _exec_cmd(cmd_and_args, as_shell)[1]


def exec_native_cmd(cmd_and_args, native_sysroot, pseudo=""):
    """
    Execute native command, catching stderr, stdout

    Need to execute as_shell if the command uses wildcards

    Always need to execute native commands as_shell
    """
    # The reason -1 is used is because there may be "export" commands.
    args = cmd_and_args.split(';')[-1].split()
    logger.debug(args)

    if pseudo:
        cmd_and_args = pseudo + cmd_and_args

    hosttools_dir = get_bitbake_var("HOSTTOOLS_DIR")

    native_paths = "%s/sbin:%s/usr/sbin:%s/usr/bin:%s/bin:%s" % \
                   (native_sysroot, native_sysroot,
                    native_sysroot, native_sysroot,
                    hosttools_dir)

    native_cmd_and_args = "export PATH=%s:$PATH;%s" % \
                   (native_paths, cmd_and_args)
    logger.debug("exec_native_cmd: %s", native_cmd_and_args)

    # If the command isn't in the native sysroot say we failed.
    if spawn.find_executable(args[0], native_paths):
        ret, out = _exec_cmd(native_cmd_and_args, True)
    else:
        ret = 127
        out = "can't find native executable %s in %s" % (args[0], native_paths)

    prog = args[0]
    # shell command-not-found
    if ret == 127 \
       or (pseudo and ret == 1 and out == "Can't find '%s' in $PATH." % prog):
        msg = "A native program %s required to build the image "\
              "was not found (see details above).\n\n" % prog
        recipe = NATIVE_RECIPES.get(prog)
        if recipe:
            msg += "Please make sure wic-tools have %s-native in its DEPENDS, "\
                   "build it with 'bitbake wic-tools' and try again.\n" % recipe
        else:
            msg += "Wic failed to find a recipe to build native %s. Please "\
                   "file a bug against wic.\n" % prog
        raise WicError(msg)

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

    def _parse_line(self, line, image, matcher=re.compile(r"^([a-zA-Z0-9\-_+./~]+)=(.*)")):
        """
        Parse one line from bitbake -e output or from .env file.
        Put result key-value pair into the storage.
        """
        if "=" not in line:
            return
        match = matcher.match(line)
        if not match:
            return
        key, val = match.groups()
        self[image][key] = val.strip('"')

    def get_var(self, var, image=None, cache=True):
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
                    print("Couldn't get bitbake variable from %s." % fname)
                    print("File %s doesn't exist." % fname)
                    return
            else:
                # Get bitbake -e output
                cmd = "bitbake -e"
                if image:
                    cmd += " %s" % image

                log_level = logger.getEffectiveLevel()
                logger.setLevel(logging.INFO)
                ret, lines = _exec_cmd(cmd)
                logger.setLevel(log_level)

                if ret:
                    logger.error("Couldn't get '%s' output.", cmd)
                    logger.error("Bitbake failed with error:\n%s\n", lines)
                    return

                # Parse bitbake -e output
                for line in lines.split('\n'):
                    self._parse_line(line, image)

            # Make first image a default set of variables
            if cache:
                images = [key for key in self if key]
                if len(images) == 1:
                    self[None] = self[image]

        result = self[image].get(var)
        if not cache:
            self.pop(image, None)

        return result

# Create BB_VARS singleton
BB_VARS = BitbakeVars()

def get_bitbake_var(var, image=None, cache=True):
    """
    Provide old get_bitbake_var API by wrapping
    get_var method of BB_VARS singleton.
    """
    return BB_VARS.get_var(var, image, cache)
