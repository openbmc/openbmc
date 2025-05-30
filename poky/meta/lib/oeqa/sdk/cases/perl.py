#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class PerlTest(OESDKTestCase):
    def setUp(self):
        self.ensure_host_package("perl")

    def test_perl(self):
        cmd = "perl -e '$_=\"Uryyb, jbeyq\"; tr/a-zA-Z/n-za-mN-ZA-M/;print'"
        output = self._run(cmd)
        self.assertEqual(output, "Hello, world")
