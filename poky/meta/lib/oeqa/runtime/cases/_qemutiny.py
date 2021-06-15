#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase

class QemuTinyTest(OERuntimeTestCase):

    def test_boot_tiny(self):
        status, output = self.target.run_serial('uname -a')
        msg = "Cannot detect poky tiny boot!"
        self.assertTrue("yocto-tiny" in output, msg)
