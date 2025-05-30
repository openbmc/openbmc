#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import subprocess
import tempfile

from oeqa.sdk.cases.meson import MesonTestBase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class GTK3Test(MesonTestBase):

    def setUp(self):
        super().setUp()
        self.ensure_target_package("gtk+3", "libgtk-3.0", recipe="gtk+3")
        self.ensure_host_package("glib-2.0-utils", "libglib-2.0-utils", recipe="glib-2.0")

    """
    Test that autotools and GTK+ 3 compiles correctly.
    """
    def test_libhandy(self):
        with tempfile.TemporaryDirectory(prefix="libhandy", dir=self.tc.sdk_dir) as testdir:
            tarball = self.fetch(testdir, self.td["DL_DIR"], "https://download.gnome.org/sources/libhandy/1.8/libhandy-1.8.3.tar.xz")

            sourcedir = os.path.join(testdir, "libhandy-1.8.3")
            builddir = os.path.join(testdir, "build")
            installdir = os.path.join(testdir, "install")

            subprocess.check_output(["tar", "xf", tarball, "-C", testdir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(sourcedir))
            os.makedirs(builddir)

            self.build_meson(sourcedir, builddir, installdir, "-Dglade_catalog=disabled -Dintrospection=disabled -Dvapi=false")
            self.assertTrue(os.path.isdir(installdir))
            self.check_elf(os.path.join(installdir, "usr", "local", "lib", "libhandy-1.so"))
