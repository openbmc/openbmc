# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import time
import unittest
import logging
import re

xmlEnabled = False
try:
    import xmlrunner
    from xmlrunner.result import _XMLTestResult as _TestResult
    from xmlrunner.runner import XMLTestRunner as _TestRunner
    xmlEnabled = True
except ImportError:
    # use the base runner instead
    from unittest import TextTestResult as _TestResult
    from unittest import TextTestRunner as _TestRunner

class OEStreamLogger(object):
    def __init__(self, logger):
        self.logger = logger
        self.buffer = ""

    def write(self, msg):
        if len(msg) > 1 and msg[0] != '\n':
            if '...' in msg:
                self.buffer += msg
            elif self.buffer:
                self.buffer += msg
                self.logger.log(logging.INFO, self.buffer)
                self.buffer = ""
            else:
                self.logger.log(logging.INFO, msg)

    def flush(self):
        for handler in self.logger.handlers:
            handler.flush()

class OETestResult(_TestResult):
    def __init__(self, tc, *args, **kwargs):
        super(OETestResult, self).__init__(*args, **kwargs)

        self.tc = tc
        self._tc_map_results()

    def startTest(self, test):
        # Allow us to trigger the testcase buffer mode on a per test basis
        # so stdout/stderr are only printed upon failure. Enables debugging
        # but clean output
        if hasattr(test, "buffer"):
            self.buffer = test.buffer
        super(OETestResult, self).startTest(test)

    def _tc_map_results(self):
        self.tc._results['failures'] = self.failures
        self.tc._results['errors'] = self.errors
        self.tc._results['skipped'] = self.skipped
        self.tc._results['expectedFailures'] = self.expectedFailures

    def logSummary(self, component, context_msg=''):
        elapsed_time = self.tc._run_end_time - self.tc._run_start_time
        self.tc.logger.info("SUMMARY:")
        self.tc.logger.info("%s (%s) - Ran %d test%s in %.3fs" % (component,
            context_msg, self.testsRun, self.testsRun != 1 and "s" or "",
            elapsed_time))

        if self.wasSuccessful():
            msg = "%s - OK - All required tests passed" % component
        else:
            msg = "%s - FAIL - Required tests failed" % component
        skipped = len(self.tc._results['skipped'])
        if skipped: 
            msg += " (skipped=%d)" % skipped
        self.tc.logger.info(msg)

    def _getDetailsNotPassed(self, case, type, desc):
        found = False

        for (scase, msg) in self.tc._results[type]:
            # XXX: When XML reporting is enabled scase is
            # xmlrunner.result._TestInfo instance instead of
            # string.
            if xmlEnabled:
                if case.id() == scase.test_id:
                    found = True
                    break
                scase_str = scase.test_id
            else:
                if case == scase:
                    found = True
                    break
                scase_str = str(scase)

            # When fails at module or class level the class name is passed as string
            # so figure out to see if match
            m = re.search("^setUpModule \((?P<module_name>.*)\)$", scase_str)
            if m:
                if case.__class__.__module__ == m.group('module_name'):
                    found = True
                    break

            m = re.search("^setUpClass \((?P<class_name>.*)\)$", scase_str)
            if m:
                class_name = "%s.%s" % (case.__class__.__module__,
                        case.__class__.__name__)

                if class_name == m.group('class_name'):
                    found = True
                    break

        if found:
            return (found, msg)

        return (found, None)

    def logDetails(self):
        self.tc.logger.info("RESULTS:")
        for case_name in self.tc._registry['cases']:
            case = self.tc._registry['cases'][case_name]

            result_types = ['failures', 'errors', 'skipped', 'expectedFailures']
            result_desc = ['FAILED', 'ERROR', 'SKIPPED', 'EXPECTEDFAIL']

            fail = False
            desc = None
            for idx, name in enumerate(result_types):
                (fail, msg) = self._getDetailsNotPassed(case, result_types[idx],
                        result_desc[idx])
                if fail:
                    desc = result_desc[idx]
                    break

            oeid = -1
            if hasattr(case, 'decorators'):
                for d in case.decorators:
                    if hasattr(d, 'oeid'):
                        oeid = d.oeid

            if fail:
                self.tc.logger.info("RESULTS - %s - Testcase %s: %s" % (case.id(),
                    oeid, desc))
            else:
                self.tc.logger.info("RESULTS - %s - Testcase %s: %s" % (case.id(),
                    oeid, 'PASSED'))

