# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase
from oeqa.core.decorator.oeid import OETestID

class IDTest(OETestCase):

    @OETestID(101)
    def testIdGood(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestID(102)
    def testIdOther(self):
        self.assertTrue(True, msg='How is this possible?')

    def testIdNone(self):
        self.assertTrue(True, msg='How is this possible?')
