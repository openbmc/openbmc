#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os.path

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars

class SDKTests(OESelftestTestCase):

    def load_manifest(self, filename):
        manifest = {}
        with open(filename) as f:
            for line in f:
                name, arch, version = line.split(maxsplit=3)
                manifest[name] = (version, arch)
        return manifest

    def test_sdk_manifests(self):
        image = "core-image-minimal"

        self.write_config("""
TOOLCHAIN_HOST_TASK:append = " nativesdk-selftest-hello"
IMAGE_INSTALL:append = " selftest-hello"
""")

        bitbake(f"{image} -c populate_sdk")
        vars = get_bb_vars(['SDK_DEPLOY', 'TOOLCHAIN_OUTPUTNAME'], image)

        path = os.path.join(vars["SDK_DEPLOY"], vars["TOOLCHAIN_OUTPUTNAME"] + ".host.manifest")
        self.assertNotEqual(os.path.getsize(path), 0, msg="Host manifest is empty")
        self.assertIn("nativesdk-selftest-hello", self.load_manifest(path))

        path = os.path.join(vars["SDK_DEPLOY"], vars["TOOLCHAIN_OUTPUTNAME"] + ".target.manifest")
        self.assertNotEqual(os.path.getsize(path), 0, msg="Target manifest is empty")
        self.assertIn("selftest-hello", self.load_manifest(path))
