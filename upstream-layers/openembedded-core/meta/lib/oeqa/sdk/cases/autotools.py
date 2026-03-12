#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import tempfile
import subprocess
import unittest

from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class AutotoolsTest(OESDKTestCase):
    """
    Check that autotools will cross-compile correctly.
    """
    def setUp(self):
        libc = self.td.get("TCLIBC")
        if libc in [ 'newlib' ]:
            raise unittest.SkipTest("AutotoolsTest class: SDK doesn't contain a supported C library")

    def test_cpio(self):
        from oe.utils import parallel_make_value
        pmv = parallel_make_value((self.td.get('PARALLEL_MAKE') or '').split())

        with tempfile.TemporaryDirectory(prefix="cpio-", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://ftpmirror.gnu.org/gnu/cpio/cpio-2.15.tar.gz")

            opts = {}
            opts["source"] = os.path.join(testdir, "cpio-2.15")
            opts["build"] = os.path.join(testdir, "build")
            opts["install"] = os.path.join(testdir, "install")
            opts["parallel_make"] = "-j %d" % (pmv) if pmv else ""

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(opts["source"]))
            os.makedirs(opts["build"])

            self._run("cd {build} && {source}/configure CFLAGS='-std=gnu17 -Dbool=int -Dtrue=1 -Dfalse=0 -Wno-error=implicit-function-declaration' $CONFIGURE_FLAGS".format(**opts))

            # Check that configure detected the target correctly
            with open(os.path.join(opts["build"], "config.log")) as f:
                configure_flags= self._run("echo $CONFIGURE_FLAGS")
                host_sys = configure_flags.split("--host=")[1].split()[0]
                self.assertIn(f"host_alias='{host_sys}'\n", f.readlines())

            self._run("cd {build} && make CFLAGS='-std=gnu17 -Dbool=int -Dtrue=1 -Dfalse=0 -Wno-error=implicit-function-declaration' {parallel_make}".format(**opts))
            self._run("cd {build} && make install DESTDIR={install}".format(**opts))

            self.check_elf(os.path.join(opts["install"], "usr", "local", "bin", "cpio"))
