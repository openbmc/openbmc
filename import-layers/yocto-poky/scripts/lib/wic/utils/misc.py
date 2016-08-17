#!/usr/bin/env python -tt
#
# Copyright (c) 2010, 2011 Intel Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.

import os
import time
import wic.engine

def build_name(kscfg, release=None, prefix=None, suffix=None):
    """Construct and return an image name string.

    This is a utility function to help create sensible name and fslabel
    strings. The name is constructed using the sans-prefix-and-extension
    kickstart filename and the supplied prefix and suffix.

    kscfg -- a path to a kickstart file
    release --  a replacement to suffix for image release
    prefix -- a prefix to prepend to the name; defaults to None, which causes
              no prefix to be used
    suffix -- a suffix to append to the name; defaults to None, which causes
              a YYYYMMDDHHMM suffix to be used

    Note, if maxlen is less then the len(suffix), you get to keep both pieces.

    """
    name = os.path.basename(kscfg)
    idx = name.rfind('.')
    if idx >= 0:
        name = name[:idx]

    if release is not None:
        suffix = ""
    if prefix is None:
        prefix = ""
    if suffix is None:
        suffix = time.strftime("%Y%m%d%H%M")

    if name.startswith(prefix):
        name = name[len(prefix):]

    prefix = "%s-" % prefix if prefix else ""
    suffix = "-%s" % suffix if suffix else ""

    ret = prefix + name + suffix

    return ret

def find_canned(scripts_path, file_name):
    """
    Find a file either by its path or by name in the canned files dir.

    Return None if not found
    """
    if os.path.exists(file_name):
        return file_name

    layers_canned_wks_dir = wic.engine.build_canned_image_list(scripts_path)
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
    scripts_path = os.path.abspath(os.path.dirname(__file__))
    # Get the scripts path of poky
    for x in range(0, 3):
        scripts_path = os.path.dirname(scripts_path)

    cfg_file = find_canned(scripts_path, boot_file)
    if cfg_file:
        with open(cfg_file, "r") as f:
            config = f.read()
        return config

    return None
