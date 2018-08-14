# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase

class ThreadedTestModule(OETestCase):
    def test_threaded_module(self):
        self.assertTrue(True, msg='How is this possible?')

class ThreadedTestModule2(OETestCase):
    def test_threaded_module2(self):
        self.assertTrue(True, msg='How is this possible?')
