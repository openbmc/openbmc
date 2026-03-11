#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import glob
import os
import shutil
from oeqa.utils.commands import bitbake, get_test_layer
from oeqa.selftest.case import OESelftestTestCase

class Pseudo(OESelftestTestCase):

    def test_pseudo_pyc_creation(self):
        self.write_config("")

        metaselftestpath = get_test_layer()
        pycache_path = os.path.join(metaselftestpath, 'lib/__pycache__')
        if os.path.exists(pycache_path):
            shutil.rmtree(pycache_path)

        bitbake('pseudo-pyc-test -c install')

        test1_pyc_present = len(glob.glob(os.path.join(pycache_path, 'pseudo_pyc_test1.*.pyc')))
        self.assertTrue(test1_pyc_present, 'test1 pyc file missing, should be created outside of pseudo context.')

        test2_pyc_present = len(glob.glob(os.path.join(pycache_path, 'pseudo_pyc_test2.*.pyc')))
        self.assertFalse(test2_pyc_present, 'test2 pyc file present, should not be created in pseudo context.')
