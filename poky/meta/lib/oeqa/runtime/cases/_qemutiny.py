#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.target.qemu import OEQemuTarget

class QemuTinyTest(OERuntimeTestCase):

    def test_boot_tiny(self):
        # Until the target has explicit run_serial support, check that the
        # target is the qemu runner
        if isinstance(self.target, OEQemuTarget):
            status, output = self.target.runner.run_serial('uname -a')
            self.assertIn("Linux", output)
        else:
            self.skipTest("Target %s is not OEQemuTarget" % self.target)
