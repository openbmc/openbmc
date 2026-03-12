#
# Copyright (c) 2014, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'bootimg_pcbios' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import re
import shutil

from glob import glob
from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import (exec_cmd, exec_native_cmd,
                      get_bitbake_var, BOOTDD_EXTRA_SPACE)

logger = logging.getLogger('wic')

class BootimgPcbiosPlugin(SourcePlugin):
    """
    Creates boot partition that is legacy BIOS firmare bootable with
    MBR/MSDOS as partition table format. Plugin will install caller
    selected bootloader directly to resulting wic image.

    Supported Bootloaders:
        * syslinux (default)
        * grub

    ****************** Wic Plugin Depends/Vars ******************
    WKS_FILE_DEPENDS = "grub-native grub"
    WKS_FILE_DEPENDS = "syslinux-native syslinux"

    # Optional variables
    # GRUB_MKIMAGE_FORMAT_PC - Used to define target platform.
    # GRUB_PREFIX_PATH       - Used to define which directory
    #                          grub config and modules are going
    #                          to reside in.
    GRUB_PREFIX_PATH = '/boot/grub2'    # Default: /boot/grub
    GRUB_MKIMAGE_FORMAT_PC = 'i386-pc'  # Default: i386-pc

    WICVARS:append = "\
        GRUB_PREFIX_PATH \
        GRUB_MKIMAGE_FORMAT_PC \
        "
    ****************** Wic Plugin Depends/Vars ******************


    **************** Example kickstart Legacy Bios Grub Boot ****************
    part boot --label bios_boot --fstype ext4 --offset 1024 --fixed-size 78M
        --source bootimg_pcbios --sourceparams="loader-bios=grub" --active

    part roots --label rootfs --fstype ext4 --source rootfs --use-uuid
    bootloader --ptable msdos --source bootimg_pcbios
    **************** Example kickstart Legacy Bios Grub Boot ****************


    *************** Example kickstart Legacy Bios Syslinux Boot ****************
    part /boot --source bootimg_pcbios --sourceparams="loader-bios=syslinux"
           --ondisk sda --label boot --fstype vfat --align 1024 --active

    part roots --label rootfs --fstype ext4 --source rootfs --use-uuid
    bootloader --ptable msdos --source bootimg_pcbios
    """

    name = 'bootimg_pcbios'

    # Variable required for do_install_disk
    loader = ''

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
        full_path = creator._full_path(workdir, disk_name, "direct")
        logger.debug("Installing MBR on disk %s as %s with size %s bytes",
                     disk_name, full_path, disk.min_size)

        if cls.loader == 'grub':
            cls._do_install_grub(creator, kernel_dir,
                            native_sysroot, full_path)
        elif cls.loader == 'syslinux':
            cls._do_install_syslinux(creator, bootimg_dir,
                            native_sysroot, full_path)
        else:
            raise WicError("boot loader some how not specified check do_prepare_partition")

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        try:
            if source_params['loader-bios'] == 'grub':
                cls._do_configure_grub(part, creator, cr_workdir)
            elif source_params['loader-bios'] == 'syslinux':
                cls._do_configure_syslinux(part, creator, cr_workdir)
            else:
                raise WicError("unrecognized bootimg_pcbios loader: %s" % source_params['loader-bios'])
        except KeyError:
            cls._do_configure_syslinux(part, creator, cr_workdir)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        try:
            if source_params['loader-bios'] == 'grub':
                cls._do_prepare_grub(part, cr_workdir, oe_builddir,
                                kernel_dir, rootfs_dir, native_sysroot)
            elif source_params['loader-bios'] == 'syslinux':
                cls._do_prepare_syslinux(part, cr_workdir, bootimg_dir,
                                    kernel_dir, native_sysroot)
            else:
                raise WicError("unrecognized bootimg_pcbios loader: %s" % source_params['loader-bios'])

            # Required by do_install_disk
            cls.loader = source_params['loader-bios']
        except KeyError:
            # Required by do_install_disk
            cls.loader = 'syslinux'
            cls._do_prepare_syslinux(part, cr_workdir, bootimg_dir,
                                kernel_dir, native_sysroot)

    @classmethod
    def _get_staging_libdir(cls):
        """
        For unknown reasons when running test with poky
        STAGING_LIBDIR gets unset when wic create is executed.
        Bellow is a hack to determine what STAGING_LIBDIR should
        be if not specified.
        """

        staging_libdir = get_bitbake_var('STAGING_LIBDIR')
        staging_dir_target = get_bitbake_var('STAGING_DIR_TARGET')

        if not staging_libdir:
            staging_libdir = '%s/usr/lib64' % staging_dir_target
            if not os.path.isdir(staging_libdir):
                staging_libdir = '%s/usr/lib32' % staging_dir_target
                if not os.path.isdir(staging_libdir):
                    staging_libdir = '%s/usr/lib' % staging_dir_target

        return staging_libdir

    @classmethod
    def _get_bootloader_config(cls, bootloader, loader):
        custom_cfg = None

        if bootloader.configfile:
            custom_cfg = get_custom_config(bootloader.configfile)
            if custom_cfg:
                logger.debug("Using custom configuration file %s "
                             "for %s.cfg", bootloader.configfile,
                             loader)
                return custom_cfg
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s." % bootloader.configfile)
        return custom_cfg

    @classmethod
    def _do_configure_syslinux(cls, part, creator, cr_workdir):
        """
        Called before do_prepare_partition(), creates syslinux config
        """

        hdddir = "%s/hdd/boot" % cr_workdir

        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)

        bootloader = creator.ks.bootloader
        syslinux_conf = cls._get_bootloader_config(bootloader, 'syslinux')

        if not syslinux_conf:
            # Create syslinux configuration using parameters from wks file
            splash = os.path.join(hdddir, "/splash.jpg")
            if os.path.exists(splash):
                splashline = "menu background splash.jpg"
            else:
                splashline = ""

            # Set a default timeout if none specified to avoid
            # 'None' being the value placed within the configuration
            # file.
            if not bootloader.timeout:
                bootloader.timeout = 500

            # Set a default kernel params string if none specified
            # to avoid 'None' being the value placed within the
            # configuration file.
            if not bootloader.append:
                bootloader.append = "rootwait console=ttyS0,115200 console=tty0"

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

            kernel = "/" + get_bitbake_var("KERNEL_IMAGETYPE")
            syslinux_conf += "KERNEL " + kernel + "\n"

            syslinux_conf += "APPEND label=boot root=%s %s\n" % \
                             (creator.rootdev, bootloader.append)

        logger.debug("Writing syslinux config %s/syslinux.cfg", hdddir)
        cfg = open("%s/hdd/boot/syslinux.cfg" % cr_workdir, "w")
        cfg.write(syslinux_conf)
        cfg.close()

    @classmethod
    def _do_prepare_syslinux(cls, part, cr_workdir, bootimg_dir,
                             kernel_dir, native_sysroot):
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

        cmds = ("install -m 0644 %s/%s %s/%s" %
                (staging_kernel_dir, kernel, hdddir, get_bitbake_var("KERNEL_IMAGETYPE")),
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

        du_cmd = "du --apparent-size -ks %s" % hdddir
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

        label = part.label if part.label else "boot"

        dosfs_cmd = "mkdosfs -n %s -i %s -S 512 -C %s %d" % \
                    (label, part.fsuuid, bootimg, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        mcopy_cmd = "mcopy -i %s -s %s/* ::/" % (bootimg, hdddir)
        exec_native_cmd(mcopy_cmd, native_sysroot)

        syslinux_cmd = "syslinux %s" % bootimg
        exec_native_cmd(syslinux_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % bootimg
        exec_cmd(chmod_cmd)

        du_cmd = "du --apparent-size -Lks %s" % bootimg
        out = exec_cmd(du_cmd)
        bootimg_size = out.split()[0]

        part.size = int(bootimg_size)
        part.source_file = bootimg

    @classmethod
    def _do_install_syslinux(cls, creator, bootimg_dir,
                             native_sysroot, full_path):
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

        dd_cmd = "dd if=%s of=%s conv=notrunc" % (mbrfile, full_path)
        exec_cmd(dd_cmd, native_sysroot)

    @classmethod
    def _do_configure_grub(cls, part, creator, cr_workdir):
        hdddir = "%s/hdd" % cr_workdir
        bootloader = creator.ks.bootloader

        grub_conf = cls._get_bootloader_config(bootloader, 'grub')

        grub_prefix_path = get_bitbake_var('GRUB_PREFIX_PATH')
        if not grub_prefix_path:
            grub_prefix_path = '/boot/grub'

        grub_path = "%s/%s" %(hdddir, grub_prefix_path)
        install_cmd = "install -d %s" % grub_path
        exec_cmd(install_cmd)

        if not grub_conf:
            # Set a default timeout if none specified to avoid
            # 'None' being the value placed within the configuration
            # file.
            if not bootloader.timeout:
                bootloader.timeout = 500

            # Set a default kernel params string if none specified
            # to avoid 'None' being the value placed within the
            # configuration file.
            if not bootloader.append:
                bootloader.append = "rootwait rootfstype=%s " % (part.fstype)
                bootloader.append += "console=ttyS0,115200 console=tty0"

            kernel = "/boot/" + get_bitbake_var("KERNEL_IMAGETYPE")

            grub_conf = 'serial --unit=0 --speed=115200 --word=8 --parity=no --stop=1\n'
            grub_conf += 'set gfxmode=auto\n'
            grub_conf += 'set gfxpayload=keep\n\n'
            grub_conf += 'set default=0\n\n'
            grub_conf += '# Boot automatically after %d secs.\n' % (bootloader.timeout)
            grub_conf += 'set timeout=%d\n\n' % (bootloader.timeout)
            grub_conf += 'menuentry \'default\' {\n'
            grub_conf += '\tsearch --no-floppy --set=root --file %s\n' % (kernel)
            grub_conf += '\tprobe --set partuuid --part-uuid ($root)\n'
            grub_conf += '\tlinux %s root=PARTUUID=$partuuid %s\n}\n' % \
                            (kernel, bootloader.append)

        logger.debug("Writing grub config %s/grub.cfg", grub_path)
        cfg = open("%s/grub.cfg" % grub_path, "w")
        cfg.write(grub_conf)
        cfg.close()

    @classmethod
    def _do_prepare_grub(cls, part, cr_workdir, oe_builddir,
                         kernel_dir, rootfs_dir, native_sysroot):
        """
        1. Generate embed.cfg that'll later be embedded into core.img.
           So, that core.img knows where to search for grub.cfg.
        2. Generate core.img or grub stage 1.5.
        3. Copy modules into partition.
        4. Create partition rootfs file.
        """

        hdddir = "%s/hdd" % cr_workdir

        copy_types = [ '*.mod', '*.o', '*.lst' ]

        builtin_modules = 'boot linux ext2 fat serial part_msdos part_gpt \
        normal multiboot probe biosdisk msdospart configfile search loadenv test'

        staging_libdir = cls._get_staging_libdir()

        grub_format = get_bitbake_var('GRUB_MKIMAGE_FORMAT_PC')
        if not grub_format:
            grub_format = 'i386-pc'

        grub_prefix_path = get_bitbake_var('GRUB_PREFIX_PATH')
        if not grub_prefix_path:
            grub_prefix_path = '/boot/grub'

        grub_path = "%s/%s" %(hdddir, grub_prefix_path)
        core_img = '%s/grub-bios-core.img' % (kernel_dir)
        grub_mods_path = '%s/grub/%s' % (staging_libdir, grub_format)

        # Generate embedded grub config
        embed_cfg_str = 'search.file %s/grub.cfg root\n' % (grub_prefix_path)
        embed_cfg_str += 'set prefix=($root)%s\n' % (grub_prefix_path)
        embed_cfg_str += 'configfile ($root)%s/grub.cfg\n' % (grub_prefix_path)
        cfg = open('%s/embed.cfg' % (kernel_dir), 'w+')
        cfg.write(embed_cfg_str)
        cfg.close()

        # core.img doesn't get included into boot partition
        # it's later dd onto the resulting wic image.
        grub_mkimage = 'grub-mkimage \
        --prefix=%s \
        --format=%s \
        --config=%s/embed.cfg \
        --directory=%s \
        --output=%s %s' % \
        (grub_prefix_path, grub_format, kernel_dir,
         grub_mods_path, core_img, builtin_modules)
        exec_native_cmd(grub_mkimage, native_sysroot)

        # Copy grub modules
        install_dir = '%s/%s/%s' % (hdddir, grub_prefix_path, grub_format)
        os.makedirs(install_dir, exist_ok=True)

        for ctype in copy_types:
            files = glob('%s/grub/%s/%s' % \
                (staging_libdir, grub_format, ctype))
            for file in files:
                shutil.copy2(file, install_dir, follow_symlinks=True)

        # Create boot partition
        logger.debug('Prepare partition using rootfs in %s', hdddir)
        part.prepare_rootfs(cr_workdir, oe_builddir, hdddir,
                            native_sysroot, False)

    @classmethod
    def _do_install_grub(cls, creator, kernel_dir,
                         native_sysroot, full_path):
        core_img = '%s/grub-bios-core.img' % (kernel_dir)

        staging_libdir = cls._get_staging_libdir()

        grub_format = get_bitbake_var('GRUB_MKIMAGE_FORMAT_PC')
        if not grub_format:
            grub_format = 'i386-pc'

        boot_img = '%s/grub/%s/boot.img' % (staging_libdir, grub_format)
        if not os.path.exists(boot_img):
            raise WicError("Couldn't find %s. Did you include "
                           "do_image_wic[depends] += \"grub:do_populate_sysroot\" "
                           "in your image recipe" % boot_img)

        # Install boot.img or grub stage 1
        dd_cmd = "dd if=%s of=%s conv=notrunc bs=1 seek=0 count=440" % (boot_img, full_path)
        exec_cmd(dd_cmd, native_sysroot)

        if creator.ptable_format == 'msdos':
            # Install core.img or grub stage 1.5
            dd_cmd = "dd if=%s of=%s conv=notrunc bs=1 seek=512" % (core_img, full_path)
            exec_cmd(dd_cmd, native_sysroot)
        else:
            raise WicError("Unsupported partition table: %s" %
                           creator.ptable_format)
