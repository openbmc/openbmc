# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-

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
# This implements the 'isoimage-isohybrid' source plugin class for 'wic'
#
# AUTHORS
# Mihaly Varga <mihaly.varga (at] ni.com>

import os
import re
import shutil
import glob

from wic import msger
from wic.pluginbase import SourcePlugin
from wic.utils.oe.misc import exec_cmd, exec_native_cmd, get_bitbake_var

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
    image_name= IsoImage" --ondisk cd --label LIVECD --fstype=ext2
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
        splash = os.path.join(cr_workdir, "/ISO/boot/splash.jpg")
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

        kernel = "/bzImage"
        syslinux_conf += "KERNEL " + kernel + "\n"
        syslinux_conf += "APPEND initrd=/initrd LABEL=boot %s\n" \
                             % bootloader.append

        msger.debug("Writing syslinux config %s/ISO/isolinux/isolinux.cfg" \
                    % cr_workdir)
        with open("%s/ISO/isolinux/isolinux.cfg" % cr_workdir, "w") as cfg:
            cfg.write(syslinux_conf)

    @classmethod
    def do_configure_grubefi(cls, part, creator, cr_workdir):
        """
        Create loader-specific (grub-efi) config
        """
        splash = os.path.join(cr_workdir, "/EFI/boot/splash.jpg")
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

        kernel = "/bzImage"

        grubefi_conf += "linux %s rootwait %s\n" \
            % (kernel, bootloader.append)
        grubefi_conf += "initrd /initrd \n"
        grubefi_conf += "}\n"

        if splashline:
            grubefi_conf += "%s\n" % splashline

        msger.debug("Writing grubefi config %s/EFI/BOOT/grub.cfg" \
                        % cr_workdir)
        with open("%s/EFI/BOOT/grub.cfg" % cr_workdir, "w") as cfg:
            cfg.write(grubefi_conf)

    @staticmethod
    def _build_initramfs_path(rootfs_dir, cr_workdir):
        """
        Create path for initramfs image
        """

        initrd = get_bitbake_var("INITRD")
        if not initrd:
            initrd_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not initrd_dir:
                msger.error("Couldn't find DEPLOY_DIR_IMAGE, exiting.\n")

            image_name = get_bitbake_var("IMAGE_BASENAME")
            if not image_name:
                msger.error("Couldn't find IMAGE_BASENAME, exiting.\n")

            image_type = get_bitbake_var("INITRAMFS_FSTYPES")
            if not image_type:
                msger.error("Couldn't find INITRAMFS_FSTYPES, exiting.\n")

            machine_arch = get_bitbake_var("MACHINE_ARCH")
            if not machine_arch:
                msger.error("Couldn't find MACHINE_ARCH, exiting.\n")

            initrd = glob.glob('%s/%s*%s.%s' % (initrd_dir, image_name, machine_arch, image_type))[0]

        if not os.path.exists(initrd):
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
                msger.error("Couldn't find or build initrd, exiting.\n")

            exec_cmd("cd %s && find . | cpio -o -H newc -R +0:+0 >./initrd.cpio " \
                    % initrd_dir, as_shell=True)
            exec_cmd("gzip -f -9 -c %s/initrd.cpio > %s" \
                    % (cr_workdir, initrd), as_shell=True)
            shutil.rmtree(initrd_dir)

        return initrd

    @classmethod
    def do_stage_partition(cls, part, source_params, creator, cr_workdir,
                           oe_builddir, bootimg_dir, kernel_dir,
                           native_sysroot):
        """
        Special content staging called before do_prepare_partition().
        It cheks if all necessary tools are available, if not
        tries to instal them.
        """
        # Make sure parted is available in native sysroot
        if not os.path.isfile("%s/usr/sbin/parted" % native_sysroot):
            msger.info("Building parted-native...\n")
            exec_cmd("bitbake parted-native")

        # Make sure mkfs.ext2/3/4 is available in native sysroot
        if not os.path.isfile("%s/sbin/mkfs.ext2" % native_sysroot):
            msger.info("Building e2fsprogs-native...\n")
            exec_cmd("bitbake e2fsprogs-native")

        # Make sure syslinux is available in sysroot and in native sysroot
        syslinux_dir = get_bitbake_var("STAGING_DATADIR")
        if not syslinux_dir:
            msger.error("Couldn't find STAGING_DATADIR, exiting.\n")
        if not os.path.exists("%s/syslinux" % syslinux_dir):
            msger.info("Building syslinux...\n")
            exec_cmd("bitbake syslinux")
        if not os.path.exists("%s/syslinux" % syslinux_dir):
            msger.error("Please build syslinux first\n")

        # Make sure syslinux is available in native sysroot
        if not os.path.exists("%s/usr/bin/syslinux" % native_sysroot):
            msger.info("Building syslinux-native...\n")
            exec_cmd("bitbake syslinux-native")

        #Make sure mkisofs is available in native sysroot
        if not os.path.isfile("%s/usr/bin/mkisofs" % native_sysroot):
            msger.info("Building cdrtools-native...\n")
            exec_cmd("bitbake cdrtools-native")

        # Make sure mkfs.vfat is available in native sysroot
        if not os.path.isfile("%s/sbin/mkfs.vfat" % native_sysroot):
            msger.info("Building dosfstools-native...\n")
            exec_cmd("bitbake dosfstools-native")

        # Make sure mtools is available in native sysroot
        if not os.path.isfile("%s/usr/bin/mcopy" % native_sysroot):
            msger.info("Building mtools-native...\n")
            exec_cmd("bitbake mtools-native")

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), creates loader-specific config
        """
        isodir = "%s/ISO/" % cr_workdir

        if os.path.exists(cr_workdir):
            shutil.rmtree(cr_workdir)

        install_cmd = "install -d %s " % isodir
        exec_cmd(install_cmd)

        # Overwrite the name of the created image
        msger.debug("%s" % source_params)
        if 'image_name' in source_params and \
                    source_params['image_name'].strip():
            creator.name = source_params['image_name'].strip()
            msger.debug("The name of the image is: %s" % creator.name)

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
                msger.error("Couldn't find --rootfs-dir, exiting.\n")
            rootfs_dir = rootfs_dir['ROOTFS_DIR']
        else:
            if part.rootfs_dir in rootfs_dir:
                rootfs_dir = rootfs_dir[part.rootfs_dir]
            elif part.rootfs_dir:
                rootfs_dir = part.rootfs_dir
            else:
                msg = "Couldn't find --rootfs-dir=%s connection "
                msg += "or it is not a valid path, exiting.\n"
                msger.error(msg % part.rootfs_dir)

        if not os.path.isdir(rootfs_dir):
            rootfs_dir = get_bitbake_var("IMAGE_ROOTFS")
        if not os.path.isdir(rootfs_dir):
            msger.error("Couldn't find IMAGE_ROOTFS, exiting.\n")

        part.rootfs_dir = rootfs_dir

        # Prepare rootfs.img
        hdd_dir = get_bitbake_var("HDDDIR")
        img_iso_dir = get_bitbake_var("ISODIR")

        rootfs_img = "%s/rootfs.img" % hdd_dir
        if not os.path.isfile(rootfs_img):
            rootfs_img = "%s/rootfs.img" % img_iso_dir
        if not os.path.isfile(rootfs_img):
            # check if rootfs.img is in deploydir
            deploy_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            image_name = get_bitbake_var("IMAGE_LINK_NAME")
            rootfs_img = "%s/%s.%s" \
                % (deploy_dir, image_name, part.fstype)

        if not os.path.isfile(rootfs_img):
            # create image file with type specified by --fstype
            # which contains rootfs
            du_cmd = "du -bks %s" % rootfs_dir
            out = exec_cmd(du_cmd)
            part.size = int(out.split()[0])
            part.extra_space = 0
            part.overhead_factor = 1.2
            part.prepare_rootfs(cr_workdir, oe_builddir, rootfs_dir, \
                                native_sysroot)
            rootfs_img = part.source_file

        install_cmd = "install -m 0644 %s %s/rootfs.img" \
            % (rootfs_img, isodir)
        exec_cmd(install_cmd)

        # Remove the temporary file created by part.prepare_rootfs()
        if os.path.isfile(part.source_file):
            os.remove(part.source_file)

        # Prepare initial ramdisk
        initrd = "%s/initrd" % hdd_dir
        if not os.path.isfile(initrd):
            initrd = "%s/initrd" % img_iso_dir
        if not os.path.isfile(initrd):
            initrd = cls._build_initramfs_path(rootfs_dir, cr_workdir)

        install_cmd = "install -m 0644 %s %s/initrd" \
            % (initrd, isodir)
        exec_cmd(install_cmd)

        # Remove the temporary file created by _build_initramfs_path function
        if os.path.isfile("%s/initrd.cpio.gz" % cr_workdir):
            os.remove("%s/initrd.cpio.gz" % cr_workdir)

        # Install bzImage
        install_cmd = "install -m 0644 %s/bzImage %s/bzImage" % \
            (kernel_dir, isodir)
        exec_cmd(install_cmd)

        #Create bootloader for efi boot
        try:
            if source_params['loader'] == 'grub-efi':
                # Builds grub.cfg if ISODIR didn't exist or
                # didn't contains grub.cfg
                bootimg_dir = img_iso_dir
                if not os.path.exists("%s/EFI/BOOT" % bootimg_dir):
                    bootimg_dir = "%s/bootimg" % cr_workdir
                    if os.path.exists(bootimg_dir):
                        shutil.rmtree(bootimg_dir)
                    install_cmd = "install -d %s/EFI/BOOT" % bootimg_dir
                    exec_cmd(install_cmd)

                if not os.path.isfile("%s/EFI/BOOT/boot.cfg" % bootimg_dir):
                    cls.do_configure_grubefi(part, creator, bootimg_dir)

                # Builds bootx64.efi/bootia32.efi if ISODIR didn't exist or
                # didn't contains it
                target_arch = get_bitbake_var("TARGET_SYS")
                if not target_arch:
                    msger.error("Coludn't find target architecture\n")

                if re.match("x86_64", target_arch):
                    grub_target = 'x86_64-efi'
                    grub_image = "bootx64.efi"
                elif re.match('i.86', target_arch):
                    grub_target = 'i386-efi'
                    grub_image = "bootia32.efi"
                else:
                    msger.error("grub-efi is incompatible with target %s\n" \
                                % target_arch)

                if not os.path.isfile("%s/EFI/BOOT/%s" \
                                % (bootimg_dir, grub_image)):
                    grub_path = get_bitbake_var("STAGING_LIBDIR")
                    if not grub_path:
                        msger.error("Couldn't find STAGING_LIBDIR, exiting.\n")

                    grub_core = "%s/grub/%s" % (grub_path, grub_target)
                    if not os.path.exists(grub_core):
                        msger.info("Building grub-efi...\n")
                        exec_cmd("bitbake grub-efi")
                    if not os.path.exists(grub_core):
                        msger.error("Please build grub-efi first\n")

                    grub_cmd = "grub-mkimage -p '/EFI/BOOT' "
                    grub_cmd += "-d %s "  % grub_core
                    grub_cmd += "-O %s -o %s/EFI/BOOT/%s " \
                                % (grub_target, bootimg_dir, grub_image)
                    grub_cmd += "part_gpt part_msdos ntfs ntfscomp fat ext2 "
                    grub_cmd += "normal chain boot configfile linux multiboot "
                    grub_cmd += "search efi_gop efi_uga font gfxterm gfxmenu "
                    grub_cmd += "terminal minicmd test iorw loadenv echo help "
                    grub_cmd += "reboot serial terminfo iso9660 loopback tar "
                    grub_cmd += "memdisk ls search_fs_uuid udf btrfs xfs lvm "
                    grub_cmd += "reiserfs ata "
                    exec_native_cmd(grub_cmd, native_sysroot)

            else:
                # TODO: insert gummiboot stuff
                msger.error("unrecognized bootimg-efi loader: %s" \
                            % source_params['loader'])
        except KeyError:
            msger.error("bootimg-efi requires a loader, none specified")

        if os.path.exists("%s/EFI/BOOT" % isodir):
            shutil.rmtree("%s/EFI/BOOT" % isodir)

        shutil.copytree(bootimg_dir+"/EFI/BOOT", isodir+"/EFI/BOOT")

        # If exists, remove cr_workdir/bootimg temporary folder
        if os.path.exists("%s/bootimg" % cr_workdir):
            shutil.rmtree("%s/bootimg" % cr_workdir)

        # Create efi.img that contains bootloader files for EFI booting
        # if ISODIR didn't exist or didn't contains it
        if os.path.isfile("%s/efi.img" % img_iso_dir):
            install_cmd = "install -m 0644 %s/efi.img %s/efi.img" % \
                (img_iso_dir, isodir)
            exec_cmd(install_cmd)
        else:
            du_cmd = "du -bks %s/EFI" % isodir
            out = exec_cmd(du_cmd)
            blocks = int(out.split()[0])
            # Add some extra space for file system overhead
            blocks += 100
            msg = "Added 100 extra blocks to %s to get to %d total blocks" \
                    % (part.mountpoint, blocks)
            msger.debug(msg)

            # Ensure total sectors is an integral number of sectors per
            # track or mcopy will complain. Sectors are 512 bytes, and we
            # generate images with 32 sectors per track. This calculation is
            # done in blocks, thus the mod by 16 instead of 32.
            blocks += (16 - (blocks % 16))

            # dosfs image for EFI boot
            bootimg = "%s/efi.img" % isodir

            dosfs_cmd = 'mkfs.vfat -n "EFIimg" -S 512 -C %s %d' \
                        % (bootimg, blocks)
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
            msger.error("Couldn't find STAGING_DATADIR, exiting.\n")

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

        msger.debug("running command: %s" % mkisofs_cmd)
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

        full_path = creator._full_path(workdir, disk_name, "direct")
        iso_img = "%s.p1" % full_path
        full_path_iso = creator._full_path(workdir, disk_name, "iso")

        isohybrid_cmd = "isohybrid -u %s" % iso_img
        msger.debug("running command: %s" % \
                    isohybrid_cmd)
        exec_native_cmd(isohybrid_cmd, native_sysroot)

        # Replace the image created by direct plugin with the one created by
        # mkisofs command. This is necessary because the iso image created by
        # mkisofs has a very specific MBR is system area of the ISO image, and
        # direct plugin adds and configures an another MBR.
        msger.debug("Replaceing the image created by direct plugin\n")
        os.remove(full_path)
        shutil.copy2(iso_img, full_path_iso)
        shutil.copy2(full_path_iso, full_path)

        # Remove temporary ISO file
        os.remove(iso_img)
