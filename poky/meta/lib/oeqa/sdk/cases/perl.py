#
# SPDX-License-Identifier: MIT
#

import unittest
from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class PerlTest(OESDKTestCase):
    def setUp(self):
        if not (self.tc.hasHostPackage("nativesdk-perl") or
                self.tc.hasHostPackage("perl-native")):
            raise unittest.SkipTest("No perl package in the SDK")

    def test_perl(self):
        cmd = "perl -e '$_=\"Uryyb, jbeyq\"; tr/a-zA-Z/n-za-mN-ZA-M/;print'"
        output = self._run(cmd)
        self.assertEqual(output, "Hello, world")
