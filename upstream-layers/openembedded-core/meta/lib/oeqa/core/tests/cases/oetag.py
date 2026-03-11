#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from oeqa.core.case import OETestCase
from oeqa.core.decorator import OETestTag

class TagTest(OETestCase):
    @OETestTag('goodTag')
    def testTagGood(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestTag('otherTag')
    def testTagOther(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestTag('otherTag', 'multiTag')
    def testTagOtherMulti(self):
        self.assertTrue(True, msg='How is this possible?')

    def testTagNone(self):
        self.assertTrue(True, msg='How is this possible?')

@OETestTag('classTag')
class TagClassTest(OETestCase):
    @OETestTag('otherTag')
    def testTagOther(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestTag('otherTag', 'multiTag')
    def testTagOtherMulti(self):
        self.assertTrue(True, msg='How is this possible?')

    def testTagNone(self):
        self.assertTrue(True, msg='How is this possible?')

