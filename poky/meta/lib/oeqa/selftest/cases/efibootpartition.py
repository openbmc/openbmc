# Based on runqemu.py test file
#
# Copyright (c) 2017 Wind River Systems, Inc.
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu
from oeqa.core.decorator.data import skipIfNotMachine
import oe.types

class GenericEFITest(OESelftestTestCase):
    """EFI booting test class"""
    @skipIfNotMachine("qemux86-64", "test is qemux86-64 specific currently")
    def test_boot_efi(self):
        cmd = "runqemu nographic serial wic ovmf"
        if oe.types.qemu_use_kvm(self.td.get('QEMU_USE_KVM', 0), self.td["TARGET_ARCH"]):
            cmd += " kvm"
        image = "core-image-minimal"

        self.write_config("""
EFI_PROVIDER = "systemd-boot"
IMAGE_FSTYPES:pn-%s:append = " wic"
MACHINE_FEATURES:append = " efi"
WKS_FILE = "efi-bootdisk.wks.in"
IMAGE_INSTALL:append = " grub-efi systemd-boot kernel-image-bzimage"
"""
% (image))

        bitbake(image + " ovmf")
        with runqemu(image, ssh=False, launch_cmd=cmd) as qemu:
            self.assertTrue(qemu.runner.logged, "Failed: %s" % cmd)
