#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from oeqa.core.case import OETestCase
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import OETestDataDepends

class DataTest(OETestCase):
    data_vars = ['IMAGE', 'ARCH']

    @OETestDataDepends(['MACHINE',])
    @OETestTag('dataTestOk')
    def testDataOk(self):
        self.assertEqual(self.td.get('IMAGE'), 'core-image-minimal')
        self.assertEqual(self.td.get('ARCH'), 'x86')
        self.assertEqual(self.td.get('MACHINE'), 'qemuarm')

    @OETestTag('dataTestFail')
    def testDataFail(self):
        pass
