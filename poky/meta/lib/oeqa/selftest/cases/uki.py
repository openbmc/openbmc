# Based on runqemu.py test file
#
# Copyright (c) 2017 Wind River Systems, Inc.
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_var
from oeqa.core.decorator.data import skipIfNotArch
from oeqa.core.decorator import OETestTag
import oe.types

class UkiTest(OESelftestTestCase):
    """Boot Unified Kernel Image (UKI) generated with uki.bbclass on UEFI firmware (omvf/edk2)"""

    @skipIfNotArch(['i586', 'i686', 'x86_64'])
    @OETestTag("runqemu")
    def test_uki_boot_systemd(self):
        """Build and boot into UEFI firmware (omvf/edk2), systemd-boot, initrd without systemd, rootfs with systemd"""
        image = "core-image-minimal"
        runqemu_params = get_bb_var('TEST_RUNQEMUPARAMS', image) or ""
        cmd = "runqemu %s nographic serial wic ovmf" % (runqemu_params)
        if oe.types.qemu_use_kvm(self.td.get('QEMU_USE_KVM', 0), self.td["TARGET_ARCH"]):
            cmd += " kvm"

        self.write_config("""
# efi firmware must load systemd-boot, not grub
EFI_PROVIDER = "systemd-boot"

# image format must be wic, needs esp partition for firmware etc
IMAGE_FSTYPES:pn-%s:append = " wic"
WKS_FILE = "efi-uki-bootdisk.wks.in"

# efi, uki and systemd features must be enabled
INIT_MANAGER = "systemd"
MACHINE_FEATURES:append = " efi"
IMAGE_CLASSES:append:pn-core-image-minimal = " uki"

# uki embeds also an initrd
INITRAMFS_IMAGE = "core-image-minimal-initramfs"

# runqemu must not load kernel separately, it's in the uki
QB_KERNEL_ROOT = ""
QB_DEFAULT_KERNEL = "none"

# boot command line provided via uki, not via bootloader
UKI_CMDLINE = "rootwait root=LABEL=root console=${KERNEL_CONSOLE}"

# disable kvm, breaks boot
QEMU_USE_KVM = ""

IMAGE_CLASSES:remove = 'testimage'
""" % (image))

        uki_filename = get_bb_var('UKI_FILENAME', image)

        bitbake(image + " ovmf")
        with runqemu(image, ssh=False, launch_cmd=cmd) as qemu:
            self.assertTrue(qemu.runner.logged, "Failed: %s" % cmd)

            # Verify from efivars that firmware was:
            # x86_64, qemux86_64, ovmf = edk2
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderFirmwareInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep 'EDK II'"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that systemd-boot was the loader
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep systemd-boot"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that systemd-stub was used
            cmd = "echo $( cat /sys/firmware/efi/efivars/StubInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep systemd-stub"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that the compiled uki file was booted into
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderEntrySelected-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep '%s'" % (uki_filename)
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

    @skipIfNotArch(['i586', 'i686', 'x86_64'])
    @OETestTag("runqemu")
    def test_uki_sysvinit(self):
        """Build and boot into UEFI firmware (omvf/edk2), systemd-boot, initrd with sysvinit, rootfs with sysvinit"""
        config = """
# efi firmware must load systemd-boot, not grub
EFI_PROVIDER = "systemd-boot"

# image format must be wic, needs esp partition for firmware etc
IMAGE_FSTYPES:pn-core-image-base:append = " wic"
WKS_FILE = "efi-uki-bootdisk.wks.in"

# efi, uki and systemd features must be enabled
MACHINE_FEATURES:append = " efi"
DISTRO_FEATURES_NATIVE:append = " systemd"
IMAGE_CLASSES:append:pn-core-image-base = " uki"

# uki embeds also an initrd, no systemd or udev
INITRAMFS_IMAGE = "core-image-initramfs-boot"

# runqemu must not load kernel separately, it's in the uki
QB_KERNEL_ROOT = ""
QB_DEFAULT_KERNEL = "none"

# boot command line provided via uki, not via bootloader
UKI_CMDLINE = "rootwait root=LABEL=root console=${KERNEL_CONSOLE}"

# disable kvm, breaks boot
QEMU_USE_KVM = ""

IMAGE_CLASSES:remove = 'testimage'
"""
        self.append_config(config)
        bitbake('core-image-base ovmf')
        runqemu_params = get_bb_var('TEST_RUNQEMUPARAMS', 'core-image-base') or ""
        uki_filename = get_bb_var('UKI_FILENAME', 'core-image-base')
        self.remove_config(config)

        with runqemu('core-image-base', ssh=False,
                     runqemuparams='%s slirp nographic ovmf' % (runqemu_params), image_fstype='wic') as qemu:
            # Verify from efivars that firmware was:
            # x86_64, qemux86_64, ovmf = edk2
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderFirmwareInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep 'EDK II'"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that systemd-boot was the loader
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep systemd-boot"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that systemd-stub was used
            cmd = "echo $( cat /sys/firmware/efi/efivars/StubInfo-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep systemd-stub"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))

            # Check that the compiled uki file was booted into
            cmd = "echo $( cat /sys/firmware/efi/efivars/LoaderEntrySelected-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f ) | grep '%s'" % (uki_filename)
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
