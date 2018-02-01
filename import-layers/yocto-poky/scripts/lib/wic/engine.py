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

# This module implements the image creation engine used by 'wic' to
# create images.  The engine parses through the OpenEmbedded kickstart
# (wks) file specified and generates images that can then be directly
# written onto media.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os

from wic import WicError
from wic.pluginbase import PluginMgr
from wic.utils.misc import get_bitbake_var

logger = logging.getLogger('wic')

def verify_build_env():
    """
    Verify that the build environment is sane.

    Returns True if it is, false otherwise
    """
    if not os.environ.get("BUILDDIR"):
        raise WicError("BUILDDIR not found, exiting. (Did you forget to source oe-init-build-env?)")

    return True


CANNED_IMAGE_DIR = "lib/wic/canned-wks" # relative to scripts
SCRIPTS_CANNED_IMAGE_DIR = "scripts/" + CANNED_IMAGE_DIR
WIC_DIR = "wic"

def build_canned_image_list(path):
    layers_path = get_bitbake_var("BBLAYERS")
    canned_wks_layer_dirs = []

    if layers_path is not None:
        for layer_path in layers_path.split():
            for wks_path in (WIC_DIR, SCRIPTS_CANNED_IMAGE_DIR):
                cpath = os.path.join(layer_path, wks_path)
                if os.path.isdir(cpath):
                    canned_wks_layer_dirs.append(cpath)

    cpath = os.path.join(path, CANNED_IMAGE_DIR)
    canned_wks_layer_dirs.append(cpath)

    return canned_wks_layer_dirs

def find_canned_image(scripts_path, wks_file):
    """
    Find a .wks file with the given name in the canned files dir.

    Return False if not found
    """
    layers_canned_wks_dir = build_canned_image_list(scripts_path)

    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname.endswith("~") or fname.endswith("#"):
                    continue
                if fname.endswith(".wks") and wks_file + ".wks" == fname:
                    fullpath = os.path.join(canned_wks_dir, fname)
                    return fullpath
    return None


def list_canned_images(scripts_path):
    """
    List the .wks files in the canned image dir, minus the extension.
    """
    layers_canned_wks_dir = build_canned_image_list(scripts_path)

    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname.endswith("~") or fname.endswith("#"):
                    continue
                if fname.endswith(".wks"):
                    fullpath = os.path.join(canned_wks_dir, fname)
                    with open(fullpath) as wks:
                        for line in wks:
                            desc = ""
                            idx = line.find("short-description:")
                            if idx != -1:
                                desc = line[idx + len("short-description:"):].strip()
                                break
                    basename = os.path.splitext(fname)[0]
                    print("  %s\t\t%s" % (basename.ljust(30), desc))


def list_canned_image_help(scripts_path, fullpath):
    """
    List the help and params in the specified canned image.
    """
    found = False
    with open(fullpath) as wks:
        for line in wks:
            if not found:
                idx = line.find("long-description:")
                if idx != -1:
                    print()
                    print(line[idx + len("long-description:"):].strip())
                    found = True
                continue
            if not line.strip():
                break
            idx = line.find("#")
            if idx != -1:
                print(line[idx + len("#:"):].rstrip())
            else:
                break


def list_source_plugins():
    """
    List the available source plugins i.e. plugins available for --source.
    """
    plugins = PluginMgr.get_plugins('source')

    for plugin in plugins:
        print("  %s" % plugin)


def wic_create(wks_file, rootfs_dir, bootimg_dir, kernel_dir,
               native_sysroot, options):
    """
    Create image

    wks_file - user-defined OE kickstart file
    rootfs_dir - absolute path to the build's /rootfs dir
    bootimg_dir - absolute path to the build's boot artifacts directory
    kernel_dir - absolute path to the build's kernel directory
    native_sysroot - absolute path to the build's native sysroots dir
    image_output_dir - dirname to create for image
    options - wic command line options (debug, bmap, etc)

    Normally, the values for the build artifacts values are determined
    by 'wic -e' from the output of the 'bitbake -e' command given an
    image name e.g. 'core-image-minimal' and a given machine set in
    local.conf.  If that's the case, the variables get the following
    values from the output of 'bitbake -e':

    rootfs_dir:        IMAGE_ROOTFS
    kernel_dir:        DEPLOY_DIR_IMAGE
    native_sysroot:    STAGING_DIR_NATIVE

    In the above case, bootimg_dir remains unset and the
    plugin-specific image creation code is responsible for finding the
    bootimg artifacts.

    In the case where the values are passed in explicitly i.e 'wic -e'
    is not used but rather the individual 'wic' options are used to
    explicitly specify these values.
    """
    try:
        oe_builddir = os.environ["BUILDDIR"]
    except KeyError:
        raise WicError("BUILDDIR not found, exiting. (Did you forget to source oe-init-build-env?)")

    if not os.path.exists(options.outdir):
        os.makedirs(options.outdir)

    pname = 'direct'
    plugin_class = PluginMgr.get_plugins('imager').get(pname)
    if not plugin_class:
        raise WicError('Unknown plugin: %s' % pname)

    plugin = plugin_class(wks_file, rootfs_dir, bootimg_dir, kernel_dir,
                          native_sysroot, oe_builddir, options)

    plugin.do_create()

    logger.info("The image(s) were created using OE kickstart file:\n  %s", wks_file)


def wic_list(args, scripts_path):
    """
    Print the list of images or source plugins.
    """
    if len(args) < 1:
        return False

    if args == ["images"]:
        list_canned_images(scripts_path)
        return True
    elif args == ["source-plugins"]:
        list_source_plugins()
        return True
    elif len(args) == 2 and args[1] == "help":
        wks_file = args[0]
        fullpath = find_canned_image(scripts_path, wks_file)
        if not fullpath:
            raise WicError("No image named %s found, exiting. "
                           "(Use 'wic list images' to list available images, "
                           "or specify a fully-qualified OE kickstart (.wks) "
                           "filename)" % wks_file)

        list_canned_image_help(scripts_path, fullpath)
        return True

    return False

def find_canned(scripts_path, file_name):
    """
    Find a file either by its path or by name in the canned files dir.

    Return None if not found
    """
    if os.path.exists(file_name):
        return file_name

    layers_canned_wks_dir = build_canned_image_list(scripts_path)
    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname == file_name:
                    fullpath = os.path.join(canned_wks_dir, fname)
                    return fullpath

def get_custom_config(boot_file):
    """
    Get the custom configuration to be used for the bootloader.

    Return None if the file can't be found.
    """
    # Get the scripts path of poky
    scripts_path = os.path.abspath("%s/../.." % os.path.dirname(__file__))

    cfg_file = find_canned(scripts_path, boot_file)
    if cfg_file:
        with open(cfg_file, "r") as f:
            config = f.read()
        return config
