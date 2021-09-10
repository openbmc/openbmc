#
# SPDX-License-Identifier: MIT
#

import os.path
from oeqa.sdk.case import OESDKTestCase

class GccTests(OESDKTestCase):
    def test_verify_specs(self):
        """
        Verify that the compiler has been relocated successfully and isn't
        looking in the hard-coded prefix.
        """
        # Canonicalise the SDK root
        sdk_base = os.path.realpath(self.tc.sdk_dir)
        # Canonicalise the location of GCC
        gcc_path = os.path.realpath(self._run("command -v gcc").strip())
        # Skip the test if the GCC didn't come from the buildtools, as it only
        # comes with buildtools-extended-tarball.
        if os.path.commonprefix((sdk_base, gcc_path)) != sdk_base:
            self.skipTest("Buildtools does not provide GCC")

        # This is the prefix that GCC is build with, and should be replaced at
        # installation time.
        sdkpath = self.td.get("SDKPATH")
        self.assertTrue(sdkpath)

        for line in self._run('gcc -dumpspecs').splitlines():
            self.assertNotIn(sdkpath, line)
