#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os, tempfile, subprocess
import unittest
from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class MakefileTest(OESDKTestCase):
    """
    Test that "plain" compilation works, using just $CC $CFLAGS etc.
    """
    def setUp(self):
        libc = self.td.get("TCLIBC")
        if libc in [ 'newlib' ]:
            raise unittest.SkipTest("MakefileTest class: SDK doesn't contain a supported C library")

    def test_lzip(self):
        from oe.utils import parallel_make_value
        pmv = parallel_make_value((self.td.get('PARALLEL_MAKE') or '').split())

        with tempfile.TemporaryDirectory(prefix="lzip", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "http://downloads.yoctoproject.org/mirror/sources/lzip-1.19.tar.gz")

            opts = {}
            opts["source"] = os.path.join(testdir, "lzip-1.19")
            opts["build"] = os.path.join(testdir, "build")
            opts["install"] = os.path.join(testdir, "install")
            opts["parallel_make"] = "-j %d" % (pmv) if pmv else ""

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(opts["source"]))
            os.makedirs(opts["build"])

            cmd = """cd {build} && \
                     {source}/configure --srcdir {source} \
                     CXX="$CXX" \
                     CPPFLAGS="$CPPFLAGS" \
                     CXXFLAGS="$CXXFLAGS" \
                     LDFLAGS="$LDFLAGS" \
                  """
            self._run(cmd.format(**opts))
            self._run("cd {build} && make {parallel_make}".format(**opts))
            self._run("cd {build} && make install DESTDIR={install}".format(**opts))
            self.check_elf(os.path.join(opts["install"], "usr", "local", "bin", "lzip"))
