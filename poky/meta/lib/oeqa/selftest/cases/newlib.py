#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class NewlibTest(OESelftestTestCase):
    def test_newlib(self):
        self.write_config('TCLIBC = "newlib"')
        bitbake("newlib libgloss")
