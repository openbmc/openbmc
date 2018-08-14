# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase

class ThreadedTest(OETestCase):
    def test_threaded_no_depends(self):
        self.assertTrue(True, msg='How is this possible?')

class ThreadedTest2(OETestCase):
    def test_threaded_same_module(self):
        self.assertTrue(True, msg='How is this possible?')
