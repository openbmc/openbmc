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

class BuildAssimp(OESDKTestCase):
    """
    Test case to build a project using cmake.
    """

    def setUp(self):
        if not (self.tc.hasHostPackage("nativesdk-cmake") or
                self.tc.hasHostPackage("cmake-native")):
            raise unittest.SkipTest("Needs cmake")

    def test_assimp(self):
        with tempfile.TemporaryDirectory(prefix="assimp", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://github.com/assimp/assimp/archive/v4.1.0.tar.gz")

            dirs = {}
            dirs["source"] = os.path.join(testdir, "assimp-4.1.0")
            dirs["build"] = os.path.join(testdir, "build")
            dirs["install"] = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(dirs["source"]))
            os.makedirs(dirs["build"])

            self._run("cd {build} && cmake -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON {source}".format(**dirs))
            self._run("cmake --build {build} -- -j".format(**dirs))
            self._run("cmake --build {build} --target install -- DESTDIR={install}".format(**dirs))
            self.check_elf(os.path.join(dirs["install"], "usr", "local", "lib", "libassimp.so.4.1.0"))
