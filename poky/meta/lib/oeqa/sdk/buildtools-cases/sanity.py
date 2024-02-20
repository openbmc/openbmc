#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import shutil
import os.path
from oeqa.sdk.case import OESDKTestCase

class SanityTests(OESDKTestCase):
    def test_tools(self):
        """
        Test that wget and tar come from the buildtools, not the host. This
        verifies that the buildtools have installed correctly. We can't check
        for gcc as that is only installed by buildtools-extended.
        """
        for command in ("tar", "wget"):
            # Canonicalise the SDK root
            sdk_base = os.path.realpath(self.tc.sdk_dir)
            # Canonicalise the location of this command
            tool_path = os.path.realpath(self._run("command -v %s" % command).strip())
            # Assert that the tool was found inside the SDK root
            self.assertEqual(os.path.commonprefix((sdk_base, tool_path)), sdk_base)
