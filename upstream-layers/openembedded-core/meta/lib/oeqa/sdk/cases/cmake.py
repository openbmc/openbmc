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

class CMakeTest(OESDKTestCase):
    """
    Test case to build a project using cmake.
    """

    def setUp(self):
        libc = self.td.get("TCLIBC")
        if libc in [ 'newlib' ]:
            raise unittest.SkipTest("CMakeTest class: SDK doesn't contain a supported C library")

        self.ensure_host_package("cmake")

    def test_assimp(self):
        from oe.utils import parallel_make_value
        pmv = parallel_make_value((self.td.get('PARALLEL_MAKE') or '').split())

        with tempfile.TemporaryDirectory(prefix="assimp", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://github.com/assimp/assimp/archive/v5.4.1.tar.gz")

            opts = {}
            opts["source"] = os.path.join(testdir, "assimp-5.4.1")
            opts["build"] = os.path.join(testdir, "build")
            opts["install"] = os.path.join(testdir, "install")
            opts["parallel_make"] = "-j %d" % (pmv) if pmv else ""

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(opts["source"]))
            # Apply the zlib patch https://github.com/madler/zlib/commit/a566e156b3fa07b566ddbf6801b517a9dba04fa3
            # this sed wont be needed once assimp moves its zlib copy to v1.3.1+
            self._run("sed -i '/#  ifdef _FILE_OFFSET_BITS/I,+2 d' {source}/contrib/zlib/gzguts.h".format(**opts))
            os.makedirs(opts["build"])

            self._run("cd {build} && cmake -DASSIMP_WARNINGS_AS_ERRORS=OFF -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON -DASSIMP_BUILD_ZLIB=ON {source}".format(**opts))
            self._run("cmake --build {build} -- {parallel_make}".format(**opts))
            self._run("cmake --build {build} --target install -- DESTDIR={install}".format(**opts))
            self.check_elf(os.path.join(opts["install"], "usr", "local", "lib", "libassimp.so.5.4.1"))
