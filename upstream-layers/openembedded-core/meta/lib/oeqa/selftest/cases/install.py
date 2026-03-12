#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class InstallTests(OESelftestTestCase):
    """Test installability of recipes which use complex dynamic packaging"""

    def do_installation_test(self, packages, extra_config=""):
        self.write_config('CORE_IMAGE_EXTRA_INSTALL = "{}"\n{}'.format(packages, extra_config))
        bitbake("core-image-minimal")

    def test_gstreamer_plugins(self):
        """Ensure that all gstreamer plugins can be installed together"""
        self.do_installation_test("gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
                                   gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly",
                                  extra_config='LICENSE_FLAGS_ACCEPTED += "commercial"')

    def test_linux_firmware(self):
        """Ensure that all linux-firmware packages can be installed together"""
        self.do_installation_test("linux-firmware")

    def test_linux_modules(self):
        """Ensure that all in-tree kernel modules can be installed together"""
        self.do_installation_test("kernel-modules")

    def test_perl(self):
        """Ensure that all built-in perl modules can be installed together"""
        self.do_installation_test("perl perl-modules")

    def test_python(self):
        """Ensure that all built-in python modules can be installed together"""
        self.do_installation_test("python3 python3-modules python3-misc")
