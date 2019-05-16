#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from time import sleep

from oeqa.core.case import OETestCase
from oeqa.core.decorator.oetimeout import OETimeout

class TimeoutTest(OETestCase):

    @OETimeout(1)
    def testTimeoutPass(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETimeout(1)
    def testTimeoutFail(self):
        sleep(2)
        self.assertTrue(True, msg='How is this possible?')
