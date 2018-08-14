# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase

class AnotherIDTest(OETestCase):

    def testAnotherIdGood(self):
        self.assertTrue(True, msg='How is this possible?')

    def testAnotherIdOther(self):
        self.assertTrue(True, msg='How is this possible?')

    def testAnotherIdNone(self):
        self.assertTrue(True, msg='How is this possible?')
