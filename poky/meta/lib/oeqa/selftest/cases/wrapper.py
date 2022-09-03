#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class WrapperTests(OESelftestTestCase):
    def test_shebang_wrapper(self):
        """
        Summary:   Build a recipe which will fail if the cmdline_shebang_wrapper function is defective.
        Expected:  Exit status to be 0.
        Author:    Paulo Neves <ptsneves@gmail.com>
        """
        res = bitbake("cmdline-shebang-wrapper-test -c install", ignore_status=False)
