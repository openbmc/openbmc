#!/usr/bin/env python3

# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import unittest
import logging
import os

from common import setup_sys_path, TestBase
setup_sys_path()

from oeqa.core.exception import OEQAMissingVariable
from oeqa.core.utils.test import getCaseMethod, getSuiteCasesNames

class TestData(TestBase):
    modules = ['data']

    def test_data_fail_missing_variable(self):
        expectedException = "oeqa.core.exception.OEQAMissingVariable"

        tc = self._testLoader(modules=self.modules)
        self.assertEqual(False, tc.runTests().wasSuccessful())
        for test, data in tc.errors:
            expect = False
            if expectedException in data:
                expect = True

            self.assertTrue(expect)

    def test_data_fail_wrong_variable(self):
        expectedError = 'AssertionError'
        d = {'IMAGE' : 'core-image-sato', 'ARCH' : 'arm'}

        tc = self._testLoader(d=d, modules=self.modules)
        self.assertEqual(False, tc.runTests().wasSuccessful())
        for test, data in tc.failures:
            expect = False
            if expectedError in data:
                expect = True

            self.assertTrue(expect)

    def test_data_ok(self):
        d = {'IMAGE' : 'core-image-minimal', 'ARCH' : 'x86', 'MACHINE' : 'qemuarm'}

        tc = self._testLoader(d=d, modules=self.modules)
        self.assertEqual(True, tc.runTests().wasSuccessful())

if __name__ == '__main__':
    unittest.main()
