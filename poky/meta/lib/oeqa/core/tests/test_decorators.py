#!/usr/bin/env python3
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import signal
import unittest

from common import setup_sys_path, TestBase
setup_sys_path()

from oeqa.core.exception import OEQADependency
from oeqa.core.utils.test import getCaseMethod, getSuiteCasesNames, getSuiteCasesIDs

class TestTagDecorator(TestBase):
    def _runTest(self, modules, filterfn, expect):
        tc = self._testLoader(modules = modules, tags_filter = filterfn)
        test_loaded = set(getSuiteCasesIDs(tc.suites))
        self.assertEqual(expect, test_loaded)

    def test_oetag(self):
        # get all cases without any filtering
        self._runTest(['oetag'], None, {
                'oetag.TagTest.testTagGood',
                'oetag.TagTest.testTagOther',
                'oetag.TagTest.testTagOtherMulti',
                'oetag.TagTest.testTagNone',
                'oetag.TagClassTest.testTagOther',
                'oetag.TagClassTest.testTagOtherMulti',
                'oetag.TagClassTest.testTagNone',
                })

        # exclude any case with tags
        self._runTest(['oetag'], lambda tags: tags, {
                'oetag.TagTest.testTagNone',
                })

        # exclude any case with otherTag
        self._runTest(['oetag'], lambda tags: "otherTag" in tags, {
                'oetag.TagTest.testTagGood',
                'oetag.TagTest.testTagNone',
                'oetag.TagClassTest.testTagNone',
                })

        # exclude any case with classTag
        self._runTest(['oetag'], lambda tags: "classTag" in tags, {
                'oetag.TagTest.testTagGood',
                'oetag.TagTest.testTagOther',
                'oetag.TagTest.testTagOtherMulti',
                'oetag.TagTest.testTagNone',
                })

        # include any case with classTag
        self._runTest(['oetag'], lambda tags: "classTag" not in tags, {
                'oetag.TagClassTest.testTagOther',
                'oetag.TagClassTest.testTagOtherMulti',
                'oetag.TagClassTest.testTagNone',
                })

        # include any case with classTag or no tags
        self._runTest(['oetag'], lambda tags: tags and "classTag" not in tags, {
                'oetag.TagTest.testTagNone',
                'oetag.TagClassTest.testTagOther',
                'oetag.TagClassTest.testTagOtherMulti',
                'oetag.TagClassTest.testTagNone',
                })

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

    def test_timeout_cancel(self):
        tests = ['timeout.TimeoutTest.testTimeoutSkip', 'timeout.TimeoutTest.testTimeoutDepends', 'timeout.TimeoutTest.testTimeoutUnrelated']
        msg = 'Unrelated test failed to complete'
        tc = self._testLoader(modules=self.modules, tests=tests)
        self.assertTrue(tc.runTests().wasSuccessful(), msg=msg)

if __name__ == '__main__':
    unittest.main()
