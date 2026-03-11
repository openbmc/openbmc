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

    def test_build_pnid(self):
        tests = {
            ('', 'helloworld', 'do_sometask') : 'helloworld:do_sometask',
            ('XX', 'helloworld', 'do_sometask') : 'mc:XX:helloworld:do_sometask',
        }

        for t in tests:
            self.assertEqual(bb.siggen.build_pnid(*t), tests[t])

