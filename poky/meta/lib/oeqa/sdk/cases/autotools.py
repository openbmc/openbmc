#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import tempfile
import subprocess

from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class AutotoolsTest(OESDKTestCase):
    """
    Check that autotools will cross-compile correctly.
    """
    def test_cpio(self):
        with tempfile.TemporaryDirectory(prefix="cpio-", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://ftp.gnu.org/gnu/cpio/cpio-2.15.tar.gz")

            dirs = {}
            dirs["source"] = os.path.join(testdir, "cpio-2.15")
            dirs["build"] = os.path.join(testdir, "build")
            dirs["install"] = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(dirs["source"]))
            os.makedirs(dirs["build"])

            self._run("cd {build} && {source}/configure $CONFIGURE_FLAGS".format(**dirs))
            self._run("cd {build} && make -j".format(**dirs))
            self._run("cd {build} && make install DESTDIR={install}".format(**dirs))

            self.check_elf(os.path.join(dirs["install"], "usr", "local", "bin", "cpio"))
