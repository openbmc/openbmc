#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class BitbakeTests(OESelftestTestCase):

    def test_rpm_filenames(self):
        test_recipe = "testrpm"
        bitbake(test_recipe)
