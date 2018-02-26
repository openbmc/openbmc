# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase
from oeqa.core.decorator.depends import OETestDepends

class ThreadedTest3(OETestCase):
    @OETestDepends(['threaded.ThreadedTest.test_threaded_no_depends'])
    def test_threaded_depends(self):
        self.assertTrue(True, msg='How is this possible?')
