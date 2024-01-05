#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator.data import skipIfNotQemuUsermode
from oeqa.utils.commands import bitbake


class CCppTests(OESelftestTestCase):

    @skipIfNotQemuUsermode()
    def _qemu_usermode(self, recipe_name):
        self.add_command_to_tearDown("bitbake -c clean %s" % recipe_name)
        bitbake("%s -c run_tests" % recipe_name)

    @skipIfNotQemuUsermode()
    def _qemu_usermode_failing(self, recipe_name):
        config = 'PACKAGECONFIG:pn-%s = "failing_test"' % recipe_name
        self.write_config(config)
        self.add_command_to_tearDown("bitbake -c clean %s" % recipe_name)
        result = bitbake("%s -c run_tests" % recipe_name, ignore_status=True)
        self.assertNotEqual(0, result.status, "command: %s is expected to fail but passed, status: %s, output: %s, error: %s" % (
            result.command, result.status, result.output, result.error))


class CMakeTests(CCppTests):
    def test_cmake_qemu(self):
        """Test for cmake-qemu.bbclass good case

        compile the cmake-example and verify the CTests pass in qemu-user.
        qemu-user is configured by CMAKE_CROSSCOMPILING_EMULATOR.
        """
        self._qemu_usermode("cmake-example")

    def test_cmake_qemu_failing(self):
        """Test for cmake-qemu.bbclass bad case

        Break the comparison in the test code and verify the CTests do not pass.
        """
        self._qemu_usermode_failing("cmake-example")


class MesonTests(CCppTests):
    def test_meson_qemu(self):
        """Test the qemu-user feature of the meson.bbclass good case

        compile the meson-example and verify the Unit Test pass in qemu-user.
        qemu-user is configured by meson's exe_wrapper option.
        """
        self._qemu_usermode("meson-example")

    def test_meson_qemu_failing(self):
        """Test the qemu-user feature of the meson.bbclass bad case

        Break the comparison in the test code and verify the Unit Test does not pass in qemu-user.
        """
        self._qemu_usermode_failing("meson-example")
