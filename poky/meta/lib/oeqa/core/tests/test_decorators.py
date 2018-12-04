#!/usr/bin/env python3

# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import signal
import unittest

from common import setup_sys_path, TestBase
setup_sys_path()

from oeqa.core.exception import OEQADependency
from oeqa.core.utils.test import getCaseMethod, getSuiteCasesNames, getSuiteCasesIDs

class TestFilterDecorator(TestBase):

    def _runFilterTest(self, modules, filters, expect, msg):
        tc = self._testLoader(modules=modules, filters=filters)
        test_loaded = set(getSuiteCasesNames(tc.suites))
        self.assertEqual(expect, test_loaded, msg=msg)

    def test_oetag(self):
        # Get all cases without filtering.
        filter_all = {}
        test_all = {'testTagGood', 'testTagOther', 'testTagNone'}
        msg_all = 'Failed to get all oetag cases without filtering.'

        # Get cases with 'goodTag'.
        filter_good = {'oetag':'goodTag'}
        test_good = {'testTagGood'}
        msg_good = 'Failed to get just one test filtering with "goodTag" oetag.'

        # Get cases with an invalid tag.
        filter_invalid = {'oetag':'invalidTag'}
        test_invalid = set()
        msg_invalid = 'Failed to filter all test using an invalid oetag.'

        tests = ((filter_all, test_all, msg_all),
                 (filter_good, test_good, msg_good),
                 (filter_invalid, test_invalid, msg_invalid))

        for test in tests:
            self._runFilterTest(['oetag'], test[0], test[1], test[2])

    def test_oeid(self):
        # Get all cases without filtering.
        filter_all = {}
        test_all = {'testIdGood', 'testIdOther', 'testIdNone'}
        msg_all = 'Failed to get all oeid cases without filtering.'

        # Get cases with '101' oeid.
        filter_good = {'oeid': 101}
        test_good = {'testIdGood'}
        msg_good = 'Failed to get just one tes filtering with "101" oeid.'

        # Get cases with an invalid id.
        filter_invalid = {'oeid':999}
        test_invalid = set()
        msg_invalid = 'Failed to filter all test using an invalid oeid.'

        tests = ((filter_all, test_all, msg_all),
                 (filter_good, test_good, msg_good),
                 (filter_invalid, test_invalid, msg_invalid))

        for test in tests:
            self._runFilterTest(['oeid'], test[0], test[1], test[2])

class TestDependsDecorator(TestBase):
    modules = ['depends']

    def test_depends_order(self):
        tests =  ['depends.DependsTest.testDependsFirst',
                  'depends.DependsTest.testDependsSecond',
                  'depends.DependsTest.testDependsThird',
                  'depends.DependsTest.testDependsFourth',
                  'depends.DependsTest.testDependsFifth']
        tests2 = list(tests)
        tests2[2], tests2[3] = tests[3], tests[2]
        tc = self._testLoader(modules=self.modules, tests=tests)
        test_loaded = getSuiteCasesIDs(tc.suites)
        result = True if test_loaded == tests or test_loaded == tests2 else False
        msg = 'Failed to order tests using OETestDepends decorator.\nTest order:'\
              ' %s.\nExpected:   %s\nOr:         %s' % (test_loaded, tests, tests2)
        self.assertTrue(result, msg=msg)

    def test_depends_fail_missing_dependency(self):
        expect = "TestCase depends.DependsTest.testDependsSecond depends on "\
                 "depends.DependsTest.testDependsFirst and isn't available"
        tests =  ['depends.DependsTest.testDependsSecond']
        try:
            # Must throw OEQADependency because missing 'testDependsFirst'
            tc = self._testLoader(modules=self.modules, tests=tests)
            self.fail('Expected OEQADependency exception')
        except OEQADependency as e:
            result = True if expect in str(e) else False
            msg = 'Expected OEQADependency exception missing testDependsFirst test'
            self.assertTrue(result, msg=msg)

    def test_depends_fail_circular_dependency(self):
        expect = 'have a circular dependency'
        tests =  ['depends.DependsTest.testDependsCircular1',
                  'depends.DependsTest.testDependsCircular2',
                  'depends.DependsTest.testDependsCircular3']
        try:
            # Must throw OEQADependency because circular dependency
            tc = self._testLoader(modules=self.modules, tests=tests)
            self.fail('Expected OEQADependency exception')
        except OEQADependency as e:
            result = True if expect in str(e) else False
            msg = 'Expected OEQADependency exception having a circular dependency'
            self.assertTrue(result, msg=msg)

class TestTimeoutDecorator(TestBase):
    modules = ['timeout']

    def test_timeout(self):
        tests = ['timeout.TimeoutTest.testTimeoutPass']
        msg = 'Failed to run test using OETestTimeout'
        alarm_signal = signal.getsignal(signal.SIGALRM)
        tc = self._testLoader(modules=self.modules, tests=tests)
        self.assertTrue(tc.runTests().wasSuccessful(), msg=msg)
        msg = "OETestTimeout didn't restore SIGALRM"
        self.assertIs(alarm_signal, signal.getsignal(signal.SIGALRM), msg=msg)

    def test_timeout_fail(self):
        tests = ['timeout.TimeoutTest.testTimeoutFail']
        msg = "OETestTimeout test didn't timeout as expected"
        alarm_signal = signal.getsignal(signal.SIGALRM)
        tc = self._testLoader(modules=self.modules, tests=tests)
        self.assertFalse(tc.runTests().wasSuccessful(), msg=msg)
        msg = "OETestTimeout didn't restore SIGALRM"
        self.assertIs(alarm_signal, signal.getsignal(signal.SIGALRM), msg=msg)

if __name__ == '__main__':
    unittest.main()
