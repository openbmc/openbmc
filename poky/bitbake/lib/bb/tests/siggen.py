#
# BitBake Test for lib/bb/siggen.py
#
# Copyright (C) 2020 Jean-François Dagenais
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import logging
import bb
import time

logger = logging.getLogger('BitBake.TestSiggen')

import bb.siggen

class SiggenTest(unittest.TestCase):

    def test_clean_basepath_simple_target_basepath(self):
        basepath = '/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)

    def test_clean_basepath_basic_virtual_basepath(self):
        basepath = 'virtual:something:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask:virtual:something'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)

    def test_clean_basepath_mc_basepath(self):
        basepath = 'mc:somemachine:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask:mc:somemachine'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)

    def test_clean_basepath_virtual_long_prefix_basepath(self):
        basepath = 'virtual:something:A:B:C:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask:virtual:something:A:B:C'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)

    def test_clean_basepath_mc_virtual_basepath(self):
        basepath = 'mc:somemachine:virtual:something:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask:virtual:something:mc:somemachine'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)

    def test_clean_basepath_mc_virtual_long_prefix_basepath(self):
        basepath = 'mc:X:virtual:something:C:B:A:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask'
        expected_cleaned = 'helloworld/helloworld_1.2.3.bb:do_sometask:virtual:something:C:B:A:mc:X'

        actual_cleaned = bb.siggen.clean_basepath(basepath)

        self.assertEqual(actual_cleaned, expected_cleaned)


    # def test_clean_basepath_performance(self):
    #     input_basepaths = [
    #         'mc:X:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         'mc:X:virtual:something:C:B:A:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         'virtual:something:C:B:A:/different/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         'virtual:something:A:/full/path/to/poky/meta/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         '/this/is/most/common/input/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         '/and/should/be/tested/with/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #         '/more/weight/recipes-whatever/helloworld/helloworld_1.2.3.bb:do_sometask',
    #     ]

    #     time_start = time.time()

    #     i = 2000000
    #     while i >= 0:
    #         for basepath in input_basepaths:
    #             bb.siggen.clean_basepath(basepath)
    #         i -= 1

    #     elapsed = time.time() - time_start
    #     print('{} ({}s)'.format(self.id(), round(elapsed, 3)))

    #     self.assertTrue(False)
