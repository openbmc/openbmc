#!/usr/bin/env python3

# Copyright (C) 2016-2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import unittest

from common import setup_sys_path, TestBase
setup_sys_path()

from oeqa.core.exception import OEQADependency
from oeqa.core.utils.test import getSuiteModules, getSuiteCasesIDs

class TestLoader(TestBase):

    def test_fail_empty_filter(self):
        filters = {'oetag' : ''}
        expect = 'Filter oetag specified is empty'
        msg = 'Expected TypeError exception for having invalid filter'
        try:
            # Must throw TypeError because empty filter
            tc = self._testLoader(filters=filters)
            self.fail(msg)
        except TypeError as e:
            result = True if expect in str(e) else False
            self.assertTrue(result, msg=msg)

    def test_fail_invalid_filter(self):
        filters = {'invalid' : 'good'}
        expect = 'filter but not declared in any of'
        msg = 'Expected TypeError exception for having invalid filter'
        try:
            # Must throw TypeError because invalid filter
            tc = self._testLoader(filters=filters)
            self.fail(msg)
        except TypeError as e:
            result = True if expect in str(e) else False
            self.assertTrue(result, msg=msg)

    def test_fail_duplicated_module(self):
        cases_path = self.cases_path
        invalid_path = os.path.join(cases_path, 'loader', 'invalid')
        self.cases_path = [self.cases_path, invalid_path]
        expect = 'Duplicated oeid module found in'
        msg = 'Expected ImportError exception for having duplicated module'
        try:
            # Must throw ImportEror because duplicated module
            tc = self._testLoader()
            self.fail(msg)
        except ImportError as e:
            result = True if expect in str(e) else False
            self.assertTrue(result, msg=msg)
        finally:
            self.cases_path = cases_path

    def test_filter_modules(self):
        expected_modules = {'oeid', 'oetag'}
        tc = self._testLoader(modules=expected_modules)
        modules = getSuiteModules(tc.suites)
        msg = 'Expected just %s modules' % ', '.join(expected_modules)
        self.assertEqual(modules, expected_modules, msg=msg)

    def test_filter_cases(self):
        modules = ['oeid', 'oetag', 'data']
        expected_cases = {'data.DataTest.testDataOk',
                          'oetag.TagTest.testTagGood',
                          'oeid.IDTest.testIdGood'}
        tc = self._testLoader(modules=modules, tests=expected_cases)
        cases = set(getSuiteCasesIDs(tc.suites))
        msg = 'Expected just %s cases' % ', '.join(expected_cases)
        self.assertEqual(cases, expected_cases, msg=msg)

    def test_import_from_paths(self):
        cases_path = self.cases_path
        cases2_path = os.path.join(cases_path, 'loader', 'valid')
        expected_modules = {'oeid', 'another'}
        self.cases_path = [self.cases_path, cases2_path]
        tc = self._testLoader(modules=expected_modules)
        modules = getSuiteModules(tc.suites)
        self.cases_path = cases_path
        msg = 'Expected modules from two different paths'
        self.assertEqual(modules, expected_modules, msg=msg)

    def test_loader_threaded(self):
        cases_path = self.cases_path

        self.cases_path = [os.path.join(self.cases_path, 'loader', 'threaded')]

        tc = self._testLoaderThreaded()
        self.assertEqual(len(tc.suites), 3, "Expected to be 3 suites")

        case_ids = ['threaded.ThreadedTest.test_threaded_no_depends',
                'threaded.ThreadedTest2.test_threaded_same_module',
                'threaded_depends.ThreadedTest3.test_threaded_depends']
        for case in tc.suites[0]._tests:
            self.assertEqual(case.id(),
                    case_ids[tc.suites[0]._tests.index(case)])

        case_ids = ['threaded_alone.ThreadedTestAlone.test_threaded_alone']
        for case in tc.suites[1]._tests:
            self.assertEqual(case.id(),
                    case_ids[tc.suites[1]._tests.index(case)])

        case_ids = ['threaded_module.ThreadedTestModule.test_threaded_module',
                'threaded_module.ThreadedTestModule2.test_threaded_module2']
        for case in tc.suites[2]._tests:
            self.assertEqual(case.id(),
                    case_ids[tc.suites[2]._tests.index(case)])

        self.cases_path = cases_path

if __name__ == '__main__':
    unittest.main()
