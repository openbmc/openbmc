#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'isoimage-isohybrid' source plugin class for 'wic'
#
# AUTHORS
# Mihaly Varga <mihaly.varga (at] ni.com>

import glob
import logging
import os
import re
import shutil

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, exec_native_cmd, get_bitbake_var

logger = logging.getLogger('wic')

class IsoImagePlugin(SourcePlugin):
    """
    Create a bootable ISO image

    This plugin creates a hybrid, legacy and EFI bootable ISO image. The
    generated image can be used on optical media as well as USB media.

    Legacy boot uses syslinux and EFI boot uses grub or gummiboot (not
    implemented yet) as bootloader. The plugin creates the directories required
    by bootloaders and populates them by creating and configuring the
    bootloader files.

    Example kickstart file:
    part /boot --source isoimage-isohybrid --sourceparams="loader=grub-efi, \\
    image_name= IsoImage" --ondisk cd --label LIVECD
    bootloader  --timeout=10  --append=" "

    In --sourceparams "loader" specifies the bootloader used for booting in EFI
    mode, while "image_name" specifies the name of the generated image. In the
    example above, wic creates an ISO image named IsoImage-cd.direct (default
    extension added by direct imeger plugin) and a file named IsoImage-cd.iso
    """

    name = 'isoimage-isohybrid'

    @classmethod
    def do_configure_syslinux(cls, creator, cr_workdir):
        """
        Create loader-specific (syslinux) config
        """
        splash = os.path.join(cr_workdir, "ISO/boot/splash.jpg")
        if os.path.exists(splash):
            splashline = "menu background splash.jpg"
        else:
            splashline = ""

        bootloader = creator.ks.bootloader

        syslinux_conf = ""
        syslinux_conf += "PROMPT 0\n"
        syslinux_conf += "TIMEOUT %s \n" % (bootloader.timeout or 10)
        syslinux_conf += "\n"
        syslinux_conf += "ALLOWOPTIONS 1\n"
        syslinux_conf += "SERIAL 0 115200\n"
        syslinux_conf += "\n"
        if splashline:
            syslinux_conf += "%s\n" % splashline
        syslinux_conf += "DEFAULT boot\n"
        syslinux_conf += "LABEL boot\n"

        kernel = get_bitbake_var("KERNEL_IMAGETYPE")
        if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
            if get_bitbake_var("INITRAMFS_IMAGE"):
                kernel = "%s-%s.bin" % \
                    (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

        syslinux_conf += "KERNEL /" + kernel + "\n"
        syslinux_conf += "APPEND initrd=/initrd LABEL=boot %s\n" \
                             % bootloader.append

        logger.debug("Writing syslinux config %s/ISO/isolinux/isolinux.cfg",
                     cr_workdir)

        with open("%s/ISO/isolinux/isolinux.cfg" % cr_workdir, "w") as cfg:
            cfg.write(syslinux_conf)

    @classmethod
    def do_configure_grubefi(cls, part, creator, target_dir):
        """
        Create loader-specific (grub-efi) config
        """
        configfile = creator.ks.bootloader.configfile
        if configfile:
            grubefi_conf = get_custom_config(configfile)
            if grubefi_conf:
                logger.debug("Using custom configuration file %s for grub.cfg",
                             configfile)
            else:
                raise WicError("configfile is specified "
                               "but failed to get it from %s", configfile)
        else:
            splash = os.path.join(target_dir, "splash.jpg")
            if os.path.exists(splash):
                splashline = "menu background splash.jpg"
            else:
                splashline = ""

            bootloader = creator.ks.bootloader

            grubefi_conf = ""
            grubefi_conf += "serial --unit=0 --speed=115200 --word=8 "
            grubefi_conf += "--parity=no --stop=1\n"
            grubefi_conf += "default=boot\n"
            grubefi_conf += "timeout=%s\n" % (bootloader.timeout or 10)
            grubefi_conf += "\n"
            grubefi_conf += "search --set=root --label %s " % part.label
            grubefi_conf += "\n"
            grubefi_conf += "menuentry 'boot'{\n"

            kernel = get_bitbake_var("KERNEL_IMAGETYPE")
            if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
                if get_bitbake_var("INITRAMFS_IMAGE"):
                    kernel = "%s-%s.bin" % \
                        (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

            grubefi_conf += "linux /%s rootwait %s\n" \
                            % (kernel, bootloader.append)
            grubefi_conf += "initrd /initrd \n"
            grubefi_conf += "}\n"

            if splashline:
                grubefi_conf += "%s\n" % splashline

        cfg_path = os.path.join(target_dir, "grub.cfg")
        logger.debug("Writing grubefi config %s", cfg_path)

        with open(cfg_path, "w") as cfg:
            cfg.write(grubefi_conf)

    @staticmethod
    def _build_initramfs_path(rootfs_dir, cr_workdir):
        """
        Create path for initramfs image
        """

        initrd = get_bitbake_var("INITRD_LIVE") or get_bitbake_var("INITRD")
        if not initrd:
            initrd_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not initrd_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting.")

            image_name = get_bitbake_var("IMAGE_BASENAME")
            if not image_name:
                raise WicError("Couldn't find IMAGE_BASENAME, exiting.")

            image_type = get_bitbake_var("INITRAMFS_FSTYPES")
            if not image_type:
                raise WicError("Couldn't find INITRAMFS_FSTYPES, exiting.")

            machine = os.path.basename(initrd_dir)

            pattern = '%s/%s*%s.%s' % (initrd_dir, image_name, machine, image_type)
            files = glob.glob(pattern)
            if files:
                initrd = files[0]

        if not initrd or not os.path.exists(initrd):
            # Create initrd from rootfs directory
            initrd = "%s/initrd.cpio.gz" % cr_workdir
            initrd_dir = "%s/INITRD" % cr_workdir
            shutil.copytree("%s" % rootfs_dir, \
                            "%s" % initrd_dir, symlinks=True)

            if os.path.isfile("%s/init" % rootfs_dir):
                shutil.copy2("%s/init" % rootfs_dir, "%s/init" % initrd_dir)
            elif os.path.lexists("%s/init" % rootfs_dir):
                os.symlink(os.readlink("%s/init" % rootfs_dir), \
                            "%s/init" % initrd_dir)
            elif os.path.isfile("%s/sbin/init" % rootfs_dir):
                shutil.copy2("%s/sbin/init" % rootfs_dir, \
                            "%s" % initrd_dir)
            elif os.path.lexists("%s/sbin/init" % rootfs_dir):
                os.symlink(os.readlink("%s/sbin/init" % rootfs_dir), \
                            "%s/init" % initrd_dir)
            else:
                raise WicError("Couldn't find or build initrd, exiting.")

            exec_cmd("cd %s && find . | cpio -o -H newc -R root:root >%s/initrd.cpio " \
                     % (initrd_dir, cr_workdir), as_shell=True)
            exec_cmd("gzip -f -9 %s/initrd.cpio" % cr_workdir, as_shell=True)
            shutil.rmtree(initrd_dir)

        return initrd

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), creates loader-specific config
        """
        isodir = "%s/ISO/" % cr_workdir

        if os.path.exists(isodir):
            shutil.rmtree(isodir)

        install_cmd = "install -d %s " % isodir
        exec_cmd(install_cmd)

        # Overwrite the name of the created image
        logger.debug(source_params)
        if 'image_name' in source_params and \
                    source_params['image_name'].strip():
            creator.name = source_params['image_name'].strip()
            logger.debug("The name of the image is: %s", creator.name)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, prepare content for a bootable ISO image.
        """

        isodir = "%s/ISO" % cr_workdir

        if part.rootfs_dir is None:
            if not 'ROOTFS_DIR' in rootfs_dir:
                raise WicError("Couldn't find --rootfs-dir, exiting.")
            rootfs_dir = rootfs_dir['ROOTFS_DIR']
        else:
            if part.rootfs_dir in rootfs_dir:
                rootfs_dir = rootfs_dir[part.rootfs_dir]
            elif part.rootfs_dir:
                rootfs_dir = part.rootfs_dir
            else:
                raise WicError("Couldn't find --rootfs-dir=%s connection "
                               "or it is not a valid path, exiting." %
                               part.rootfs_dir)

        if not os.path.isdir(rootfs_dir):
            rootfs_dir = get_bitbake_var("IMAGE_ROOTFS")
        if not os.path.isdir(rootfs_dir):
            raise WicError("Couldn't find IMAGE_ROOTFS, exiting.")

        part.rootfs_dir = rootfs_dir
        deploy_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
        img_iso_dir = get_bitbake_var("ISODIR")

        # Remove the temporary file created by part.prepare_rootfs()
        if os.path.isfile(part.source_file):
            os.remove(part.source_file)

        # Support using a different initrd other than default
        if source_params.get('initrd'):
            initrd = source_params['initrd']
            if not deploy_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")
            cp_cmd = "cp %s/%s %s" % (deploy_dir, initrd, cr_workdir)
            exec_cmd(cp_cmd)
        else:
            # Prepare initial ramdisk
            initrd = "%s/initrd" % deploy_dir
            if not os.path.isfile(initrd):
                initrd = "%s/initrd" % img_iso_dir
            if not os.path.isfile(initrd):
                initrd = cls._build_initramfs_path(rootfs_dir, cr_workdir)

        install_cmd = "install -m 0644 %s %s/initrd" % (initrd, isodir)
        exec_cmd(install_cmd)

        # Remove the temporary file created by _build_initramfs_path function
        if os.path.isfile("%s/initrd.cpio.gz" % cr_workdir):
            os.remove("%s/initrd.cpio.gz" % cr_workdir)

        kernel = get_bitbake_var("KERNEL_IMAGETYPE")
        if get_bitbake_var("INITRAMFS_IMAGE_BUNDLE") == "1":
            if get_bitbake_var("INITRAMFS_IMAGE"):
                kernel = "%s-%s.bin" % \
                    (get_bitbake_var("KERNEL_IMAGETYPE"), get_bitbake_var("INITRAMFS_LINK_NAME"))

        install_cmd = "install -m 0644 %s/%s %s/%s" % \
                      (kernel_dir, kernel, isodir, kernel)
        exec_cmd(install_cmd)

        #Create bootloader for efi boot
        try:
            target_dir = "%s/EFI/BOOT" % isodir
            if os.path.exists(target_dir):
                shutil.rmtree(target_dir)

            os.makedirs(target_dir)

            if source_params['loader'] == 'grub-efi':
                # Builds bootx64.efi/bootia32.efi if ISODIR didn't exist or
                # didn't contains it
                target_arch = get_bitbake_var("TARGET_SYS")
                if not target_arch:
                    raise WicError("Coludn't find target architecture")

                if re.match("x86_64", target_arch):
                    grub_src_image = "grub-efi-bootx64.efi"
                    grub_dest_image = "bootx64.efi"
                elif re.match('i.86', target_arch):
                    grub_src_image = "grub-efi-bootia32.efi"
                    grub_dest_image = "bootia32.efi"
                else:
                    raise WicError("grub-efi is incompatible with target %s" %
                                   target_arch)

                grub_target = os.path.join(target_dir, grub_dest_image)
                if not os.path.isfile(grub_target):
                    grub_src = os.path.join(deploy_dir, grub_src_image)
                    if not os.path.exists(grub_src):
                        raise WicError("Grub loader %s is not found in %s. "
                                       "Please build grub-efi first" % (grub_src_image, deploy_dir))
                    shutil.copy(grub_src, grub_target)

                if not os.path.isfile(os.path.join(target_dir, "boot.cfg")):
                    cls.do_configure_grubefi(part, creator, target_dir)

            else:
                raise WicError("unrecognized bootimg-efi loader: %s" %
                               source_params['loader'])
        except KeyError:
            raise WicError("bootimg-efi requires a loader, none specified")

        # Create efi.img that contains bootloader files for EFI booting
        # if ISODIR didn't exist or didn't contains it
        if os.path.isfile("%s/efi.img" % img_iso_dir):
            install_cmd = "install -m 0644 %s/efi.img %s/efi.img" % \
                (img_iso_dir, isodir)
            exec_cmd(install_cmd)
        else:
            # Default to 100 blocks of extra space for file system overhead
            esp_extra_blocks = int(source_params.get('esp_extra_blocks', '100'))

            du_cmd = "du -bks %s/EFI" % isodir
            out = exec_cmd(du_cmd)
            blocks = int(out.split()[0])
            blocks += esp_extra_blocks
            logger.debug("Added 100 extra blocks to %s to get to %d "
                         "total blocks", part.mountpoint, blocks)

            # dosfs image for EFI boot
            bootimg = "%s/efi.img" % isodir

            esp_label = source_params.get('esp_label', 'EFIimg')

            dosfs_cmd = 'mkfs.vfat -n \'%s\' -S 512 -C %s %d' \
                        % (esp_label, bootimg, blocks)
            exec_native_cmd(dosfs_cmd, native_sysroot)

            mmd_cmd = "mmd -i %s ::/EFI" % bootimg
            exec_native_cmd(mmd_cmd, native_sysroot)

            mcopy_cmd = "mcopy -i %s -s %s/EFI/* ::/EFI/" \
                        % (bootimg, isodir)
            exec_native_cmd(mcopy_cmd, native_sysroot)

            chmod_cmd = "chmod 644 %s" % bootimg
            exec_cmd(chmod_cmd)

        # Prepare files for legacy boot
        syslinux_dir = get_bitbake_var("STAGING_DATADIR")
        if not syslinux_dir:
            raise WicError("Couldn't find STAGING_DATADIR, exiting.")

        if os.path.exists("%s/isolinux" % isodir):
            shutil.rmtree("%s/isolinux" % isodir)

        install_cmd = "install -d %s/isolinux" % isodir
        exec_cmd(install_cmd)

        cls.do_configure_syslinux(creator, cr_workdir)

        install_cmd = "install -m 444 %s/syslinux/ldlinux.sys " % syslinux_dir
        install_cmd += "%s/isolinux/ldlinux.sys" % isodir
        exec_cmd(install_cmd)

        install_cmd = "install -m 444 %s/syslinux/isohdpfx.bin " % syslinux_dir
        install_cmd += "%s/isolinux/isohdpfx.bin" % isodir
        exec_cmd(install_cmd)

        install_cmd = "install -m 644 %s/syslinux/isolinux.bin " % syslinux_dir
        install_cmd += "%s/isolinux/isolinux.bin" % isodir
        exec_cmd(install_cmd)

        install_cmd = "install -m 644 %s/syslinux/ldlinux.c32 " % syslinux_dir
        install_cmd += "%s/isolinux/ldlinux.c32" % isodir
        exec_cmd(install_cmd)

        #create ISO image
        iso_img = "%s/tempiso_img.iso" % cr_workdir
        iso_bootimg = "isolinux/isolinux.bin"
        iso_bootcat = "isolinux/boot.cat"
        efi_img = "efi.img"

        mkisofs_cmd = "mkisofs -V %s " % part.label
        mkisofs_cmd += "-o %s -U " % iso_img
        mkisofs_cmd += "-J -joliet-long -r -iso-level 2 -b %s " % iso_bootimg
        mkisofs_cmd += "-c %s -no-emul-boot -boot-load-size 4 " % iso_bootcat
        mkisofs_cmd += "-boot-info-table -eltorito-alt-boot "
        mkisofs_cmd += "-eltorito-platform 0xEF -eltorito-boot %s " % efi_img
        mkisofs_cmd += "-no-emul-boot %s " % isodir

        logger.debug("running command: %s", mkisofs_cmd)
        exec_native_cmd(mkisofs_cmd, native_sysroot)

        shutil.rmtree(isodir)

        du_cmd = "du -Lbks %s" % iso_img
        out = exec_cmd(du_cmd)
        isoimg_size = int(out.split()[0])

        part.size = isoimg_size
        part.source_file = iso_img

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.  In this case, we insert/modify the MBR using isohybrid
        utility for booting via BIOS from disk storage devices.
        """

        iso_img = "%s.p1" % disk.path
        full_path = creator._full_path(workdir, disk_name, "direct")
        full_path_iso = creator._full_path(workdir, disk_name, "iso")

        isohybrid_cmd = "isohybrid -u %s" % iso_img
        logger.debug("running command: %s", isohybrid_cmd)
        exec_native_cmd(isohybrid_cmd, native_sysroot)

        # Replace the image created by direct plugin with the one created by
        # mkisofs command. This is necessary because the iso image created by
        # mkisofs has a very specific MBR is system area of the ISO image, and
        # direct plugin adds and configures an another MBR.
        logger.debug("Replaceing the image created by direct plugin\n")
        os.remove(disk.path)
        shutil.copy2(iso_img, full_path_iso)
        shutil.copy2(full_path_iso, full_path)
