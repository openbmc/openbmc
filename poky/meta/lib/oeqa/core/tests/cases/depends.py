# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase
from oeqa.core.decorator.depends import OETestDepends

class DependsTest(OETestCase):

    def testDependsFirst(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsFirst'])
    def testDependsSecond(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsSecond'])
    def testDependsThird(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsSecond'])
    def testDependsFourth(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsThird', 'testDependsFourth'])
    def testDependsFifth(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsCircular3'])
    def testDependsCircular1(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsCircular1'])
    def testDependsCircular2(self):
        self.assertTrue(True, msg='How is this possible?')

    @OETestDepends(['testDependsCircular2'])
    def testDependsCircular3(self):
        self.assertTrue(True, msg='How is this possible?')
