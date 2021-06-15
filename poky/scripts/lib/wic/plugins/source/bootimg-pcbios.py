#
# Copyright (c) 2014, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'bootimg-pcbios' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import re

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import (exec_cmd, exec_native_cmd,
                      get_bitbake_var, BOOTDD_EXTRA_SPACE)

logger = logging.getLogger('wic')

class BootimgPcbiosPlugin(SourcePlugin):
    """
    Create MBR boot partition and install syslinux on it.
    """

    name = 'bootimg-pcbios'

    @classmethod
    def _get_bootimg_dir(cls, bootimg_dir, dirname):
        """
        Check if dirname exists in default bootimg_dir or in STAGING_DIR.
        """
        staging_datadir = get_bitbake_var("STAGING_DATADIR")
        for result in (bootimg_dir, staging_datadir):
            if os.path.exists("%s/%s" % (result, dirname)):
                return result

        # STAGING_DATADIR is expanded with MLPREFIX if multilib is enabled
        # but dependency syslinux is still populated to original STAGING_DATADIR
        nonarch_datadir = re.sub('/[^/]*recipe-sysroot', '/recipe-sysroot', staging_datadir)
        if os.path.exists(os.path.join(nonarch_datadir, dirname)):
            return nonarch_datadir

        raise WicError("Couldn't find correct bootimg_dir, exiting")

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.  In this case, we install the MBR.
        """
        bootimg_dir = cls._get_bootimg_dir(bootimg_dir, 'syslinux')
        mbrfile = "%s/syslinux/" % bootimg_dir
        if creator.ptable_format == 'msdos':
            mbrfile += "mbr.bin"
        elif creator.ptable_format == 'gpt':
            mbrfile += "gptmbr.bin"
        else:
            raise WicError("Unsupported partition table: %s" %
                           creator.ptable_format)

        if not os.path.exists(mbrfile):
            raise WicError("Couldn't find %s.  If using the -e option, do you "
                           "have the right MACHINE set in local.conf?  If not, "
                           "is the bootimg_dir path correct?" % mbrfile)

        full_path = creator._full_path(workdir, disk_name, "direct")
        logger.debug("Installing MBR on disk %s as %s with size %s bytes",
                     disk_name, full_path, disk.min_size)

        dd_cmd = "dd if=%s of=%s conv=notrunc" % (mbrfile, full_path)
        exec_cmd(dd_cmd, native_sysroot)

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), creates syslinux config
        """
        hdddir = "%s/hdd/boot" % cr_workdir

        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)

        bootloader = creator.ks.bootloader

        custom_cfg = None
        if bootloader.configfile:
            custom_cfg = get_custom_config(bootloader.configfile)
            if custom_cfg:
                # Use a custom configuration for grub
                syslinux_conf = custom_cfg
                logger.debug("Using custom configuration file %s "
                             "for syslinux.cfg", bootloader.configfile)
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s." % bootloader.configfile)

        if not custom_cfg:
            # Create syslinux configuration using parameters from wks file
            splash = os.path.join(cr_workdir, "/hdd/boot/splash.jpg")
            if os.path.exists(splash):
                splashline = "menu background splash.jpg"
            else:
                splashline = ""

            syslinux_conf = ""
            syslinux_conf += "PROMPT 0\n"
            syslinux_conf += "TIMEOUT " + str(bootloader.timeout) + "\n"
            syslinux_conf += "\n"
            syslinux_conf += "ALLOWOPTIONS 1\n"
            syslinux_conf += "SERIAL 0 115200\n"
            syslinux_conf += "\n"
            if splashline:
                syslinux_conf += "%s\n" % splashline
            syslinux_conf += "DEFAULT boot\n"
            syslinux_conf += "LABEL boot\n"

            kernel = "/vmlinuz"
            syslinux_conf += "KERNEL " + kernel + "\n"

            syslinux_conf += "APPEND label=boot root=%s %s\n" % \
                             (creator.rootdev, bootloader.append)

        logger.debug("Writing syslinux config %s/hdd/boot/syslinux.cfg",
                     cr_workdir)
        cfg = open("%s/hdd/boot/syslinux.cfg" % cr_workdir, "w")
        cfg.write(syslinux_conf)
        cfg.close()

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, prepare content for legacy bios boot partition.
        """
        bootimg_dir = cls._get_bootimg_dir(bootimg_dir, 'syslinux')

        staging_kernel_dir = kernel_dir

        hdddir = "%s/hdd/boot" % cr_workdir

        kernel = get_bitbake_var("KERNEL_IMAGETYPE")
        if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
            if get_bitbake_var("INITRAMFS_IMAGE"):
                kernel = "%s-%s.bin" % \
                    (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

        cmds = ("install -m 0644 %s/%s %s/vmlinuz" %
                (staging_kernel_dir, kernel, hdddir),
                "install -m 444 %s/syslinux/ldlinux.sys %s/ldlinux.sys" %
                (bootimg_dir, hdddir),
                "install -m 0644 %s/syslinux/vesamenu.c32 %s/vesamenu.c32" %
                (bootimg_dir, hdddir),
                "install -m 444 %s/syslinux/libcom32.c32 %s/libcom32.c32" %
                (bootimg_dir, hdddir),
                "install -m 444 %s/syslinux/libutil.c32 %s/libutil.c32" %
                (bootimg_dir, hdddir))

        for install_cmd in cmds:
            exec_cmd(install_cmd)

        du_cmd = "du -bks %s" % hdddir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])

        extra_blocks = part.get_extra_block_count(blocks)

        if extra_blocks < BOOTDD_EXTRA_SPACE:
            extra_blocks = BOOTDD_EXTRA_SPACE

        blocks += extra_blocks

        logger.debug("Added %d extra blocks to %s to get to %d total blocks",
                     extra_blocks, part.mountpoint, blocks)

        # dosfs image, created by mkdosfs
        bootimg = "%s/boot%s.img" % (cr_workdir, part.lineno)

        dosfs_cmd = "mkdosfs -n boot -i %s -S 512 -C %s %d" % \
                    (part.fsuuid, bootimg, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        mcopy_cmd = "mcopy -i %s -s %s/* ::/" % (bootimg, hdddir)
        exec_native_cmd(mcopy_cmd, native_sysroot)

        syslinux_cmd = "syslinux %s" % bootimg
        exec_native_cmd(syslinux_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % bootimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % bootimg
        out = exec_cmd(du_cmd)
        bootimg_size = out.split()[0]

        part.size = int(bootimg_size)
        part.source_file = bootimg
