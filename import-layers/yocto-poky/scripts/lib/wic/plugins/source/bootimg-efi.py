# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2014, Intel Corporation.
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
# This implements the 'bootimg-efi' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import shutil

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import (exec_cmd, exec_native_cmd,
                      get_bitbake_var, BOOTDD_EXTRA_SPACE)

logger = logging.getLogger('wic')

class BootimgEFIPlugin(SourcePlugin):
    """
    Create EFI boot partition.
    This plugin supports GRUB 2 and systemd-boot bootloaders.
    """

    name = 'bootimg-efi'

    @classmethod
    def do_configure_grubefi(cls, creator, cr_workdir):
        """
        Create loader-specific (grub-efi) config
        """
        configfile = creator.ks.bootloader.configfile
        custom_cfg = None
        if configfile:
            custom_cfg = get_custom_config(configfile)
            if custom_cfg:
                # Use a custom configuration for grub
                grubefi_conf = custom_cfg
                logger.debug("Using custom configuration file "
                             "%s for grub.cfg", configfile)
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s." % configfile)

        if not custom_cfg:
            # Create grub configuration using parameters from wks file
            bootloader = creator.ks.bootloader

            grubefi_conf = ""
            grubefi_conf += "serial --unit=0 --speed=115200 --word=8 --parity=no --stop=1\n"
            grubefi_conf += "default=boot\n"
            grubefi_conf += "timeout=%s\n" % bootloader.timeout
            grubefi_conf += "menuentry 'boot'{\n"

            kernel = "/bzImage"

            grubefi_conf += "linux %s root=%s rootwait %s\n" \
                % (kernel, creator.rootdev, bootloader.append)
            grubefi_conf += "}\n"

        logger.debug("Writing grubefi config %s/hdd/boot/EFI/BOOT/grub.cfg",
                     cr_workdir)
        cfg = open("%s/hdd/boot/EFI/BOOT/grub.cfg" % cr_workdir, "w")
        cfg.write(grubefi_conf)
        cfg.close()

    @classmethod
    def do_configure_systemdboot(cls, hdddir, creator, cr_workdir, source_params):
        """
        Create loader-specific systemd-boot/gummiboot config
        """
        install_cmd = "install -d %s/loader" % hdddir
        exec_cmd(install_cmd)

        install_cmd = "install -d %s/loader/entries" % hdddir
        exec_cmd(install_cmd)

        bootloader = creator.ks.bootloader

        loader_conf = ""
        loader_conf += "default boot\n"
        loader_conf += "timeout %d\n" % bootloader.timeout

        initrd = source_params.get('initrd')

        if initrd:
            # obviously we need to have a common common deploy var
            bootimg_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not bootimg_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

            cp_cmd = "cp %s/%s %s" % (bootimg_dir, initrd, hdddir)
            exec_cmd(cp_cmd, True)
        else:
            logger.debug("Ignoring missing initrd")

        logger.debug("Writing systemd-boot config "
                     "%s/hdd/boot/loader/loader.conf", cr_workdir)
        cfg = open("%s/hdd/boot/loader/loader.conf" % cr_workdir, "w")
        cfg.write(loader_conf)
        cfg.close()

        configfile = creator.ks.bootloader.configfile
        custom_cfg = None
        if configfile:
            custom_cfg = get_custom_config(configfile)
            if custom_cfg:
                # Use a custom configuration for systemd-boot
                boot_conf = custom_cfg
                logger.debug("Using custom configuration file "
                             "%s for systemd-boots's boot.conf", configfile)
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s.", configfile)

        if not custom_cfg:
            # Create systemd-boot configuration using parameters from wks file
            kernel = "/bzImage"

            boot_conf = ""
            boot_conf += "title boot\n"
            boot_conf += "linux %s\n" % kernel
            boot_conf += "options LABEL=Boot root=%s %s\n" % \
                             (creator.rootdev, bootloader.append)

            if initrd:
                boot_conf += "initrd /%s\n" % initrd

        logger.debug("Writing systemd-boot config "
                     "%s/hdd/boot/loader/entries/boot.conf", cr_workdir)
        cfg = open("%s/hdd/boot/loader/entries/boot.conf" % cr_workdir, "w")
        cfg.write(boot_conf)
        cfg.close()


    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), creates loader-specific config
        """
        hdddir = "%s/hdd/boot" % cr_workdir

        install_cmd = "install -d %s/EFI/BOOT" % hdddir
        exec_cmd(install_cmd)

        try:
            if source_params['loader'] == 'grub-efi':
                cls.do_configure_grubefi(creator, cr_workdir)
            elif source_params['loader'] == 'systemd-boot':
                cls.do_configure_systemdboot(hdddir, creator, cr_workdir, source_params)
            else:
                raise WicError("unrecognized bootimg-efi loader: %s" % source_params['loader'])
        except KeyError:
            raise WicError("bootimg-efi requires a loader, none specified")


    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, prepare content for an EFI (grub) boot partition.
        """
        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        staging_kernel_dir = kernel_dir

        hdddir = "%s/hdd/boot" % cr_workdir

        install_cmd = "install -m 0644 %s/bzImage %s/bzImage" % \
            (staging_kernel_dir, hdddir)
        exec_cmd(install_cmd)


        try:
            if source_params['loader'] == 'grub-efi':
                shutil.copyfile("%s/hdd/boot/EFI/BOOT/grub.cfg" % cr_workdir,
                                "%s/grub.cfg" % cr_workdir)
                for mod in [x for x in os.listdir(kernel_dir) if x.startswith("grub-efi-")]:
                    cp_cmd = "cp %s/%s %s/EFI/BOOT/%s" % (kernel_dir, mod, hdddir, mod[9:])
                    exec_cmd(cp_cmd, True)
                shutil.move("%s/grub.cfg" % cr_workdir,
                            "%s/hdd/boot/EFI/BOOT/grub.cfg" % cr_workdir)
            elif source_params['loader'] == 'systemd-boot':
                for mod in [x for x in os.listdir(kernel_dir) if x.startswith("systemd-")]:
                    cp_cmd = "cp %s/%s %s/EFI/BOOT/%s" % (kernel_dir, mod, hdddir, mod[8:])
                    exec_cmd(cp_cmd, True)
            else:
                raise WicError("unrecognized bootimg-efi loader: %s" %
                               source_params['loader'])
        except KeyError:
            raise WicError("bootimg-efi requires a loader, none specified")

        startup = os.path.join(kernel_dir, "startup.nsh")
        if os.path.exists(startup):
            cp_cmd = "cp %s %s/" % (startup, hdddir)
            exec_cmd(cp_cmd, True)

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
        bootimg = "%s/boot.img" % cr_workdir

        dosfs_cmd = "mkdosfs -n efi -C %s %d" % (bootimg, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        mcopy_cmd = "mcopy -i %s -s %s/* ::/" % (bootimg, hdddir)
        exec_native_cmd(mcopy_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % bootimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % bootimg
        out = exec_cmd(du_cmd)
        bootimg_size = out.split()[0]

        part.size = int(bootimg_size)
        part.source_file = bootimg
