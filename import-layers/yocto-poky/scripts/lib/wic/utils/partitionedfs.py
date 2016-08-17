#!/usr/bin/env python -tt
#
# Copyright (c) 2009, 2010, 2011 Intel, Inc.
# Copyright (c) 2007, 2008 Red Hat, Inc.
# Copyright (c) 2008 Daniel P. Berrange
# Copyright (c) 2008 David P. Huff
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
from wic import msger
from wic.utils.errors import ImageError
from wic.utils.oe.misc import exec_cmd, exec_native_cmd

# Overhead of the MBR partitioning scheme (just one sector)
MBR_OVERHEAD = 1

# Overhead of the GPT partitioning scheme
GPT_OVERHEAD = 34

# Size of a sector in bytes
SECTOR_SIZE = 512

class Image(object):
    """
    Generic base object for an image.

    An Image is a container for a set of DiskImages and associated
    partitions.
    """
    def __init__(self, native_sysroot=None):
        self.disks = {}
        self.partitions = []
        # Size of a sector used in calculations
        self.sector_size = SECTOR_SIZE
        self._partitions_layed_out = False
        self.native_sysroot = native_sysroot

    def __add_disk(self, disk_name):
        """ Add a disk 'disk_name' to the internal list of disks. Note,
        'disk_name' is the name of the disk in the target system
        (e.g., sdb). """

        if disk_name in self.disks:
            # We already have this disk
            return

        assert not self._partitions_layed_out

        self.disks[disk_name] = \
                {'disk': None,     # Disk object
                 'numpart': 0,     # Number of allocate partitions
                 'realpart': 0,    # Number of partitions in the partition table
                 'partitions': [], # Indexes to self.partitions
                 'offset': 0,      # Offset of next partition (in sectors)
                 # Minimum required disk size to fit all partitions (in bytes)
                 'min_size': 0,
                 'ptable_format': "msdos"} # Partition table format

    def add_disk(self, disk_name, disk_obj):
        """ Add a disk object which have to be partitioned. More than one disk
        can be added. In case of multiple disks, disk partitions have to be
        added for each disk separately with 'add_partition()". """

        self.__add_disk(disk_name)
        self.disks[disk_name]['disk'] = disk_obj

    def __add_partition(self, part):
        """ This is a helper function for 'add_partition()' which adds a
        partition to the internal list of partitions. """

        assert not self._partitions_layed_out

        self.partitions.append(part)
        self.__add_disk(part['disk_name'])

    def add_partition(self, size, disk_name, mountpoint, source_file=None, fstype=None,
                      label=None, fsopts=None, boot=False, align=None, no_table=False,
                      part_type=None, uuid=None):
        """ Add the next partition. Prtitions have to be added in the
        first-to-last order. """

        ks_pnum = len(self.partitions)

        # Converting kB to sectors for parted
        size = size * 1024 / self.sector_size

        part = {'ks_pnum': ks_pnum, # Partition number in the KS file
                'size': size, # In sectors
                'mountpoint': mountpoint, # Mount relative to chroot
                'source_file': source_file, # partition contents
                'fstype': fstype, # Filesystem type
                'fsopts': fsopts, # Filesystem mount options
                'label': label, # Partition label
                'disk_name': disk_name, # physical disk name holding partition
                'device': None, # kpartx device node for partition
                'num': None, # Partition number
                'boot': boot, # Bootable flag
                'align': align, # Partition alignment
                'no_table' : no_table, # Partition does not appear in partition table
                'part_type' : part_type, # Partition type
                'uuid': uuid} # Partition UUID

        self.__add_partition(part)

    def layout_partitions(self, ptable_format="msdos"):
        """ Layout the partitions, meaning calculate the position of every
        partition on the disk. The 'ptable_format' parameter defines the
        partition table format and may be "msdos". """

        msger.debug("Assigning %s partitions to disks" % ptable_format)

        if self._partitions_layed_out:
            return

        self._partitions_layed_out = True

        # Go through partitions in the order they are added in .ks file
        for num in range(len(self.partitions)):
            part = self.partitions[num]

            if not self.disks.has_key(part['disk_name']):
                raise ImageError("No disk %s for partition %s" \
                                 % (part['disk_name'], part['mountpoint']))

            if ptable_format == 'msdos' and part['part_type']:
                # The --part-type can also be implemented for MBR partitions,
                # in which case it would map to the 1-byte "partition type"
                # filed at offset 3 of the partition entry.
                raise ImageError("setting custom partition type is not " \
                                 "implemented for msdos partitions")

            # Get the disk where the partition is located
            disk = self.disks[part['disk_name']]
            disk['numpart'] += 1
            if not part['no_table']:
                disk['realpart'] += 1
            disk['ptable_format'] = ptable_format

            if disk['numpart'] == 1:
                if ptable_format == "msdos":
                    overhead = MBR_OVERHEAD
                elif ptable_format == "gpt":
                    overhead = GPT_OVERHEAD

                # Skip one sector required for the partitioning scheme overhead
                disk['offset'] += overhead

            if disk['realpart'] > 3:
                # Reserve a sector for EBR for every logical partition
                # before alignment is performed.
                if ptable_format == "msdos":
                    disk['offset'] += 1


            if part['align']:
                # If not first partition and we do have alignment set we need
                # to align the partition.
                # FIXME: This leaves a empty spaces to the disk. To fill the
                # gaps we could enlargea the previous partition?

                # Calc how much the alignment is off.
                align_sectors = disk['offset'] % (part['align'] * 1024 / self.sector_size)

                if align_sectors:
                    # If partition is not aligned as required, we need
                    # to move forward to the next alignment point
                    align_sectors = (part['align'] * 1024 / self.sector_size) - align_sectors

                    msger.debug("Realignment for %s%s with %s sectors, original"
                                " offset %s, target alignment is %sK." %
                                (part['disk_name'], disk['numpart'], align_sectors,
                                 disk['offset'], part['align']))

                    # increase the offset so we actually start the partition on right alignment
                    disk['offset'] += align_sectors

            part['start'] = disk['offset']
            disk['offset'] += part['size']

            part['type'] = 'primary'
            if not part['no_table']:
                part['num'] = disk['realpart']
            else:
                part['num'] = 0

            if disk['ptable_format'] == "msdos":
                if disk['realpart'] > 3:
                    part['type'] = 'logical'
                    part['num'] = disk['realpart'] + 1

            disk['partitions'].append(num)
            msger.debug("Assigned %s to %s%d, sectors range %d-%d size %d "
                        "sectors (%d bytes)." \
                            % (part['mountpoint'], part['disk_name'], part['num'],
                               part['start'], part['start'] + part['size'] - 1,
                               part['size'], part['size'] * self.sector_size))

        # Once all the partitions have been layed out, we can calculate the
        # minumim disk sizes.
        for disk in self.disks.values():
            disk['min_size'] = disk['offset']
            if disk['ptable_format'] == "gpt":
                disk['min_size'] += GPT_OVERHEAD

            disk['min_size'] *= self.sector_size

    def __create_partition(self, device, parttype, fstype, start, size):
        """ Create a partition on an image described by the 'device' object. """

        # Start is included to the size so we need to substract one from the end.
        end = start + size - 1
        msger.debug("Added '%s' partition, sectors %d-%d, size %d sectors" %
                    (parttype, start, end, size))

        cmd = "parted -s %s unit s mkpart %s" % (device, parttype)
        if fstype:
            cmd += " %s" % fstype
        cmd += " %d %d" % (start, end)

        return exec_native_cmd(cmd, self.native_sysroot)

    def __format_disks(self):
        self.layout_partitions()

        for dev in self.disks.keys():
            disk = self.disks[dev]
            msger.debug("Initializing partition table for %s" % \
                        (disk['disk'].device))
            exec_native_cmd("parted -s %s mklabel %s" % \
                            (disk['disk'].device, disk['ptable_format']),
                            self.native_sysroot)

        msger.debug("Creating partitions")

        for part in self.partitions:
            if part['num'] == 0:
                continue

            disk = self.disks[part['disk_name']]
            if disk['ptable_format'] == "msdos" and part['num'] == 5:
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
                self.__create_partition(disk['disk'].device, "extended",
                                        None, part['start'] - 1,
                                        disk['offset'] - part['start'] + 1)

            if part['fstype'] == "swap":
                parted_fs_type = "linux-swap"
            elif part['fstype'] == "vfat":
                parted_fs_type = "fat32"
            elif part['fstype'] == "msdos":
                parted_fs_type = "fat16"
            elif part['fstype'] == "ontrackdm6aux3":
                parted_fs_type = "ontrackdm6aux3"
            else:
                # Type for ext2/ext3/ext4/btrfs
                parted_fs_type = "ext2"

            # Boot ROM of OMAP boards require vfat boot partition to have an
            # even number of sectors.
            if part['mountpoint'] == "/boot" and part['fstype'] in ["vfat", "msdos"] \
               and part['size'] % 2:
                msger.debug("Substracting one sector from '%s' partition to " \
                            "get even number of sectors for the partition" % \
                            part['mountpoint'])
                part['size'] -= 1

            self.__create_partition(disk['disk'].device, part['type'],
                                    parted_fs_type, part['start'], part['size'])

            if part['part_type']:
                msger.debug("partition %d: set type UID to %s" % \
                            (part['num'], part['part_type']))
                exec_native_cmd("sgdisk --typecode=%d:%s %s" % \
                                         (part['num'], part['part_type'],
                                          disk['disk'].device), self.native_sysroot)

            if part['uuid']:
                msger.debug("partition %d: set UUID to %s" % \
                            (part['num'], part['uuid']))
                exec_native_cmd("sgdisk --partition-guid=%d:%s %s" % \
                                (part['num'], part['uuid'], disk['disk'].device),
                                self.native_sysroot)

            if part['boot']:
                flag_name = "legacy_boot" if disk['ptable_format'] == 'gpt' else "boot"
                msger.debug("Set '%s' flag for partition '%s' on disk '%s'" % \
                            (flag_name, part['num'], disk['disk'].device))
                exec_native_cmd("parted -s %s set %d %s on" % \
                                (disk['disk'].device, part['num'], flag_name),
                                self.native_sysroot)

            # Parted defaults to enabling the lba flag for fat16 partitions,
            # which causes compatibility issues with some firmware (and really
            # isn't necessary).
            if parted_fs_type == "fat16":
                if disk['ptable_format'] == 'msdos':
                    msger.debug("Disable 'lba' flag for partition '%s' on disk '%s'" % \
                                (part['num'], disk['disk'].device))
                    exec_native_cmd("parted -s %s set %d lba off" % \
                                    (disk['disk'].device, part['num']),
                                    self.native_sysroot)

    def cleanup(self):
        if self.disks:
            for dev in self.disks:
                disk = self.disks[dev]
                try:
                    disk['disk'].cleanup()
                except:
                    pass

    def assemble(self, image_file):
        msger.debug("Installing partitions")

        for part in self.partitions:
            source = part['source_file']
            if source:
                # install source_file contents into a partition
                cmd = "dd if=%s of=%s bs=%d seek=%d count=%d conv=notrunc" % \
                      (source, image_file, self.sector_size,
                       part['start'], part['size'])
                exec_cmd(cmd)

                msger.debug("Installed %s in partition %d, sectors %d-%d, "
                            "size %d sectors" % \
                            (source, part['num'], part['start'],
                             part['start'] + part['size'] - 1, part['size']))

                os.rename(source, image_file + '.p%d' % part['num'])

    def create(self):
        for dev in self.disks.keys():
            disk = self.disks[dev]
            disk['disk'].create()

        self.__format_disks()

        return
