# Qemu-based barebox bootloader integration testing
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu
from oeqa.core.decorator.data import skipIfNotArch
from oeqa.core.decorator import OETestTag

barebox_boot_patterns = {
        'search_reached_prompt': r"stop autoboot",
        'search_login_succeeded': r"barebox@[^:]+:[^ ]+ ",
        'search_cmd_finished': r"barebox@[a-zA-Z0-9\-\s]+:/"
        }


class BareboxTest(OESelftestTestCase):

    @skipIfNotArch(['arm', 'aarch64'])
    @OETestTag("runqemu")
    def test_boot_barebox(self):
        """
        Tests building barebox and booting it with QEMU
        """

        self.write_config("""
QB_DEFAULT_KERNEL = "barebox-dt-2nd.img"
PREFERRED_PROVIDER_virtual/bootloader = "barebox"
QEMU_USE_KVM = "False"
""")

        bitbake("virtual/bootloader core-image-minimal")

        with runqemu('core-image-minimal', ssh=False, runqemuparams='nographic',
                     boot_patterns=barebox_boot_patterns) as qemu:

            # test if barebox console works
            cmd = "version"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(status, 1, msg=output)
            self.assertTrue("barebox" in output, msg=output)
