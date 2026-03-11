# Qemu-based u-boot bootloader integration testing
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_var, get_bb_vars, runCmd
from oeqa.core.decorator.data import skipIfNotArch, skipIfNotBuildArch
from oeqa.core.decorator import OETestTag

uboot_boot_patterns = {
        'search_reached_prompt': "stop autoboot",
        'search_login_succeeded': "=>",
        'search_cmd_finished': "=>"
        }


class UBootTest(OESelftestTestCase):

    @skipIfNotArch(['arm', 'aarch64'])
    @OETestTag("runqemu")
    def test_boot_uboot(self):
        """
        Tests building u-boot and booting it with QEMU
        """

        self.write_config("""
QB_DEFAULT_BIOS = "u-boot.bin"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot"
QEMU_USE_KVM = "False"
""")
        bitbake("virtual/bootloader core-image-minimal")

        with runqemu('core-image-minimal', ssh=False, runqemuparams='nographic',
                     boot_patterns=uboot_boot_patterns) as qemu:

            # test if u-boot console works
            cmd = "version"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(status, 1, msg=output)
            self.assertTrue("U-Boot" in output, msg=output)

    @skipIfNotArch(['aarch64'])
    @skipIfNotBuildArch(['aarch64'])
    @OETestTag("runqemu")
    def test_boot_uboot_kvm_to_full_target(self):
        """
        Tests building u-boot and booting it with QEMU and KVM.
        Requires working KVM on build host. See "kvm-ok" output.
        """

        runCmd("kvm-ok")

        image = "core-image-minimal"
        vars = get_bb_vars(['HOST_ARCH', 'BUILD_ARCH'], image)
        host_arch = vars['HOST_ARCH']
        build_arch = vars['BUILD_ARCH']

        self.assertEqual(host_arch, build_arch, 'HOST_ARCH %s and BUILD_ARCH %s must match for KVM' % (host_arch, build_arch))

        self.write_config("""
QEMU_USE_KVM = "1"

# Using u-boot in EFI mode, need ESP partition for grub/systemd-boot/kernel etc
IMAGE_FSTYPES:pn-core-image-minimal:append = " wic"

# easiest to follow genericarm64 setup with wks file, initrd and EFI loader
INITRAMFS_IMAGE = "core-image-initramfs-boot"
EFI_PROVIDER = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd-boot", "grub-efi", d)}"
WKS_FILE = "genericarm64.wks.in"

# use wic image with ESP for u-boot, not ext4
QB_DEFAULT_FSTYPE = "wic"

PREFERRED_PROVIDER_virtual/bootloader = "u-boot"
QB_DEFAULT_BIOS = "u-boot.bin"

# let u-boot or EFI loader load kernel from ESP
QB_DEFAULT_KERNEL = "none"

# virt pci, not scsi because support not in u-boot to find ESP
QB_DRIVE_TYPE = "/dev/vd"
""")
        bitbake("virtual/bootloader %s" % image)

        runqemu_params = get_bb_var('TEST_RUNQEMUPARAMS', image) or ""
        with runqemu(image, ssh=False, runqemuparams='nographic kvm %s' % runqemu_params) as qemu:

            # boot to target and login worked, should have been fast with kvm
            cmd = "dmesg"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(status, 1, msg=output)
            # Machine is qemu
            self.assertTrue("Machine model: linux,dummy-virt" in output, msg=output)
            # with KVM enabled
            self.assertTrue("KVM: hypervisor services detected" in output, msg=output)
