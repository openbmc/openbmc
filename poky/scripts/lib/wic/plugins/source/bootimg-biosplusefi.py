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
# This implements the 'bootimg-biosplusefi' source plugin class for 'wic'
#
# AUTHORS
# William Bourque <wbourque [at) gmail.com>

import types

from wic.pluginbase import SourcePlugin
from importlib.machinery import SourceFileLoader

class BootimgBiosPlusEFIPlugin(SourcePlugin):
    """
    Create MBR + EFI boot partition

    This plugin creates a boot partition that contains both
    legacy BIOS and EFI content. It will be able to boot from both.
    This is useful when managing PC fleet with some older machines
    without EFI support.

    Note it is possible to create an image that can boot from both
    legacy BIOS and EFI by defining two partitions : one with arg
    --source bootimg-efi  and another one with --source bootimg-pcbios.
    However, this method has the obvious downside that it requires TWO
    partitions to be created on the storage device.
    Both partitions will also be marked as "bootable" which does not work on
    most BIOS, has BIOS often uses the "bootable" flag to determine
    what to boot. If you have such a BIOS, you need to manually remove the
    "bootable" flag from the EFI partition for the drive to be bootable.
    Having two partitions also seems to confuse wic : the content of
    the first partition will be duplicated into the second, even though it
    will not be used at all.

    Also, unlike "isoimage-isohybrid" that also does BIOS and EFI, this plugin
    allows you to have more than only a single rootfs partitions and does
    not turn the rootfs into an initramfs RAM image.

    This plugin is made to put everything into a single /boot partition so it
    does not have the limitations listed above.

    The plugin is made so it does tries not to reimplement what's already
    been done in other plugins; as such it imports "bootimg-pcbios"
    and "bootimg-efi".
    Plugin "bootimg-pcbios" is used to generate legacy BIOS boot.
    Plugin "bootimg-efi" is used to generate the UEFI boot. Note that it
    requires a --sourceparams argument to know which loader to use; refer
    to "bootimg-efi" code/documentation for the list of loader.

    Imports are handled with "SourceFileLoader" from importlib as it is
    otherwise very difficult to import module that has hyphen "-" in their
    filename.
    The SourcePlugin() methods used in the plugins (do_install_disk,
    do_configure_partition, do_prepare_partition) are then called on both,
    beginning by "bootimg-efi".

    Plugin options, such as "--sourceparams" can still be passed to a
    plugin, as long they does not cause issue in the other plugin.

    Example wic configuration:
    part /boot --source bootimg-biosplusefi --sourceparams="loader=grub-efi"\\
               --ondisk sda --label os_boot --active --align 1024 --use-uuid
    """

    name = 'bootimg-biosplusefi'

    __PCBIOS_MODULE_NAME = "bootimg-pcbios"
    __EFI_MODULE_NAME = "bootimg-efi"

    __imgEFIObj = None
    __imgBiosObj = None

    @classmethod
    def __init__(cls):
        """
        Constructor (init)
        """

        # XXX
        # For some reasons, __init__ constructor is never called.
        # Something to do with how pluginbase works?
        cls.__instanciateSubClasses()

    @classmethod
    def __instanciateSubClasses(cls):
        """

        """

        # Import bootimg-pcbios (class name "BootimgPcbiosPlugin")
        modulePath = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                  cls.__PCBIOS_MODULE_NAME + ".py")
        loader = SourceFileLoader(cls.__PCBIOS_MODULE_NAME, modulePath)
        mod = types.ModuleType(loader.name)
        loader.exec_module(mod)
        cls.__imgBiosObj = mod.BootimgPcbiosPlugin()

        # Import bootimg-efi (class name "BootimgEFIPlugin")
        modulePath = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                  cls.__EFI_MODULE_NAME + ".py")
        loader = SourceFileLoader(cls.__EFI_MODULE_NAME, modulePath)
        mod = types.ModuleType(loader.name)
        loader.exec_module(mod)
        cls.__imgEFIObj = mod.BootimgEFIPlugin()

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.
        """

        if ( (not cls.__imgEFIObj) or (not cls.__imgBiosObj) ):
            cls.__instanciateSubClasses()

        cls.__imgEFIObj.do_install_disk(
            disk,
            disk_name,
            creator,
            workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            native_sysroot)

        cls.__imgBiosObj.do_install_disk(
            disk,
            disk_name,
            creator,
            workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            native_sysroot)

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition()
        """

        if ( (not cls.__imgEFIObj) or (not cls.__imgBiosObj) ):
            cls.__instanciateSubClasses()

        cls.__imgEFIObj.do_configure_partition(
            part,
            source_params,
            creator,
            cr_workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            native_sysroot)

        cls.__imgBiosObj.do_configure_partition(
            part,
            source_params,
            creator,
            cr_workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            native_sysroot)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """

        if ( (not cls.__imgEFIObj) or (not cls.__imgBiosObj) ):
            cls.__instanciateSubClasses()

        cls.__imgEFIObj.do_prepare_partition(
            part,
            source_params,
            creator,
            cr_workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            rootfs_dir,
            native_sysroot)

        cls.__imgBiosObj.do_prepare_partition(
            part,
            source_params,
            creator,
            cr_workdir,
            oe_builddir,
            bootimg_dir,
            kernel_dir,
            rootfs_dir,
            native_sysroot)
