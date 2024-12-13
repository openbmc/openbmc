#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'bootimg-partition' source plugin class for
# 'wic'. The plugin creates an image of boot partition, copying over
# files listed in IMAGE_BOOT_FILES bitbake variable.
#
# AUTHORS
# Maciej Borzecki <maciej.borzecki (at] open-rnd.pl>
#

import logging
import os
import re

from oe.bootfiles import get_boot_files

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, get_bitbake_var

logger = logging.getLogger('wic')

class BootimgPartitionPlugin(SourcePlugin):
    """
    Create an image of boot partition, copying over files
    listed in IMAGE_BOOT_FILES bitbake variable.
    """

    name = 'bootimg-partition'
    image_boot_files_var_name = 'IMAGE_BOOT_FILES'

    @classmethod
    def do_configure_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             native_sysroot):
        """
        Called before do_prepare_partition(), create u-boot specific boot config
        """
        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)
        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        boot_files = None
        for (fmt, id) in (("_uuid-%s", part.uuid), ("_label-%s", part.label), (None, None)):
            if fmt:
                var = fmt % id
            else:
                var = ""

            boot_files = get_bitbake_var(cls.image_boot_files_var_name + var)
            if boot_files is not None:
                break

        if boot_files is None:
            raise WicError('No boot files defined, %s unset for entry #%d' % (cls.image_boot_files_var_name, part.lineno))

        logger.debug('Boot files: %s', boot_files)

        cls.install_task = get_boot_files(kernel_dir, boot_files)
        if source_params.get('loader') != "u-boot":
            return

        configfile = cr.ks.bootloader.configfile
        custom_cfg = None
        if configfile:
            custom_cfg = get_custom_config(configfile)
            if custom_cfg:
                # Use a custom configuration for extlinux.conf
                extlinux_conf = custom_cfg
                logger.debug("Using custom configuration file "
                             "%s for extlinux.conf", configfile)
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s." % configfile)

        if not custom_cfg:
            # The kernel types supported by the sysboot of u-boot
            kernel_types = ["zImage", "Image", "fitImage", "uImage", "vmlinux"]
            has_dtb = False
            fdt_dir = '/'
            kernel_name = None

            # Find the kernel image name, from the highest precedence to lowest
            for image in kernel_types:
                for task in cls.install_task:
                    src, dst = task
                    if re.match(image, src):
                        kernel_name = os.path.join('/', dst)
                        break
                if kernel_name:
                    break

            for task in cls.install_task:
                src, dst = task
                # We suppose that all the dtb are in the same directory
                if re.search(r'\.dtb', src) and fdt_dir == '/':
                    has_dtb = True
                    fdt_dir = os.path.join(fdt_dir, os.path.dirname(dst))
                    break

            if not kernel_name:
                raise WicError('No kernel file found')

            # Compose the extlinux.conf
            extlinux_conf = "default Yocto\n"
            extlinux_conf += "label Yocto\n"
            extlinux_conf += "   kernel %s\n" % kernel_name
            if has_dtb:
                extlinux_conf += "   fdtdir %s\n" % fdt_dir
            bootloader = cr.ks.bootloader
            extlinux_conf += "append root=%s rootwait %s\n" \
                             % (cr.rootdev, bootloader.append if bootloader.append else '')

        install_cmd = "install -d %s/extlinux/" % hdddir
        exec_cmd(install_cmd)
        cfg = open("%s/extlinux/extlinux.conf" % hdddir, "w")
        cfg.write(extlinux_conf)
        cfg.close()


    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, does the following:
        - sets up a vfat partition
        - copies all files listed in IMAGE_BOOT_FILES variable
        """
        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        logger.debug('Kernel dir: %s', bootimg_dir)


        for task in cls.install_task:
            src_path, dst_path = task
            logger.debug('Install %s as %s', src_path, dst_path)
            install_cmd = "install -m 0644 -D %s %s" \
                          % (os.path.join(kernel_dir, src_path),
                             os.path.join(hdddir, dst_path))
            exec_cmd(install_cmd)

        logger.debug('Prepare boot partition using rootfs in %s', hdddir)
        part.prepare_rootfs(cr_workdir, oe_builddir, hdddir,
                            native_sysroot, False)
