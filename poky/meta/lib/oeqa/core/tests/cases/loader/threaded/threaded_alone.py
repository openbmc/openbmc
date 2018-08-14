# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase

class ThreadedTestAlone(OETestCase):
    def test_threaded_alone(self):
        self.assertTrue(True, msg='How is this possible?')
