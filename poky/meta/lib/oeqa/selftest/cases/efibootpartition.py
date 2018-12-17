# Based on runqemu.py test file
#
# Copyright (c) 2017 Wind River Systems, Inc.
#

import re

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_var

class GenericEFITest(OESelftestTestCase):
    """EFI booting test class"""

    cmd_common = "runqemu nographic serial wic ovmf"
    efi_provider = "systemd-boot"
    image = "core-image-minimal"
    machine = "qemux86-64"
    recipes_built = False

    @classmethod
    def setUpLocal(self):
        super(GenericEFITest, self).setUpLocal(self)

        self.write_config(self,
"""
EFI_PROVIDER = "%s"
IMAGE_FSTYPES_pn-%s_append = " wic"
MACHINE = "%s"
MACHINE_FEATURES_append = " efi"
WKS_FILE = "efi-bootdisk.wks.in"
IMAGE_INSTALL_append = " grub-efi systemd-boot kernel-image-bzimage"
"""
% (self.efi_provider, self.image, self.machine))
        if not self.recipes_built:
            bitbake("ovmf")
            bitbake(self.image)
            self.recipes_built = True

    @classmethod
    def test_boot_efi(self):
        """Test generic boot partition with qemu"""
        cmd = "%s %s" % (self.cmd_common, self.machine)
        with runqemu(self.image, ssh=False, launch_cmd=cmd) as qemu:
            self.assertTrue(qemu.runner.logged, "Failed: %s" % cmd)
