#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.utils.commands import bitbake, get_bb_var
from oeqa.selftest.case import OESelftestTestCase

class PokyBleeding(OESelftestTestCase):

    def test_poky_bleeding_autorev(self):
        """
        Test that poky-bleeding.bbclass sets SRCREV to "AUTOINC" for recipe
        with a single scm in SRC_URI and for recipe with two scm's in SRC_URI.
        """

        self.assertNotEqual( get_bb_var('SRCREV', 'pseudo'), "AUTOINC")

        self.assertNotEqual( get_bb_var('SRCREV', 'hello-rs'), "AUTOINC")
        self.assertNotEqual( get_bb_var('SRCREV_hello-lib', 'hello-rs'), "AUTOINC")
        
        features = '''
INHERIT += "poky-bleeding"
POKY_AUTOREV_RECIPES = "hello-rs pseudo"
'''
        self.write_config(features)

        self.assertEqual( get_bb_var('SRCREV', 'pseudo'), "AUTOINC")

        self.assertEqual( get_bb_var('SRCREV', 'hello-rs'), "AUTOINC")
        self.assertEqual( get_bb_var('SRCREV_hello-lib', 'hello-rs'), "AUTOINC")
