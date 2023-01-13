
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class BaremetalTest(OESelftestTestCase):
    def test_baremetal(self):
        self.write_config('TCLIBC = "baremetal"')
        bitbake('baremetal-helloworld')
