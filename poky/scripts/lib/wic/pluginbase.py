#!/usr/bin/env python3
#
# Copyright (c) 2011 Intel, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

__all__ = ['ImagerPlugin', 'SourcePlugin']

import os
import logging

from collections import defaultdict
from importlib.machinery import SourceFileLoader

from wic import WicError
from wic.misc import get_bitbake_var

PLUGIN_TYPES = ["imager", "source"]

SCRIPTS_PLUGIN_DIR = "scripts/lib/wic/plugins"

logger = logging.getLogger('wic')

PLUGINS = defaultdict(dict)

class PluginMgr:
    _plugin_dirs = []

    @classmethod
    def get_plugins(cls, ptype):
        """Get dictionary of <plugin_name>:<class> pairs."""
        if ptype not in PLUGIN_TYPES:
            raise WicError('%s is not valid plugin type' % ptype)

        # collect plugin directories
        if not cls._plugin_dirs:
            cls._plugin_dirs = [os.path.join(os.path.dirname(__file__), 'plugins')]
            layers = get_bitbake_var("BBLAYERS") or ''
            for layer_path in layers.split():
                path = os.path.join(layer_path, SCRIPTS_PLUGIN_DIR)
                path = os.path.abspath(os.path.expanduser(path))
                if path not in cls._plugin_dirs and os.path.isdir(path):
                    cls._plugin_dirs.insert(0, path)

        if ptype not in PLUGINS:
            # load all ptype plugins
            for pdir in cls._plugin_dirs:
                ppath = os.path.join(pdir, ptype)
                if os.path.isdir(ppath):
                    for fname in os.listdir(ppath):
                        if fname.endswith('.py'):
                            mname = fname[:-3]
                            mpath = os.path.join(ppath, fname)
                            logger.debug("loading plugin module %s", mpath)
                            SourceFileLoader(mname, mpath).load_module()

        return PLUGINS.get(ptype)

class PluginMeta(type):
    def __new__(cls, name, bases, attrs):
        class_type = type.__new__(cls, name, bases, attrs)
        if 'name' in attrs:
            PLUGINS[class_type.wic_plugin_type][attrs['name']] = class_type

        return class_type

class ImagerPlugin(metaclass=PluginMeta):
    wic_plugin_type = "imager"

    def do_create(self):
        raise WicError("Method %s.do_create is not implemented" %
                       self.__class__.__name__)

class SourcePlugin(metaclass=PluginMeta):
    wic_plugin_type = "source"
    """
    The methods that can be implemented by --source plugins.

    Any methods not implemented in a subclass inherit these.
    """

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.  This provides a hook to allow finalization of a
        disk image e.g. to write an MBR to it.
        """
        logger.debug("SourcePlugin: do_install_disk: disk: %s", disk_name)

    @classmethod
    def do_stage_partition(cls, part, source_params, creator, cr_workdir,
                           oe_builddir, bootimg_dir, kernel_dir,
                           native_sysroot):
        """
        Special content staging hook called before do_prepare_partition(),
        normally empty.

        Typically, a partition will just use the passed-in parame e.g
        straight bootimg_dir, etc, but in some cases, things need to
        be more tailored e.g. to use a deploy dir + /boot, etc.  This
        hook allows those files to be staged in a customized fashion.
        Not that get_bitbake_var() allows you to acces non-standard
        variables that you might want to use for this.
        """
        logger.debug("SourcePlugin: do_stage_partition: part: %s", part)

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), typically used to create
        custom configuration files for a partition, for example
        syslinux or grub config files.
        """
        logger.debug("SourcePlugin: do_configure_partition: part: %s", part)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir, rootfs_dir,
                             native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        logger.debug("SourcePlugin: do_prepare_partition: part: %s", part)

    @classmethod
    def do_post_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir, rootfs_dir,
                             native_sysroot):
        """
        Called after the partition is created. It is useful to add post
        operations e.g. security signing the partition.
        """
        logger.debug("SourcePlugin: do_post_partition: part: %s", part)
