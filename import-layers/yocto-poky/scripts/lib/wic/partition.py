# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2013-2016 Intel Corporation.
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
# This module provides the OpenEmbedded partition object definitions.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
# Ed Bartosh <ed.bartosh> (at] linux.intel.com>

import os
import tempfile
import uuid

from wic.utils.oe.misc import msger, parse_sourceparams
from wic.utils.oe.misc import exec_cmd, exec_native_cmd
from wic.plugin import pluginmgr

partition_methods = {
    "do_stage_partition":None,
    "do_prepare_partition":None,
    "do_configure_partition":None,
}

class Partition(object):

    def __init__(self, args, lineno):
        self.args = args
        self.active = args.active
        self.align = args.align
        self.disk = args.disk
        self.extra_space = args.extra_space
        self.fsopts = args.fsopts
        self.fstype = args.fstype
        self.label = args.label
        self.mountpoint = args.mountpoint
        self.no_table = args.no_table
        self.overhead_factor = args.overhead_factor
        self.part_type = args.part_type
        self.rootfs_dir = args.rootfs_dir
        self.size = args.size
        self.source = args.source
        self.sourceparams = args.sourceparams
        self.use_uuid = args.use_uuid
        self.uuid = args.uuid
        if args.use_uuid and not self.uuid:
            self.uuid = str(uuid.uuid4())

        self.lineno = lineno
        self.source_file = ""
        self.sourceparams_dict = {}

    def get_extra_block_count(self, current_blocks):
        """
        The --size param is reflected in self.size (in kB), and we already
        have current_blocks (1k) blocks, calculate and return the
        number of (1k) blocks we need to add to get to --size, 0 if
        we're already there or beyond.
        """
        msger.debug("Requested partition size for %s: %d" % \
                    (self.mountpoint, self.size))

        if not self.size:
            return 0

        requested_blocks = self.size

        msger.debug("Requested blocks %d, current_blocks %d" % \
                    (requested_blocks, current_blocks))

        if requested_blocks > current_blocks:
            return requested_blocks - current_blocks
        else:
            return 0

    def prepare(self, creator, cr_workdir, oe_builddir, rootfs_dir,
                bootimg_dir, kernel_dir, native_sysroot):
        """
        Prepare content for individual partitions, depending on
        partition command parameters.
        """
        if self.sourceparams:
            self.sourceparams_dict = parse_sourceparams(self.sourceparams)

        if not self.source:
            if not self.size:
                msger.error("The %s partition has a size of zero.  Please "
                            "specify a non-zero --size for that partition." % \
                            self.mountpoint)
            if self.fstype and self.fstype == "swap":
                self.prepare_swap_partition(cr_workdir, oe_builddir,
                                            native_sysroot)
                self.source_file = "%s/fs.%s" % (cr_workdir, self.fstype)
            elif self.fstype:
                rootfs = "%s/fs_%s.%s.%s" % (cr_workdir, self.label,
                                             self.lineno, self.fstype)
                if os.path.isfile(rootfs):
                    os.remove(rootfs)
                for prefix in ("ext", "btrfs", "vfat", "squashfs"):
                    if self.fstype.startswith(prefix):
                        method = getattr(self,
                                         "prepare_empty_partition_" + prefix)
                        method(rootfs, oe_builddir, native_sysroot)
                        self.source_file = rootfs
                        break
            return

        plugins = pluginmgr.get_source_plugins()

        if self.source not in plugins:
            msger.error("The '%s' --source specified for %s doesn't exist.\n\t"
                        "See 'wic list source-plugins' for a list of available"
                        " --sources.\n\tSee 'wic help source-plugins' for "
                        "details on adding a new source plugin." % \
                        (self.source, self.mountpoint))

        self._source_methods = pluginmgr.get_source_plugin_methods(\
                                   self.source, partition_methods)
        self._source_methods["do_configure_partition"](self, self.sourceparams_dict,
                                                       creator, cr_workdir,
                                                       oe_builddir,
                                                       bootimg_dir,
                                                       kernel_dir,
                                                       native_sysroot)
        self._source_methods["do_stage_partition"](self, self.sourceparams_dict,
                                                   creator, cr_workdir,
                                                   oe_builddir,
                                                   bootimg_dir, kernel_dir,
                                                   native_sysroot)
        self._source_methods["do_prepare_partition"](self, self.sourceparams_dict,
                                                     creator, cr_workdir,
                                                     oe_builddir,
                                                     bootimg_dir, kernel_dir, rootfs_dir,
                                                     native_sysroot)

    def prepare_rootfs_from_fs_image(self, cr_workdir, oe_builddir,
                                     rootfs_dir):
        """
        Handle an already-created partition e.g. xxx.ext3
        """
        rootfs = oe_builddir
        du_cmd = "du -Lbks %s" % rootfs
        out = exec_cmd(du_cmd)
        rootfs_size = out.split()[0]

        self.size = rootfs_size
        self.source_file = rootfs

    def prepare_rootfs(self, cr_workdir, oe_builddir, rootfs_dir,
                       native_sysroot):
        """
        Prepare content for a rootfs partition i.e. create a partition
        and fill it from a /rootfs dir.

        Currently handles ext2/3/4, btrfs and vfat.
        """
        p_prefix = os.environ.get("PSEUDO_PREFIX", "%s/usr" % native_sysroot)
        p_localstatedir = os.environ.get("PSEUDO_LOCALSTATEDIR",
                                         "%s/../pseudo" % rootfs_dir)
        p_passwd = os.environ.get("PSEUDO_PASSWD", rootfs_dir)
        p_nosymlinkexp = os.environ.get("PSEUDO_NOSYMLINKEXP", "1")
        pseudo = "export PSEUDO_PREFIX=%s;" % p_prefix
        pseudo += "export PSEUDO_LOCALSTATEDIR=%s;" % p_localstatedir
        pseudo += "export PSEUDO_PASSWD=%s;" % p_passwd
        pseudo += "export PSEUDO_NOSYMLINKEXP=%s;" % p_nosymlinkexp
        pseudo += "%s/usr/bin/pseudo " % native_sysroot

        rootfs = "%s/rootfs_%s.%s.%s" % (cr_workdir, self.label,
                                         self.lineno, self.fstype)
        if os.path.isfile(rootfs):
            os.remove(rootfs)

        for prefix in ("ext", "btrfs", "vfat", "squashfs"):
            if self.fstype.startswith(prefix):
                method = getattr(self, "prepare_rootfs_" + prefix)
                method(rootfs, oe_builddir, rootfs_dir, native_sysroot, pseudo)

                self.source_file = rootfs

                # get the rootfs size in the right units for kickstart (kB)
                du_cmd = "du -Lbks %s" % rootfs
                out = exec_cmd(du_cmd)
                self.size = out.split()[0]

                break

    def prepare_rootfs_ext(self, rootfs, oe_builddir, rootfs_dir,
                           native_sysroot, pseudo):
        """
        Prepare content for an ext2/3/4 rootfs partition.
        """
        du_cmd = "du -ks %s" % rootfs_dir
        out = exec_cmd(du_cmd)
        actual_rootfs_size = int(out.split()[0])

        extra_blocks = self.get_extra_block_count(actual_rootfs_size)
        if extra_blocks < self.extra_space:
            extra_blocks = self.extra_space

        rootfs_size = actual_rootfs_size + extra_blocks
        rootfs_size *= self.overhead_factor

        msger.debug("Added %d extra blocks to %s to get to %d total blocks" % \
                    (extra_blocks, self.mountpoint, rootfs_size))

        dd_cmd = "dd if=/dev/zero of=%s bs=1024 seek=%d count=0 bs=1k" % \
            (rootfs, rootfs_size)
        exec_cmd(dd_cmd)

        extra_imagecmd = "-i 8192"

        label_str = ""
        if self.label:
            label_str = "-L %s" % self.label

        mkfs_cmd = "mkfs.%s -F %s %s %s -d %s" % \
            (self.fstype, extra_imagecmd, rootfs, label_str, rootfs_dir)
        exec_native_cmd(mkfs_cmd, native_sysroot, pseudo=pseudo)

    def prepare_rootfs_btrfs(self, rootfs, oe_builddir, rootfs_dir,
                             native_sysroot, pseudo):
        """
        Prepare content for a btrfs rootfs partition.

        Currently handles ext2/3/4 and btrfs.
        """
        du_cmd = "du -ks %s" % rootfs_dir
        out = exec_cmd(du_cmd)
        actual_rootfs_size = int(out.split()[0])

        extra_blocks = self.get_extra_block_count(actual_rootfs_size)
        if extra_blocks < self.extra_space:
            extra_blocks = self.extra_space

        rootfs_size = actual_rootfs_size + extra_blocks
        rootfs_size *= self.overhead_factor

        msger.debug("Added %d extra blocks to %s to get to %d total blocks" % \
                    (extra_blocks, self.mountpoint, rootfs_size))

        dd_cmd = "dd if=/dev/zero of=%s bs=1024 seek=%d count=0 bs=1k" % \
            (rootfs, rootfs_size)
        exec_cmd(dd_cmd)

        label_str = ""
        if self.label:
            label_str = "-L %s" % self.label

        mkfs_cmd = "mkfs.%s -b %d -r %s %s %s" % \
            (self.fstype, rootfs_size * 1024, rootfs_dir, label_str, rootfs)
        exec_native_cmd(mkfs_cmd, native_sysroot, pseudo=pseudo)

    def prepare_rootfs_vfat(self, rootfs, oe_builddir, rootfs_dir,
                            native_sysroot, pseudo):
        """
        Prepare content for a vfat rootfs partition.
        """
        du_cmd = "du -bks %s" % rootfs_dir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])

        extra_blocks = self.get_extra_block_count(blocks)
        if extra_blocks < self.extra_space:
            extra_blocks = self.extra_space

        blocks += extra_blocks

        msger.debug("Added %d extra blocks to %s to get to %d total blocks" % \
                    (extra_blocks, self.mountpoint, blocks))

        # Ensure total sectors is an integral number of sectors per
        # track or mcopy will complain. Sectors are 512 bytes, and we
        # generate images with 32 sectors per track. This calculation
        # is done in blocks, thus the mod by 16 instead of 32. Apply
        # sector count fix only when needed.
        if blocks % 16 != 0:
            blocks += (16 - (blocks % 16))

        label_str = "-n boot"
        if self.label:
            label_str = "-n %s" % self.label

        dosfs_cmd = "mkdosfs %s -S 512 -C %s %d" % (label_str, rootfs, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        mcopy_cmd = "mcopy -i %s -s %s/* ::/" % (rootfs, rootfs_dir)
        exec_native_cmd(mcopy_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % rootfs
        exec_cmd(chmod_cmd)

    def prepare_rootfs_squashfs(self, rootfs, oe_builddir, rootfs_dir,
                                native_sysroot, pseudo):
        """
        Prepare content for a squashfs rootfs partition.
        """
        squashfs_cmd = "mksquashfs %s %s -noappend" % \
                       (rootfs_dir, rootfs)
        exec_native_cmd(squashfs_cmd, native_sysroot, pseudo=pseudo)

    def prepare_empty_partition_ext(self, rootfs, oe_builddir,
                                    native_sysroot):
        """
        Prepare an empty ext2/3/4 partition.
        """
        dd_cmd = "dd if=/dev/zero of=%s bs=1k seek=%d count=0" % \
            (rootfs, self.size)
        exec_cmd(dd_cmd)

        extra_imagecmd = "-i 8192"

        label_str = ""
        if self.label:
            label_str = "-L %s" % self.label

        mkfs_cmd = "mkfs.%s -F %s %s %s" % \
            (self.fstype, extra_imagecmd, label_str, rootfs)
        exec_native_cmd(mkfs_cmd, native_sysroot)

    def prepare_empty_partition_btrfs(self, rootfs, oe_builddir,
                                      native_sysroot):
        """
        Prepare an empty btrfs partition.
        """
        dd_cmd = "dd if=/dev/zero of=%s bs=1k seek=%d count=0" % \
            (rootfs, self.size)
        exec_cmd(dd_cmd)

        label_str = ""
        if self.label:
            label_str = "-L %s" % self.label

        mkfs_cmd = "mkfs.%s -b %d %s %s" % \
            (self.fstype, self.size * 1024, label_str, rootfs)
        exec_native_cmd(mkfs_cmd, native_sysroot)

    def prepare_empty_partition_vfat(self, rootfs, oe_builddir,
                                     native_sysroot):
        """
        Prepare an empty vfat partition.
        """
        blocks = self.size

        label_str = "-n boot"
        if self.label:
            label_str = "-n %s" % self.label

        dosfs_cmd = "mkdosfs %s -S 512 -C %s %d" % (label_str, rootfs, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % rootfs
        exec_cmd(chmod_cmd)

    def prepare_empty_partition_squashfs(self, cr_workdir, oe_builddir,
                                         native_sysroot):
        """
        Prepare an empty squashfs partition.
        """
        msger.warning("Creating of an empty squashfs %s partition was attempted. " \
                      "Proceeding as requested." % self.mountpoint)

        path = "%s/fs_%s.%s" % (cr_workdir, self.label, self.fstype)
        os.path.isfile(path) and os.remove(path)

        # it is not possible to create a squashfs without source data,
        # thus prepare an empty temp dir that is used as source
        tmpdir = tempfile.mkdtemp()

        squashfs_cmd = "mksquashfs %s %s -noappend" % \
                       (tmpdir, path)
        exec_native_cmd(squashfs_cmd, native_sysroot)

        os.rmdir(tmpdir)

        # get the rootfs size in the right units for kickstart (kB)
        du_cmd = "du -Lbks %s" % path
        out = exec_cmd(du_cmd)
        fs_size = out.split()[0]

        self.size = fs_size

    def prepare_swap_partition(self, cr_workdir, oe_builddir, native_sysroot):
        """
        Prepare a swap partition.
        """
        path = "%s/fs.%s" % (cr_workdir, self.fstype)

        dd_cmd = "dd if=/dev/zero of=%s bs=1k seek=%d count=0" % \
            (path, self.size)
        exec_cmd(dd_cmd)

        import uuid
        label_str = ""
        if self.label:
            label_str = "-L %s" % self.label
        mkswap_cmd = "mkswap %s -U %s %s" % (label_str, str(uuid.uuid1()), path)
        exec_native_cmd(mkswap_cmd, native_sysroot)

