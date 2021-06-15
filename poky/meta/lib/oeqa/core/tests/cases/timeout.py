#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from time import sleep

from oeqa.core.case import OETestCase
from oeqa.core.decorator.oetimeout import OETimeout
from oeqa.core.decorator.depends import OETestDepends

class TimeoutTest(OETestCase):

    @OETimeout(1)
    def testTimeoutPass(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETimeout(1)
    def testTimeoutFail(self):
        sleep(2)
        self.assertTrue(True, msg='How is this possible?')


    def testTimeoutSkip(self):
        self.skipTest("This test needs to be skipped, so that testTimeoutDepends()'s OETestDepends kicks in")

    @OETestDepends(["timeout.TimeoutTest.testTimeoutSkip"])
    @OETimeout(3)
    def testTimeoutDepends(self):
        self.assertTrue(False, msg='How is this possible?')

    def testTimeoutUnrelated(self):
        sleep(6)