class OEListTestsResult(object):
    def wasSuccessful(self):
        return True

class OETestRunner(_TestRunner):
    streamLoggerClass = OEStreamLogger

    def __init__(self, tc, *args, **kwargs):
        if xmlEnabled:
            if not kwargs.get('output'):
                kwargs['output'] = os.path.join(os.getcwd(),
                        'TestResults_%s_%s' % (time.strftime("%Y%m%d%H%M%S"), os.getpid()))

        kwargs['stream'] = self.streamLoggerClass(tc.logger)
        super(OETestRunner, self).__init__(*args, **kwargs)
        self.tc = tc
        self.resultclass = OETestResult

    # XXX: The unittest-xml-reporting package defines _make_result method instead
    # of _makeResult standard on unittest.
    if xmlEnabled:
        def _make_result(self):
            """
            Creates a TestResult object which will be used to store
            information about the executed tests.
            """
            # override in subclasses if necessary.
            return self.resultclass(self.tc,
                self.stream, self.descriptions, self.verbosity, self.elapsed_times
            )
    else:
        def _makeResult(self):
            return self.resultclass(self.tc, self.stream, self.descriptions,
                    self.verbosity)


    def _walk_suite(self, suite, func):
        for obj in suite:
            if isinstance(obj, unittest.suite.TestSuite):
                if len(obj._tests):
                    self._walk_suite(obj, func)
            elif isinstance(obj, unittest.case.TestCase):
                func(self.tc.logger, obj)
                self._walked_cases = self._walked_cases + 1

    def _list_tests_name(self, suite):
        from oeqa.core.decorator.oeid import OETestID
        from oeqa.core.decorator.oetag import OETestTag

        self._walked_cases = 0

        def _list_cases_without_id(logger, case):

            found_id = False
            if hasattr(case, 'decorators'):
                for d in case.decorators:
                    if isinstance(d, OETestID):
                        found_id = True

            if not found_id:
                logger.info('oeid missing for %s' % case.id())

        def _list_cases(logger, case):
            oeid = None
            oetag = None

            if hasattr(case, 'decorators'):
                for d in case.decorators:
                    if isinstance(d, OETestID):
                        oeid = d.oeid
                    elif isinstance(d, OETestTag):
                        oetag = d.oetag

            logger.info("%s\t%s\t\t%s" % (oeid, oetag, case.id()))

        self.tc.logger.info("Listing test cases that don't have oeid ...")
        self._walk_suite(suite, _list_cases_without_id)
        self.tc.logger.info("-" * 80)

        self.tc.logger.info("Listing all available tests:")
        self._walked_cases = 0
        self.tc.logger.info("id\ttag\t\ttest")
        self.tc.logger.info("-" * 80)
        self._walk_suite(suite, _list_cases)
        self.tc.logger.info("-" * 80)
        self.tc.logger.info("Total found:\t%s" % self._walked_cases)

    def _list_tests_class(self, suite):
        self._walked_cases = 0

        curr = {}
        def _list_classes(logger, case):
            if not 'module' in curr or curr['module'] != case.__module__:
                curr['module'] = case.__module__
                logger.info(curr['module'])

            if not 'class' in curr  or curr['class'] != \
                    case.__class__.__name__:
                curr['class'] = case.__class__.__name__
                logger.info(" -- %s" % curr['class'])

            logger.info(" -- -- %s" % case._testMethodName)

        self.tc.logger.info("Listing all available test classes:")
        self._walk_suite(suite, _list_classes)

    def _list_tests_module(self, suite):
        self._walked_cases = 0

        listed = []
        def _list_modules(logger, case):
            if not case.__module__ in listed:
                if case.__module__.startswith('_'):
                    logger.info("%s (hidden)" % case.__module__)
                else:
                    logger.info(case.__module__)
                listed.append(case.__module__)

        self.tc.logger.info("Listing all available test modules:")
        self._walk_suite(suite, _list_modules)

    def list_tests(self, suite, display_type):
        if display_type == 'name':
            self._list_tests_name(suite)
        elif display_type == 'class':
            self._list_tests_class(suite)
        elif display_type == 'module':
            self._list_tests_module(suite)

        return OEListTestsResult()
