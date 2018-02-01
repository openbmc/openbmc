# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase

class AnotherTest(OETestCase):

    def testAnother(self):
        self.assertTrue(True, msg='How is this possible?')
