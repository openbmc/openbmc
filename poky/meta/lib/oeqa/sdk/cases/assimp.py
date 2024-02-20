#
# Copyright OpenEmbedded Contributors
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
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://github.com/assimp/assimp/archive/v5.3.1.tar.gz")

            dirs = {}
            dirs["source"] = os.path.join(testdir, "assimp-5.3.1")
            dirs["build"] = os.path.join(testdir, "build")
            dirs["install"] = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(dirs["source"]))
            # Apply the zlib patch https://github.com/madler/zlib/commit/a566e156b3fa07b566ddbf6801b517a9dba04fa3
            # this sed wont be needed once assimp moves its zlib copy to v1.3.1+
            self._run("sed -i '/#  ifdef _FILE_OFFSET_BITS/I,+2 d' {source}/contrib/zlib/gzguts.h".format(**dirs))
            os.makedirs(dirs["build"])

            self._run("cd {build} && cmake -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON -DASSIMP_BUILD_ZLIB=ON {source}".format(**dirs))
            self._run("cmake --build {build} -- -j".format(**dirs))
            self._run("cmake --build {build} --target install -- DESTDIR={install}".format(**dirs))
            self.check_elf(os.path.join(dirs["install"], "usr", "local", "lib", "libassimp.so.5.3.0"))
