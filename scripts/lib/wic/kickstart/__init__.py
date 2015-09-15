#!/usr/bin/env python -tt
#
# Copyright (c) 2007 Red Hat, Inc.
# Copyright (c) 2009, 2010, 2011 Intel, Inc.
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

import os, sys, re
import shutil
import subprocess
import string

import pykickstart.sections as kssections
import pykickstart.commands as kscommands
import pykickstart.constants as ksconstants
import pykickstart.errors as kserrors
import pykickstart.parser as ksparser
import pykickstart.version as ksversion
from pykickstart.handlers.control import commandMap
from pykickstart.handlers.control import dataMap

from wic import msger
from wic.utils import errors, misc, runner, fs_related as fs
from custom_commands import wicboot, partition

def read_kickstart(path):
    """Parse a kickstart file and return a KickstartParser instance.

    This is a simple utility function which takes a path to a kickstart file,
    parses it and returns a pykickstart KickstartParser instance which can
    be then passed to an ImageCreator constructor.

    If an error occurs, a CreatorError exception is thrown.
    """

    #version = ksversion.makeVersion()
    #ks = ksparser.KickstartParser(version)

    using_version = ksversion.DEVEL
    commandMap[using_version]["bootloader"] = wicboot.Wic_Bootloader
    commandMap[using_version]["part"] = partition.Wic_Partition
    commandMap[using_version]["partition"] = partition.Wic_Partition
    dataMap[using_version]["PartData"] = partition.Wic_PartData
    superclass = ksversion.returnClassForVersion(version=using_version)

    class KSHandlers(superclass):
        def __init__(self):
            superclass.__init__(self, mapping=commandMap[using_version])

    kickstart = ksparser.KickstartParser(KSHandlers(), errorsAreFatal=True)

    try:
        kickstart.readKickstart(path)
    except (kserrors.KickstartParseError, kserrors.KickstartError), err:
        msger.warning("Errors occurred when parsing kickstart file: %s\n" % path)
        msger.error("%s" % err)

    return kickstart

def get_image_size(kickstart, default=None):
    __size = 0
    for part in kickstart.handler.partition.partitions:
        if part.mountpoint == "/" and part.size:
            __size = part.size
    if __size > 0:
        return int(__size) * 1024L
    else:
        return default

def get_image_fstype(kickstart, default=None):
    for part in kickstart.handler.partition.partitions:
        if part.mountpoint == "/" and part.fstype:
            return part.fstype
    return default

def get_image_fsopts(kickstart, default=None):
    for part in kickstart.handler.partition.partitions:
        if part.mountpoint == "/" and part.fsopts:
            return part.fsopts
    return default

def get_timeout(kickstart, default=None):
    if not hasattr(kickstart.handler.bootloader, "timeout"):
        return default
    if kickstart.handler.bootloader.timeout is None:
        return default
    return int(kickstart.handler.bootloader.timeout)

def get_kernel_args(kickstart, default="ro rd.live.image"):
    if not hasattr(kickstart.handler.bootloader, "appendLine"):
        return default
    if kickstart.handler.bootloader.appendLine is None:
        return default
    return "%s %s" %(default, kickstart.handler.bootloader.appendLine)

def get_menu_args(kickstart, default=""):
    if not hasattr(kickstart.handler.bootloader, "menus"):
        return default
    if kickstart.handler.bootloader.menus in (None, ""):
        return default
    return "%s" % kickstart.handler.bootloader.menus

def get_default_kernel(kickstart, default=None):
    if not hasattr(kickstart.handler.bootloader, "default"):
        return default
    if not kickstart.handler.bootloader.default:
        return default
    return kickstart.handler.bootloader.default

def get_partitions(kickstart):
    return kickstart.handler.partition.partitions
