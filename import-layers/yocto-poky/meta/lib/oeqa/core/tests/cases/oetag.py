# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase
from oeqa.core.decorator.oetag import OETestTag

class TagTest(OETestCase):

    @OETestTag('goodTag')
    def testTagGood(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestTag('otherTag')
    def testTagOther(self):
        self.assertTrue(True, msg='How is this possible?')

    def testTagNone(self):
        self.assertTrue(True, msg='How is this possible?')
