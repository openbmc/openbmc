#
# SPDX-License-Identifier: MIT
#

import os
import subprocess
import tempfile
import unittest

from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class EpoxyTest(OESDKTestCase):
    """
    Test that Meson builds correctly.
    """
    def setUp(self):
        if not (self.tc.hasHostPackage("nativesdk-meson")):
            raise unittest.SkipTest("GalculatorTest class: SDK doesn't contain Meson")

    def test_epoxy(self):
        with tempfile.TemporaryDirectory(prefix="epoxy", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://github.com/anholt/libepoxy/releases/download/1.5.3/libepoxy-1.5.3.tar.xz")

            dirs = {}
            dirs["source"] = os.path.join(testdir, "libepoxy-1.5.3")
            dirs["build"] = os.path.join(testdir, "build")
            dirs["install"] = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir])
            self.assertTrue(os.path.isdir(dirs["source"]))
            os.makedirs(dirs["build"])

            log = self._run("meson -Degl=no -Dglx=no -Dx11=false {build} {source}".format(**dirs))
            # Check that Meson thinks we're doing a cross build and not a native
            self.assertIn("Build type: cross build", log)
            self._run("ninja -C {build} -v".format(**dirs))
            self._run("DESTDIR={install} ninja -C {build} -v install".format(**dirs))

            self.check_elf(os.path.join(dirs["install"], "usr", "local", "lib", "libepoxy.so"))
