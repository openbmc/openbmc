#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import unittest
from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class Python3Test(OESDKTestCase):
    def setUp(self):
        if not (self.tc.hasHostPackage("nativesdk-python3-core") or
                self.tc.hasHostPackage("python3-core-native")):
            raise unittest.SkipTest("No python3 package in the SDK")

    def test_python3(self):
        cmd = "python3 -c \"import codecs; print(codecs.encode('Uryyb, jbeyq', 'rot13'))\""
        output = self._run(cmd)
        self.assertEqual(output, "Hello, world\n")
