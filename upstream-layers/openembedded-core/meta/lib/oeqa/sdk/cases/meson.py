#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import json
import os
import subprocess
import tempfile
import unittest

from oeqa.sdk.case import OESDKTestCase
from oeqa.sdkext.context import OESDKExtTestContext
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class MesonTestBase(OESDKTestCase):
    def setUp(self):
        libc = self.td.get("TCLIBC")
        if libc in [ 'newlib' ]:
            raise unittest.SkipTest("MesonTest class: SDK doesn't contain a supported C library")

        if isinstance(self.tc, OESDKExtTestContext):
            self.skipTest(f"{self.id()} does not support eSDK (https://bugzilla.yoctoproject.org/show_bug.cgi?id=15854)")

        self.ensure_host_package("meson")
        self.ensure_host_package("pkgconfig")

    def build_meson(self, sourcedir, builddir, installdir=None, options=""):
        """
        Given a source tree in sourcedir, configure it to build in builddir with
        the specified options, and if installdir is set also install.
        """
        log = self._run(f"meson setup --warnlevel 1 {builddir} {sourcedir} {options}")

        # Check that the build directory now exists
        self.assertTrue(os.path.isdir(builddir))

        # Check that Meson thinks we're doing a cross build and not a native
        self.assertIn("Build type: cross build", log)

        # Check that the cross-compiler used is the one we set.
        data = json.loads(self._run(f"meson introspect --compilers {builddir}"))
        self.assertIn(self._run("echo $CC").split()[0], data["host"]["c"]["exelist"])

        # Check that the target architectures was set correctly.
        data = json.loads(self._run(f"meson introspect --machines {builddir}"))
        self.assertEqual(data["host"]["cpu"], self._run("echo -n $OECORE_MESON_HOST_CPU"))

        self._run(f"meson compile -C {builddir} -v")

        if installdir:
            self._run(f"meson install -C {builddir} --destdir {installdir}")
            # Check that the install directory now exists
            self.assertTrue(os.path.isdir(installdir))

class MesonTest(MesonTestBase):
    """
    Test that Meson builds correctly.
    """

    def test_iputils(self):
        with tempfile.TemporaryDirectory(prefix="iputils", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://github.com/iputils/iputils/releases/download/20250605/iputils-20250605.tar.gz")

            sourcedir = os.path.join(testdir, "iputils-20250605")
            builddir = os.path.join(testdir, "build")
            installdir = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(sourcedir))

            self.build_meson(sourcedir, builddir, installdir, "-DUSE_CAP=false -DUSE_IDN=false")
            self.check_elf(os.path.join(installdir, "usr", "local", "bin", "ping"))
