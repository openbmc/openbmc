#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag
from oeqa.utils.commands import bitbake, runqemu

class LocalesTest(OESelftestTestCase):

    @OETestTag("runqemu")

    def run_locales_test(self, binary_enabled):
        features = []
        features.append('EXTRA_IMAGE_FEATURES = "empty-root-password allow-empty-password allow-root-login"')
        features.append('IMAGE_INSTALL:append = " glibc-utils localedef"')
        features.append('GLIBC_GENERATE_LOCALES = "en_US.UTF-8 fr_FR.UTF-8 en_US.ISO-8859-1 de_DE.UTF-8 fr_FR.ISO-8859-1 zh_HK.BIG5-HKSCS tr_TR.UTF-8"')
        features.append('IMAGE_LINGUAS:append = " en-us fr-fr"')
        if binary_enabled:
            features.append('ENABLE_BINARY_LOCALE_GENERATION = "1"')
        else:
            features.append('ENABLE_BINARY_LOCALE_GENERATION = "0"')
        self.write_config("\n".join(features))

        # Build a core-image-minimal
        bitbake('core-image-minimal')

        with runqemu("core-image-minimal", ssh=False, runqemuparams='nographic') as qemu:
            cmd = "locale -a"
            status, output = qemu.run_serial(cmd)
            # output must includes fr_FR or fr_FR.UTF-8
            self.assertEqual(status, 1, msg='locale test command failed: output: %s' % output)
            self.assertIn("fr_FR", output, msg='locale -a test failed: output: %s' % output)

            cmd = "localedef --list-archive -v"
            status, output = qemu.run_serial(cmd)
            # output must includes fr_FR.utf8
            self.assertEqual(status, 1, msg='localedef test command failed: output: %s' % output)
            self.assertIn("fr_FR.utf8", output, msg='localedef test failed: output: %s' % output)

    def test_locales_on(self):
        """
        Summary: Test the locales are generated
        Expected: 1. Check the locale exist in the locale-archive
                  2. Check the locale exist for the glibc
                  3. Check the locale can be generated
        Product: oe-core
        Author: Louis Rannou <lrannou@baylibre.com>
        AutomatedBy: Louis Rannou <lrannou@baylibre.com>
        """
        self.run_locales_test(True)

    def test_locales_off(self):
        self.run_locales_test(False)
