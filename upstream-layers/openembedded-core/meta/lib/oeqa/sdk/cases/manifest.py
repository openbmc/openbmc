#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.case import OESDKTestCase
from oeqa.sdkext.context import OESDKExtTestContext


class ManifestTest(OESDKTestCase):
    def test_manifests(self):
        """
        Verify that the host and target manifests are not empty, unless this is
        a minimal eSDK without toolchain in which case they should be empty.
        """
        if (
            isinstance(self.tc, OESDKExtTestContext)
            and self.td.get("SDK_EXT_TYPE") == "minimal"
            and self.td.get("SDK_INCLUDE_TOOLCHAIN") == "0"
        ):
            self.assertEqual(self.tc.target_pkg_manifest, {})
            self.assertEqual(self.tc.host_pkg_manifest, {})
        else:
            self.assertNotEqual(self.tc.target_pkg_manifest, {})
            self.assertNotEqual(self.tc.host_pkg_manifest, {})
