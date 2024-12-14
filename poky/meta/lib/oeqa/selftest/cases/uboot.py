# Qemu-based u-boot bootloader integration testing
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu
from oeqa.core.decorator.data import skipIfNotArch
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
