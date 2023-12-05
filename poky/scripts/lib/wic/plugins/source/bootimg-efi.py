#
# Copyright (c) 2014, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'bootimg-efi' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import tempfile
import shutil
import re

from glob import glob

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
    def _copy_additional_files(cls, hdddir, initrd, dtb):
        bootimg_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
        if not bootimg_dir:
            raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        if initrd:
            initrds = initrd.split(';')
            for rd in initrds:
                cp_cmd = "cp %s/%s %s" % (bootimg_dir, rd, hdddir)
                exec_cmd(cp_cmd, True)
        else:
            logger.debug("Ignoring missing initrd")

        if dtb:
            if ';' in dtb:
                raise WicError("Only one DTB supported, exiting")
            cp_cmd = "cp %s/%s %s" % (bootimg_dir, dtb, hdddir)
            exec_cmd(cp_cmd, True)

    @classmethod
    def do_configure_grubefi(cls, hdddir, creator, cr_workdir, source_params):
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

        initrd = source_params.get('initrd')
        dtb = source_params.get('dtb')

        cls._copy_additional_files(hdddir, initrd, dtb)

        if not custom_cfg:
            # Create grub configuration using parameters from wks file
            bootloader = creator.ks.bootloader
            title = source_params.get('title')

            grubefi_conf = ""
            grubefi_conf += "serial --unit=0 --speed=115200 --word=8 --parity=no --stop=1\n"
            grubefi_conf += "default=boot\n"
            grubefi_conf += "timeout=%s\n" % bootloader.timeout
            grubefi_conf += "menuentry '%s'{\n" % (title if title else "boot")

            kernel = get_bitbake_var("KERNEL_IMAGETYPE")
            if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
                if get_bitbake_var("INITRAMFS_IMAGE"):
                    kernel = "%s-%s.bin" % \
                        (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

            label = source_params.get('label')
            label_conf = "root=%s" % creator.rootdev
            if label:
                label_conf = "LABEL=%s" % label

            grubefi_conf += "linux /%s %s rootwait %s\n" \
                % (kernel, label_conf, bootloader.append)

            if initrd:
                initrds = initrd.split(';')
                grubefi_conf += "initrd"
                for rd in initrds:
                    grubefi_conf += " /%s" % rd
                grubefi_conf += "\n"

            if dtb:
                grubefi_conf += "devicetree /%s\n" % dtb

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

        unified_image = source_params.get('create-unified-kernel-image') == "true"

        loader_conf = ""
        if not unified_image:
            loader_conf += "default boot\n"
        loader_conf += "timeout %d\n" % bootloader.timeout

        initrd = source_params.get('initrd')
        dtb = source_params.get('dtb')

        if not unified_image:
            cls._copy_additional_files(hdddir, initrd, dtb)

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
            kernel = get_bitbake_var("KERNEL_IMAGETYPE")
            if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
                if get_bitbake_var("INITRAMFS_IMAGE"):
                    kernel = "%s-%s.bin" % \
                        (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

            title = source_params.get('title')

            boot_conf = ""
            boot_conf += "title %s\n" % (title if title else "boot")
            boot_conf += "linux /%s\n" % kernel

            label = source_params.get('label')
            label_conf = "LABEL=Boot root=%s" % creator.rootdev
            if label:
                label_conf = "LABEL=%s" % label

            boot_conf += "options %s %s\n" % \
                             (label_conf, bootloader.append)

            if initrd:
                initrds = initrd.split(';')
                for rd in initrds:
                    boot_conf += "initrd /%s\n" % rd

            if dtb:
                boot_conf += "devicetree /%s\n" % dtb

        if not unified_image:
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
                cls.do_configure_grubefi(hdddir, creator, cr_workdir, source_params)
            elif source_params['loader'] == 'systemd-boot':
                cls.do_configure_systemdboot(hdddir, creator, cr_workdir, source_params)
            elif source_params['loader'] == 'uefi-kernel':
                pass
            else:
                raise WicError("unrecognized bootimg-efi loader: %s" % source_params['loader'])
        except KeyError:
            raise WicError("bootimg-efi requires a loader, none specified")

        if get_bitbake_var("IMAGE_EFI_BOOT_FILES") is None:
            logger.debug('No boot files defined in IMAGE_EFI_BOOT_FILES')
        else:
            boot_files = None
            for (fmt, id) in (("_uuid-%s", part.uuid), ("_label-%s", part.label), (None, None)):
                if fmt:
                    var = fmt % id
                else:
                    var = ""

                boot_files = get_bitbake_var("IMAGE_EFI_BOOT_FILES" + var)
                if boot_files:
                    break

            logger.debug('Boot files: %s', boot_files)

            # list of tuples (src_name, dst_name)
            deploy_files = []
            for src_entry in re.findall(r'[\w;\-\./\*]+', boot_files):
                if ';' in src_entry:
                    dst_entry = tuple(src_entry.split(';'))
                    if not dst_entry[0] or not dst_entry[1]:
                        raise WicError('Malformed boot file entry: %s' % src_entry)
                else:
                    dst_entry = (src_entry, src_entry)

                logger.debug('Destination entry: %r', dst_entry)
                deploy_files.append(dst_entry)

            cls.install_task = [];
            for deploy_entry in deploy_files:
                src, dst = deploy_entry
                if '*' in src:
                    # by default install files under their basename
                    entry_name_fn = os.path.basename
                    if dst != src:
                        # unless a target name was given, then treat name
                        # as a directory and append a basename
                        entry_name_fn = lambda name: \
                                        os.path.join(dst,
                                                     os.path.basename(name))

                    srcs = glob(os.path.join(kernel_dir, src))

                    logger.debug('Globbed sources: %s', ', '.join(srcs))
                    for entry in srcs:
                        src = os.path.relpath(entry, kernel_dir)
                        entry_dst_name = entry_name_fn(entry)
                        cls.install_task.append((src, entry_dst_name))
                else:
                    cls.install_task.append((src, dst))

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

        kernel = get_bitbake_var("KERNEL_IMAGETYPE")
        if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
            if get_bitbake_var("INITRAMFS_IMAGE"):
                kernel = "%s-%s.bin" % \
                    (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

        if source_params.get('create-unified-kernel-image') == "true":
            initrd = source_params.get('initrd')
            if not initrd:
                raise WicError("initrd= must be specified when create-unified-kernel-image=true, exiting")

            deploy_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            efi_stub = glob("%s/%s" % (deploy_dir, "linux*.efi.stub"))
            if len(efi_stub) == 0:
                raise WicError("Unified Kernel Image EFI stub not found, exiting")
            efi_stub = efi_stub[0]

            with tempfile.TemporaryDirectory() as tmp_dir:
                label = source_params.get('label')
                label_conf = "root=%s" % creator.rootdev
                if label:
                    label_conf = "LABEL=%s" % label

                bootloader = creator.ks.bootloader
                cmdline = open("%s/cmdline" % tmp_dir, "w")
                cmdline.write("%s %s" % (label_conf, bootloader.append))
                cmdline.close()

                initrds = initrd.split(';')
                initrd = open("%s/initrd" % tmp_dir, "wb")
                for f in initrds:
                    with open("%s/%s" % (deploy_dir, f), 'rb') as in_file:
                        shutil.copyfileobj(in_file, initrd)
                initrd.close()

                # Searched by systemd-boot:
                # https://systemd.io/BOOT_LOADER_SPECIFICATION/#type-2-efi-unified-kernel-images
                install_cmd = "install -d %s/EFI/Linux" % hdddir
                exec_cmd(install_cmd)

                staging_dir_host = get_bitbake_var("STAGING_DIR_HOST")
                target_sys = get_bitbake_var("TARGET_SYS")

                objdump_cmd = "%s-objdump" % target_sys
                objdump_cmd += " -p %s" % efi_stub
                objdump_cmd += " | awk '{ if ($1 == \"SectionAlignment\"){print $2} }'"

                ret, align_str = exec_native_cmd(objdump_cmd, native_sysroot)
                align = int(align_str, 16)

                objdump_cmd = "%s-objdump" % target_sys
                objdump_cmd += " -h %s | tail -2" % efi_stub
                ret, output = exec_native_cmd(objdump_cmd, native_sysroot)

                offset = int(output.split()[2], 16) + int(output.split()[3], 16)

                osrel_off = offset + align - offset % align
                osrel_path = "%s/usr/lib/os-release" % staging_dir_host
                osrel_sz = os.stat(osrel_path).st_size

                cmdline_off = osrel_off + osrel_sz
                cmdline_off = cmdline_off + align - cmdline_off % align
                cmdline_sz = os.stat(cmdline.name).st_size

                dtb_off = cmdline_off + cmdline_sz
                dtb_off = dtb_off + align - dtb_off % align

                dtb = source_params.get('dtb')
                if dtb:
                    if ';' in dtb:
                        raise WicError("Only one DTB supported, exiting")
                    dtb_path = "%s/%s" % (deploy_dir, dtb)
                    dtb_params = '--add-section .dtb=%s --change-section-vma .dtb=0x%x' % \
                            (dtb_path, dtb_off)
                    linux_off = dtb_off + os.stat(dtb_path).st_size
                    linux_off = linux_off + align - linux_off % align
                else:
                    dtb_params = ''
                    linux_off = dtb_off

                linux_path = "%s/%s" % (staging_kernel_dir, kernel)
                linux_sz = os.stat(linux_path).st_size

                initrd_off = linux_off + linux_sz
                initrd_off = initrd_off + align - initrd_off % align

                # https://www.freedesktop.org/software/systemd/man/systemd-stub.html
                objcopy_cmd = "%s-objcopy" % target_sys
                objcopy_cmd += " --enable-deterministic-archives"
                objcopy_cmd += " --preserve-dates"
                objcopy_cmd += " --add-section .osrel=%s" % osrel_path
                objcopy_cmd += " --change-section-vma .osrel=0x%x" % osrel_off
                objcopy_cmd += " --add-section .cmdline=%s" % cmdline.name
                objcopy_cmd += " --change-section-vma .cmdline=0x%x" % cmdline_off
                objcopy_cmd += dtb_params
                objcopy_cmd += " --add-section .linux=%s" % linux_path
                objcopy_cmd += " --change-section-vma .linux=0x%x" % linux_off
                objcopy_cmd += " --add-section .initrd=%s" % initrd.name
                objcopy_cmd += " --change-section-vma .initrd=0x%x" % initrd_off
                objcopy_cmd += " %s %s/EFI/Linux/linux.efi" % (efi_stub, hdddir)

                exec_native_cmd(objcopy_cmd, native_sysroot)
        else:
            if source_params.get('install-kernel-into-boot-dir') != 'false':
                install_cmd = "install -m 0644 %s/%s %s/%s" % \
                    (staging_kernel_dir, kernel, hdddir, kernel)
                exec_cmd(install_cmd)

        if get_bitbake_var("IMAGE_EFI_BOOT_FILES"):
            for src_path, dst_path in cls.install_task:
                install_cmd = "install -m 0644 -D %s %s" \
                              % (os.path.join(kernel_dir, src_path),
                                 os.path.join(hdddir, dst_path))
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
            elif source_params['loader'] == 'uefi-kernel':
                kernel = get_bitbake_var("KERNEL_IMAGETYPE")
                if not kernel:
                    raise WicError("Empty KERNEL_IMAGETYPE %s\n" % target)
                target = get_bitbake_var("TARGET_SYS")
                if not target:
                    raise WicError("Unknown arch (TARGET_SYS) %s\n" % target)

                if re.match("x86_64", target):
                    kernel_efi_image = "bootx64.efi"
                elif re.match('i.86', target):
                    kernel_efi_image = "bootia32.efi"
                elif re.match('aarch64', target):
                    kernel_efi_image = "bootaa64.efi"
                elif re.match('arm', target):
                    kernel_efi_image = "bootarm.efi"
                else:
                    raise WicError("UEFI stub kernel is incompatible with target %s" % target)

                for mod in [x for x in os.listdir(kernel_dir) if x.startswith(kernel)]:
                    cp_cmd = "cp %s/%s %s/EFI/BOOT/%s" % (kernel_dir, mod, hdddir, kernel_efi_image)
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

        for paths in part.include_path or []:
            for path in paths:
                cp_cmd = "cp -r %s %s/" % (path, hdddir)
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

        # required for compatibility with certain devices expecting file system
        # block count to be equal to partition block count
        if blocks < part.fixed_size:
            blocks = part.fixed_size
            logger.debug("Overriding %s to %d total blocks for compatibility",
                     part.mountpoint, blocks)

        # dosfs image, created by mkdosfs
        bootimg = "%s/boot.img" % cr_workdir

        label = part.label if part.label else "ESP"

        dosfs_cmd = "mkdosfs -n %s -i %s -C %s %d" % \
                    (label, part.fsuuid, bootimg, blocks)
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
