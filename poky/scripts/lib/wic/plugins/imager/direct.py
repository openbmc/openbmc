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
# This implements the 'direct' imager plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import random
import shutil
import tempfile
import uuid

from time import strftime

from oe.path import copyhardlinktree

from wic import WicError
from wic.filemap import sparse_copy
from wic.ksparser import KickStart, KickStartError
from wic.pluginbase import PluginMgr, ImagerPlugin
from wic.misc import get_bitbake_var, exec_cmd, exec_native_cmd

logger = logging.getLogger('wic')

class DirectPlugin(ImagerPlugin):
    """
    Install a system into a file containing a partitioned disk image.

    An image file is formatted with a partition table, each partition
    created from a rootfs or other OpenEmbedded build artifact and dd'ed
    into the virtual disk. The disk image can subsequently be dd'ed onto
    media and used on actual hardware.
    """
    name = 'direct'

    def __init__(self, wks_file, rootfs_dir, bootimg_dir, kernel_dir,
                 native_sysroot, oe_builddir, options):
        try:
            self.ks = KickStart(wks_file)
        except KickStartError as err:
            raise WicError(str(err))

        # parse possible 'rootfs=name' items
        self.rootfs_dir = dict(rdir.split('=') for rdir in rootfs_dir.split(' '))
        self.replaced_rootfs_paths = {}
        self.bootimg_dir = bootimg_dir
        self.kernel_dir = kernel_dir
        self.native_sysroot = native_sysroot
        self.oe_builddir = oe_builddir

        self.outdir = options.outdir
        self.compressor = options.compressor
        self.bmap = options.bmap
        self.no_fstab_update = options.no_fstab_update

        self.name = "%s-%s" % (os.path.splitext(os.path.basename(wks_file))[0],
                               strftime("%Y%m%d%H%M"))
        self.workdir = tempfile.mkdtemp(dir=self.outdir, prefix='tmp.wic.')
        self._image = None
        self.ptable_format = self.ks.bootloader.ptable
        self.parts = self.ks.partitions

        # as a convenience, set source to the boot partition source
        # instead of forcing it to be set via bootloader --source
        for part in self.parts:
            if not self.ks.bootloader.source and part.mountpoint == "/boot":
                self.ks.bootloader.source = part.source
                break

        image_path = self._full_path(self.workdir, self.parts[0].disk, "direct")
        self._image = PartitionedImage(image_path, self.ptable_format,
                                       self.parts, self.native_sysroot)

    def do_create(self):
        """
        Plugin entry point.
        """
        try:
            self.create()
            self.assemble()
            self.finalize()
            self.print_info()
        finally:
            self.cleanup()

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

        if self._update_fstab(fstab_lines, self.parts):
            # copy rootfs dir to workdir to update fstab
            # as rootfs can be used by other tasks and can't be modified
            new_pseudo = os.path.realpath(os.path.join(self.workdir, "pseudo"))
            from_dir = os.path.join(os.path.join(image_rootfs, ".."), "pseudo")
            from_dir = os.path.realpath(from_dir)
            copyhardlinktree(from_dir, new_pseudo)
            new_rootfs = os.path.realpath(os.path.join(self.workdir, "rootfs_copy"))
            copyhardlinktree(image_rootfs, new_rootfs)
            fstab_path = os.path.join(new_rootfs, 'etc/fstab')

            os.unlink(fstab_path)

            with open(fstab_path, "w") as fstab:
                fstab.writelines(fstab_lines)

            return new_rootfs

    def _update_fstab(self, fstab_lines, parts):
        """Assume partition order same as in wks"""
        updated = False
        for part in parts:
            if not part.realnum or not part.mountpoint \
               or part.mountpoint == "/":
                continue

            if part.use_uuid:
                if part.fsuuid:
                    # FAT UUID is different from others
                    if len(part.fsuuid) == 10:
                        device_name = "UUID=%s-%s" % \
                                       (part.fsuuid[2:6], part.fsuuid[6:])
                    else:
                        device_name = "UUID=%s" % part.fsuuid
                else:
                    device_name = "PARTUUID=%s" % part.uuid
            elif part.use_label:
                device_name = "LABEL=%s" % part.label
            else:
                # mmc device partitions are named mmcblk0p1, mmcblk0p2..
                prefix = 'p' if  part.disk.startswith('mmcblk') else ''
                device_name = "/dev/%s%s%d" % (part.disk, prefix, part.realnum)

            opts = part.fsopts if part.fsopts else "defaults"
            line = "\t".join([device_name, part.mountpoint, part.fstype,
                              opts, "0", "0"]) + "\n"

            fstab_lines.append(line)
            updated = True

        return updated

    def _full_path(self, path, name, extention):
        """ Construct full file path to a file we generate. """
        return os.path.join(path, "%s-%s.%s" % (self.name, name, extention))

    #
    # Actual implemention
    #
    def create(self):
        """
        For 'wic', we already have our build artifacts - we just create
        filesystems from the artifacts directly and combine them into
        a partitioned image.
        """
        if self.no_fstab_update:
            new_rootfs = None
        else:
            new_rootfs = self._write_fstab(self.rootfs_dir.get("ROOTFS_DIR"))
        if new_rootfs:
            # rootfs was copied to update fstab
            self.replaced_rootfs_paths[new_rootfs] = self.rootfs_dir['ROOTFS_DIR']
            self.rootfs_dir['ROOTFS_DIR'] = new_rootfs

        for part in self.parts:
            # get rootfs size from bitbake variable if it's not set in .ks file
            if not part.size:
                # and if rootfs name is specified for the partition
                image_name = self.rootfs_dir.get(part.rootfs_dir)
                if image_name and os.path.sep not in image_name:
                    # Bitbake variable ROOTFS_SIZE is calculated in
                    # Image._get_rootfs_size method from meta/lib/oe/image.py
                    # using IMAGE_ROOTFS_SIZE, IMAGE_ROOTFS_ALIGNMENT,
                    # IMAGE_OVERHEAD_FACTOR and IMAGE_ROOTFS_EXTRA_SPACE
                    rsize_bb = get_bitbake_var('ROOTFS_SIZE', image_name)
                    if rsize_bb:
                        part.size = int(round(float(rsize_bb)))

        self._image.prepare(self)
        self._image.layout_partitions()
        self._image.create()

    def assemble(self):
        """
        Assemble partitions into disk image
        """
        self._image.assemble()

    def finalize(self):
        """
        Finalize the disk image.

        For example, prepare the image to be bootable by e.g.
        creating and installing a bootloader configuration.
        """
        source_plugin = self.ks.bootloader.source
        disk_name = self.parts[0].disk
        if source_plugin:
            plugin = PluginMgr.get_plugins('source')[source_plugin]
            plugin.do_install_disk(self._image, disk_name, self, self.workdir,
                                   self.oe_builddir, self.bootimg_dir,
                                   self.kernel_dir, self.native_sysroot)

        full_path = self._image.path
        # Generate .bmap
        if self.bmap:
            logger.debug("Generating bmap file for %s", disk_name)
            python = os.path.join(self.native_sysroot, 'usr/bin/python3-native/python3')
            bmaptool = os.path.join(self.native_sysroot, 'usr/bin/bmaptool')
            exec_native_cmd("%s %s create %s -o %s.bmap" % \
                            (python, bmaptool, full_path, full_path), self.native_sysroot)
        # Compress the image
        if self.compressor:
            logger.debug("Compressing disk %s with %s", disk_name, self.compressor)
            exec_cmd("%s %s" % (self.compressor, full_path))

    def print_info(self):
        """
        Print the image(s) and artifacts used, for the user.
        """
        msg = "The new image(s) can be found here:\n"

        extension = "direct" + {"gzip": ".gz",
                                "bzip2": ".bz2",
                                "xz": ".xz",
                                None: ""}.get(self.compressor)
        full_path = self._full_path(self.outdir, self.parts[0].disk, extension)
        msg += '  %s\n\n' % full_path

        msg += 'The following build artifacts were used to create the image(s):\n'
        for part in self.parts:
            if part.rootfs_dir is None:
                continue
            if part.mountpoint == '/':
                suffix = ':'
            else:
                suffix = '["%s"]:' % (part.mountpoint or part.label)
            rootdir = part.rootfs_dir
            if rootdir in self.replaced_rootfs_paths:
                rootdir = self.replaced_rootfs_paths[rootdir]
            msg += '  ROOTFS_DIR%s%s\n' % (suffix.ljust(20), rootdir)

        msg += '  BOOTIMG_DIR:                  %s\n' % self.bootimg_dir
        msg += '  KERNEL_DIR:                   %s\n' % self.kernel_dir
        msg += '  NATIVE_SYSROOT:               %s\n' % self.native_sysroot

        logger.info(msg)

    @property
    def rootdev(self):
        """
        Get root device name to use as a 'root' parameter
        in kernel command line.

        Assume partition order same as in wks
        """
        for part in self.parts:
            if part.mountpoint == "/":
                if part.uuid:
                    return "PARTUUID=%s" % part.uuid
                else:
                    suffix = 'p' if part.disk.startswith('mmcblk') else ''
                    return "/dev/%s%s%-d" % (part.disk, suffix, part.realnum)

    def cleanup(self):
        if self._image:
            self._image.cleanup()

        # Move results to the output dir
        if not os.path.exists(self.outdir):
            os.makedirs(self.outdir)

        for fname in os.listdir(self.workdir):
            path = os.path.join(self.workdir, fname)
            if os.path.isfile(path):
                shutil.move(path, os.path.join(self.outdir, fname))

        # remove work directory
        shutil.rmtree(self.workdir, ignore_errors=True)

