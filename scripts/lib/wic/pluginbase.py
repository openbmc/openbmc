#!/usr/bin/env python -tt
#
# Copyright (c) 2011 Intel, Inc.
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

from wic import msger

class _Plugin(object):
    class __metaclass__(type):
        def __init__(cls, name, bases, attrs):
            if not hasattr(cls, 'plugins'):
                cls.plugins = {}

            elif 'wic_plugin_type' in attrs:
                if attrs['wic_plugin_type'] not in cls.plugins:
                    cls.plugins[attrs['wic_plugin_type']] = {}

            elif hasattr(cls, 'wic_plugin_type') and 'name' in attrs:
                cls.plugins[cls.wic_plugin_type][attrs['name']] = cls

        def show_plugins(cls):
            for cls in cls.plugins[cls.wic_plugin_type]:
                print cls

        def get_plugins(cls):
            return cls.plugins


class ImagerPlugin(_Plugin):
    wic_plugin_type = "imager"


class SourcePlugin(_Plugin):
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
        msger.debug("SourcePlugin: do_install_disk: disk: %s" % disk_name)

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
        msger.debug("SourcePlugin: do_stage_partition: part: %s" % part)

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), typically used to create
        custom configuration files for a partition, for example
        syslinux or grub config files.
        """
        msger.debug("SourcePlugin: do_configure_partition: part: %s" % part)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir, rootfs_dir,
                             native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        msger.debug("SourcePlugin: do_prepare_partition: part: %s" % part)

def get_plugins(typen):
    plugins = ImagerPlugin.get_plugins()
    if typen in plugins:
        return plugins[typen]
    else:
        return None

__all__ = ['ImagerPlugin', 'SourcePlugin', 'get_plugins']
