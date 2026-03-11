#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class Python3Test(OESDKTestCase):
    def setUp(self):
        self.ensure_host_package("python3-core", recipe="python3")

    def test_python3(self):
        cmd = "python3 -c \"import codecs; print(codecs.encode('Uryyb, jbeyq', 'rot13'))\""
        output = self._run(cmd)
        self.assertEqual(output, "Hello, world\n")