# Overhead of the MBR partitioning scheme (just one sector)
MBR_OVERHEAD = 1

# Overhead of the GPT partitioning scheme
GPT_OVERHEAD = 34

# Size of a sector in bytes
SECTOR_SIZE = 512

class PartitionedImage():
    """
    Partitioned image in a file.
    """

    def __init__(self, path, ptable_format, partitions, native_sysroot=None):
        self.path = path  # Path to the image file
        self.numpart = 0  # Number of allocated partitions
        self.realpart = 0 # Number of partitions in the partition table
        self.offset = 0   # Offset of next partition (in sectors)
        self.min_size = 0 # Minimum required disk size to fit
                          # all partitions (in bytes)
        self.ptable_format = ptable_format  # Partition table format
        # Disk system identifier
        self.identifier = random.SystemRandom().randint(1, 0xffffffff)

        self.partitions = partitions
        self.partimages = []
        # Size of a sector used in calculations
        self.sector_size = SECTOR_SIZE
        self.native_sysroot = native_sysroot

        # calculate the real partition number, accounting for partitions not
        # in the partition table and logical partitions
        realnum = 0
        for part in self.partitions:
            if part.no_table:
                part.realnum = 0
            else:
                realnum += 1
                if self.ptable_format == 'msdos' and realnum > 3 and len(partitions) > 4:
                    part.realnum = realnum + 1
                    continue
                part.realnum = realnum

        # generate parition and filesystem UUIDs
        for part in self.partitions:
            if not part.uuid and part.use_uuid:
                if self.ptable_format == 'gpt':
                    part.uuid = str(uuid.uuid4())
                else: # msdos partition table
                    part.uuid = '%08x-%02d' % (self.identifier, part.realnum)
            if not part.fsuuid:
                if part.fstype == 'vfat' or part.fstype == 'msdos':
                    part.fsuuid = '0x' + str(uuid.uuid4())[:8].upper()
                else:
                    part.fsuuid = str(uuid.uuid4())

    def prepare(self, imager):
        """Prepare an image. Call prepare method of all image partitions."""
        for part in self.partitions:
            # need to create the filesystems in order to get their
            # sizes before we can add them and do the layout.
            part.prepare(imager, imager.workdir, imager.oe_builddir,
                         imager.rootfs_dir, imager.bootimg_dir,
                         imager.kernel_dir, imager.native_sysroot)

            # Converting kB to sectors for parted
            part.size_sec = part.disk_size * 1024 // self.sector_size

    def layout_partitions(self):
        """ Layout the partitions, meaning calculate the position of every
        partition on the disk. The 'ptable_format' parameter defines the
        partition table format and may be "msdos". """

        logger.debug("Assigning %s partitions to disks", self.ptable_format)

        # The number of primary and logical partitions. Extended partition and
        # partitions not listed in the table are not included.
        num_real_partitions = len([p for p in self.partitions if not p.no_table])

        # Go through partitions in the order they are added in .ks file
        for num in range(len(self.partitions)):
            part = self.partitions[num]

            if self.ptable_format == 'msdos' and part.part_name:
                raise WicError("setting custom partition name is not " \
                               "implemented for msdos partitions")

            if self.ptable_format == 'msdos' and part.part_type:
                # The --part-type can also be implemented for MBR partitions,
                # in which case it would map to the 1-byte "partition type"
                # filed at offset 3 of the partition entry.
                raise WicError("setting custom partition type is not " \
                               "implemented for msdos partitions")

            # Get the disk where the partition is located
            self.numpart += 1
            if not part.no_table:
                self.realpart += 1

            if self.numpart == 1:
                if self.ptable_format == "msdos":
                    overhead = MBR_OVERHEAD
                elif self.ptable_format == "gpt":
                    overhead = GPT_OVERHEAD

                # Skip one sector required for the partitioning scheme overhead
                self.offset += overhead

            if self.realpart > 3 and num_real_partitions > 4:
                # Reserve a sector for EBR for every logical partition
                # before alignment is performed.
                if self.ptable_format == "msdos":
                    self.offset += 1

            if part.align:
                # If not first partition and we do have alignment set we need
                # to align the partition.
                # FIXME: This leaves a empty spaces to the disk. To fill the
                # gaps we could enlargea the previous partition?

                # Calc how much the alignment is off.
                align_sectors = self.offset % (part.align * 1024 // self.sector_size)

                if align_sectors:
                    # If partition is not aligned as required, we need
                    # to move forward to the next alignment point
                    align_sectors = (part.align * 1024 // self.sector_size) - align_sectors

                    logger.debug("Realignment for %s%s with %s sectors, original"
                                 " offset %s, target alignment is %sK.",
                                 part.disk, self.numpart, align_sectors,
                                 self.offset, part.align)

                    # increase the offset so we actually start the partition on right alignment
                    self.offset += align_sectors

            part.start = self.offset
            self.offset += part.size_sec

            part.type = 'primary'
            if not part.no_table:
                part.num = self.realpart
            else:
                part.num = 0

            if self.ptable_format == "msdos":
                # only count the partitions that are in partition table
                if num_real_partitions > 4:
                    if self.realpart > 3:
                        part.type = 'logical'
                        part.num = self.realpart + 1

            logger.debug("Assigned %s to %s%d, sectors range %d-%d size %d "
                         "sectors (%d bytes).", part.mountpoint, part.disk,
                         part.num, part.start, self.offset - 1, part.size_sec,
                         part.size_sec * self.sector_size)

        # Once all the partitions have been layed out, we can calculate the
        # minumim disk size
        self.min_size = self.offset
        if self.ptable_format == "gpt":
            self.min_size += GPT_OVERHEAD

        self.min_size *= self.sector_size

    def _create_partition(self, device, parttype, fstype, start, size):
        """ Create a partition on an image described by the 'device' object. """

        # Start is included to the size so we need to substract one from the end.
        end = start + size - 1
        logger.debug("Added '%s' partition, sectors %d-%d, size %d sectors",
                     parttype, start, end, size)

        cmd = "parted -s %s unit s mkpart %s" % (device, parttype)
        if fstype:
            cmd += " %s" % fstype
        cmd += " %d %d" % (start, end)

        return exec_native_cmd(cmd, self.native_sysroot)

    def create(self):
        logger.debug("Creating sparse file %s", self.path)
        with open(self.path, 'w') as sparse:
            os.ftruncate(sparse.fileno(), self.min_size)

        logger.debug("Initializing partition table for %s", self.path)
        exec_native_cmd("parted -s %s mklabel %s" %
                        (self.path, self.ptable_format), self.native_sysroot)

        logger.debug("Set disk identifier %x", self.identifier)
        with open(self.path, 'r+b') as img:
            img.seek(0x1B8)
            img.write(self.identifier.to_bytes(4, 'little'))

        logger.debug("Creating partitions")

        for part in self.partitions:
            if part.num == 0:
                continue

            if self.ptable_format == "msdos" and part.num == 5:
                # Create an extended partition (note: extended
                # partition is described in MBR and contains all
                # logical partitions). The logical partitions save a
                # sector for an EBR just before the start of a
                # partition. The extended partition must start one
                # sector before the start of the first logical
                # partition. This way the first EBR is inside of the
                # extended partition. Since the extended partitions
                # starts a sector before the first logical partition,
                # add a sector at the back, so that there is enough
                # room for all logical partitions.
                self._create_partition(self.path, "extended",
                                       None, part.start - 1,
                                       self.offset - part.start + 1)

            if part.fstype == "swap":
                parted_fs_type = "linux-swap"
            elif part.fstype == "vfat":
                parted_fs_type = "fat32"
            elif part.fstype == "msdos":
                parted_fs_type = "fat16"
                if not part.system_id:
                    part.system_id = '0x6' # FAT16
            else:
                # Type for ext2/ext3/ext4/btrfs
                parted_fs_type = "ext2"

            # Boot ROM of OMAP boards require vfat boot partition to have an
            # even number of sectors.
            if part.mountpoint == "/boot" and part.fstype in ["vfat", "msdos"] \
               and part.size_sec % 2:
                logger.debug("Subtracting one sector from '%s' partition to "
                             "get even number of sectors for the partition",
                             part.mountpoint)
                part.size_sec -= 1

            self._create_partition(self.path, part.type,
                                   parted_fs_type, part.start, part.size_sec)

            if part.part_name:
                logger.debug("partition %d: set name to %s",
                             part.num, part.part_name)
                exec_native_cmd("sgdisk --change-name=%d:%s %s" % \
                                         (part.num, part.part_name,
                                          self.path), self.native_sysroot)

            if part.part_type:
                logger.debug("partition %d: set type UID to %s",
                             part.num, part.part_type)
                exec_native_cmd("sgdisk --typecode=%d:%s %s" % \
                                         (part.num, part.part_type,
                                          self.path), self.native_sysroot)

            if part.uuid and self.ptable_format == "gpt":
                logger.debug("partition %d: set UUID to %s",
                             part.num, part.uuid)
                exec_native_cmd("sgdisk --partition-guid=%d:%s %s" % \
                                (part.num, part.uuid, self.path),
                                self.native_sysroot)

            if part.label and self.ptable_format == "gpt":
                logger.debug("partition %d: set name to %s",
                             part.num, part.label)
                exec_native_cmd("parted -s %s name %d %s" % \
                                (self.path, part.num, part.label),
                                self.native_sysroot)

            if part.active:
                flag_name = "legacy_boot" if self.ptable_format == 'gpt' else "boot"
                logger.debug("Set '%s' flag for partition '%s' on disk '%s'",
                             flag_name, part.num, self.path)
                exec_native_cmd("parted -s %s set %d %s on" % \
                                (self.path, part.num, flag_name),
                                self.native_sysroot)
            if part.system_id:
                exec_native_cmd("sfdisk --part-type %s %s %s" % \
                                (self.path, part.num, part.system_id),
                                self.native_sysroot)

    def cleanup(self):
        # remove partition images
        for image in set(self.partimages):
            os.remove(image)

    def assemble(self):
        logger.debug("Installing partitions")

        for part in self.partitions:
            source = part.source_file
            if source:
                # install source_file contents into a partition
                sparse_copy(source, self.path, seek=part.start * self.sector_size)

                logger.debug("Installed %s in partition %d, sectors %d-%d, "
                             "size %d sectors", source, part.num, part.start,
                             part.start + part.size_sec - 1, part.size_sec)

                partimage = self.path + '.p%d' % part.num
                os.rename(source, partimage)
                self.partimages.append(partimage)
