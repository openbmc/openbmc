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
# This implements the 'direct' image creator class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import os
import shutil

from wic import msger
from wic.utils import fs_related
from wic.utils.oe.misc import get_bitbake_var
from wic.utils.partitionedfs import Image
from wic.utils.errors import CreatorError, ImageError
from wic.imager.baseimager import BaseImageCreator
from wic.plugin import pluginmgr
from wic.utils.oe.misc import exec_cmd

disk_methods = {
    "do_install_disk":None,
}

class DirectImageCreator(BaseImageCreator):
    """
    Installs a system into a file containing a partitioned disk image.

    DirectImageCreator is an advanced ImageCreator subclass; an image
    file is formatted with a partition table, each partition created
    from a rootfs or other OpenEmbedded build artifact and dd'ed into
    the virtual disk. The disk image can subsequently be dd'ed onto
    media and used on actual hardware.
    """

    def __init__(self, oe_builddir, image_output_dir, rootfs_dir, bootimg_dir,
                 kernel_dir, native_sysroot, compressor, creatoropts=None):
        """
        Initialize a DirectImageCreator instance.

        This method takes the same arguments as ImageCreator.__init__()
        """
        BaseImageCreator.__init__(self, creatoropts)

        self.__image = None
        self.__disks = {}
        self.__disk_format = "direct"
        self._disk_names = []
        self.ptable_format = self.ks.bootloader.ptable

        self.oe_builddir = oe_builddir
        if image_output_dir:
            self.tmpdir = image_output_dir
        self.rootfs_dir = rootfs_dir
        self.bootimg_dir = bootimg_dir
        self.kernel_dir = kernel_dir
        self.native_sysroot = native_sysroot
        self.compressor = compressor

    def __get_part_num(self, num, parts):
        """calculate the real partition number, accounting for partitions not
        in the partition table and logical partitions
        """
        realnum = 0
        for pnum, part in enumerate(parts, 1):
            if not part.no_table:
                realnum += 1
            if pnum == num:
                if  part.no_table:
                    return 0
                if self.ptable_format == 'msdos' and realnum > 3:
                    # account for logical partition numbering, ex. sda5..
                    return realnum + 1
                return realnum

    def _write_fstab(self, image_rootfs):
        """overriden to generate fstab (temporarily) in rootfs. This is called
        from _create, make sure it doesn't get called from
        BaseImage.create()
        """
        if not image_rootfs:
            return

        fstab_path = image_rootfs + "/etc/fstab"
        if not os.path.isfile(fstab_path):
            return

        with open(fstab_path) as fstab:
            fstab_lines = fstab.readlines()

        if self._update_fstab(fstab_lines, self._get_parts()):
            shutil.copyfile(fstab_path, fstab_path + ".orig")

            with open(fstab_path, "w") as fstab:
                fstab.writelines(fstab_lines)

            return fstab_path

    def _update_fstab(self, fstab_lines, parts):
        """Assume partition order same as in wks"""
        updated = False
        for num, part in enumerate(parts, 1):
            pnum = self.__get_part_num(num, parts)
            if not pnum or not part.mountpoint \
               or part.mountpoint in ("/", "/boot"):
                continue

            # mmc device partitions are named mmcblk0p1, mmcblk0p2..
            prefix = 'p' if  part.disk.startswith('mmcblk') else ''
            device_name = "/dev/%s%s%d" % (part.disk, prefix, pnum)

            opts = part.fsopts if part.fsopts else "defaults"
            line = "\t".join([device_name, part.mountpoint, part.fstype,
                              opts, "0", "0"]) + "\n"

            fstab_lines.append(line)
            updated = True

        return updated

    def set_bootimg_dir(self, bootimg_dir):
        """
        Accessor for bootimg_dir, the actual location used for the source
        of the bootimg.  Should be set by source plugins (only if they
        change the default bootimg source) so the correct info gets
        displayed for print_outimage_info().
        """
        self.bootimg_dir = bootimg_dir

    def _get_parts(self):
        if not self.ks:
            raise CreatorError("Failed to get partition info, "
                               "please check your kickstart setting.")

        # Set a default partition if no partition is given out
        if not self.ks.partitions:
            partstr = "part / --size 1900 --ondisk sda --fstype=ext3"
            args = partstr.split()
            part = self.ks.parse(args[1:])
            if part not in self.ks.partitions:
                self.ks.partitions.append(part)

        # partitions list from kickstart file
        return self.ks.partitions

    def get_disk_names(self):
        """ Returns a list of physical target disk names (e.g., 'sdb') which
        will be created. """

        if self._disk_names:
            return self._disk_names

        #get partition info from ks handler
        parts = self._get_parts()

        for i in range(len(parts)):
            if parts[i].disk:
                disk_name = parts[i].disk
            else:
                raise CreatorError("Failed to create disks, no --ondisk "
                                   "specified in partition line of ks file")

            if parts[i].mountpoint and not parts[i].fstype:
                raise CreatorError("Failed to create disks, no --fstype "
                                   "specified for partition with mountpoint "
                                   "'%s' in the ks file")

            self._disk_names.append(disk_name)

        return self._disk_names

    def _full_name(self, name, extention):
        """ Construct full file name for a file we generate. """
        return "%s-%s.%s" % (self.name, name, extention)

    def _full_path(self, path, name, extention):
        """ Construct full file path to a file we generate. """
        return os.path.join(path, self._full_name(name, extention))

    def get_default_source_plugin(self):
        """
        The default source plugin i.e. the plugin that's consulted for
        overall image generation tasks outside of any particular
        partition.  For convenience, we just hang it off the
        bootloader handler since it's the one non-partition object in
        any setup.  By default the default plugin is set to the same
        plugin as the /boot partition; since we hang it off the
        bootloader object, the default can be explicitly set using the
        --source bootloader param.
        """
        return self.ks.bootloader.source

    #
    # Actual implemention
    #
    def _create(self):
        """
        For 'wic', we already have our build artifacts - we just create
        filesystems from the artifacts directly and combine them into
        a partitioned image.
        """
        parts = self._get_parts()

        self.__image = Image(self.native_sysroot)

        for part in parts:
            # as a convenience, set source to the boot partition source
            # instead of forcing it to be set via bootloader --source
            if not self.ks.bootloader.source and part.mountpoint == "/boot":
                self.ks.bootloader.source = part.source

        fstab_path = self._write_fstab(self.rootfs_dir.get("ROOTFS_DIR"))

        shutil.rmtree(self.workdir)
        os.mkdir(self.workdir)

        for part in parts:
            # get rootfs size from bitbake variable if it's not set in .ks file
            if not part.size:
                # and if rootfs name is specified for the partition
                image_name = part.rootfs_dir
                if image_name:
                    # Bitbake variable ROOTFS_SIZE is calculated in
                    # Image._get_rootfs_size method from meta/lib/oe/image.py
                    # using IMAGE_ROOTFS_SIZE, IMAGE_ROOTFS_ALIGNMENT,
                    # IMAGE_OVERHEAD_FACTOR and IMAGE_ROOTFS_EXTRA_SPACE
                    rsize_bb = get_bitbake_var('ROOTFS_SIZE', image_name)
                    if rsize_bb:
                        part.size = int(round(float(rsize_bb)))
            # need to create the filesystems in order to get their
            # sizes before we can add them and do the layout.
            # Image.create() actually calls __format_disks() to create
            # the disk images and carve out the partitions, then
            # self.assemble() calls Image.assemble() which calls
            # __write_partitition() for each partition to dd the fs
            # into the partitions.
            part.prepare(self, self.workdir, self.oe_builddir, self.rootfs_dir,
                         self.bootimg_dir, self.kernel_dir, self.native_sysroot)


            self.__image.add_partition(int(part.size),
                                       part.disk,
                                       part.mountpoint,
                                       part.source_file,
                                       part.fstype,
                                       part.label,
                                       fsopts=part.fsopts,
                                       boot=part.active,
                                       align=part.align,
                                       no_table=part.no_table,
                                       part_type=part.part_type,
                                       uuid=part.uuid)

        if fstab_path:
            shutil.move(fstab_path + ".orig", fstab_path)

        self.__image.layout_partitions(self.ptable_format)

        self.__imgdir = self.workdir
        for disk_name, disk in self.__image.disks.items():
            full_path = self._full_path(self.__imgdir, disk_name, "direct")
            msger.debug("Adding disk %s as %s with size %s bytes" \
                        % (disk_name, full_path, disk['min_size']))
            disk_obj = fs_related.DiskImage(full_path, disk['min_size'])
            self.__disks[disk_name] = disk_obj
            self.__image.add_disk(disk_name, disk_obj)

        self.__image.create()

    def assemble(self):
        """
        Assemble partitions into disk image(s)
        """
        for disk_name, disk in self.__image.disks.items():
            full_path = self._full_path(self.__imgdir, disk_name, "direct")
            msger.debug("Assembling disk %s as %s with size %s bytes" \
                        % (disk_name, full_path, disk['min_size']))
            self.__image.assemble(full_path)

    def finalize(self):
        """
        Finalize the disk image.

        For example, prepare the image to be bootable by e.g.
        creating and installing a bootloader configuration.

        """
        source_plugin = self.get_default_source_plugin()
        if source_plugin:
            self._source_methods = pluginmgr.get_source_plugin_methods(source_plugin, disk_methods)
            for disk_name, disk in self.__image.disks.items():
                self._source_methods["do_install_disk"](disk, disk_name, self,
                                                        self.workdir,
                                                        self.oe_builddir,
                                                        self.bootimg_dir,
                                                        self.kernel_dir,
                                                        self.native_sysroot)
        # Compress the image
        if self.compressor:
            for disk_name, disk in self.__image.disks.items():
                full_path = self._full_path(self.__imgdir, disk_name, "direct")
                msger.debug("Compressing disk %s with %s" % \
                            (disk_name, self.compressor))
                exec_cmd("%s %s" % (self.compressor, full_path))

    def print_outimage_info(self):
        """
        Print the image(s) and artifacts used, for the user.
        """
        msg = "The new image(s) can be found here:\n"

        parts = self._get_parts()

        for disk_name in self.__image.disks:
            extension = "direct" + {"gzip": ".gz",
                                    "bzip2": ".bz2",
                                    "xz": ".xz",
                                    "": ""}.get(self.compressor)
            full_path = self._full_path(self.__imgdir, disk_name, extension)
            msg += '  %s\n\n' % full_path

        msg += 'The following build artifacts were used to create the image(s):\n'
        for part in parts:
            if part.rootfs_dir is None:
                continue
            if part.mountpoint == '/':
                suffix = ':'
            else:
                suffix = '["%s"]:' % (part.mountpoint or part.label)
            msg += '  ROOTFS_DIR%s%s\n' % (suffix.ljust(20), part.rootfs_dir)

        msg += '  BOOTIMG_DIR:                  %s\n' % self.bootimg_dir
        msg += '  KERNEL_DIR:                   %s\n' % self.kernel_dir
        msg += '  NATIVE_SYSROOT:               %s\n' % self.native_sysroot

        msger.info(msg)

    @property
    def rootdev(self):
        """
        Get root device name to use as a 'root' parameter
        in kernel command line.

        Assume partition order same as in wks
        """
        parts = self._get_parts()
        for num, part in enumerate(parts, 1):
            if part.mountpoint == "/":
                if part.uuid:
                    return "PARTUUID=%s" % part.uuid
                else:
                    suffix = 'p' if part.disk.startswith('mmcblk') else ''
                    pnum = self.__get_part_num(num, parts)
                    return "/dev/%s%s%-d" % (part.disk, suffix, pnum)

    def _cleanup(self):
        if not self.__image is None:
            try:
                self.__image.cleanup()
            except ImageError, err:
                msger.warning("%s" % err)

