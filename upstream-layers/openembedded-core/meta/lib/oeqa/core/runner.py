#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import time
import unittest
import logging
import re
import json
import sys

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

        self.successes = []
        self.starttime = {}
        self.endtime = {}
        self.progressinfo = {}
        self.extraresults = {}
        self.shownmsg = []

        # Inject into tc so that TestDepends decorator can see results
        tc.results = self

        self.tc = tc

        # stdout and stderr for each test case
        self.logged_output = {}

    def startTest(self, test):
        # May have been set by concurrencytest
        if test.id() not in self.starttime:
            self.starttime[test.id()] = time.time()
        super(OETestResult, self).startTest(test)

    def stopTest(self, test):
        self.endtime[test.id()] = time.time()
        if self.buffer:
            self.logged_output[test.id()] = (
                    sys.stdout.getvalue(), sys.stderr.getvalue())
        super(OETestResult, self).stopTest(test)
        if test.id() in self.progressinfo:
            self.tc.logger.info(self.progressinfo[test.id()])

        # Print the errors/failures early to aid/speed debugging, its a pain
        # to wait until selftest finishes to see them.
        for t in ['failures', 'errors', 'skipped', 'expectedFailures']:
            for (scase, msg) in getattr(self, t):
                if test.id() == scase.id():
                    self.tc.logger.info(str(msg))
                    self.shownmsg.append(test.id())
                    break

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
        msg += " (successes=%d, skipped=%d, failures=%d, errors=%d)" % (len(self.successes), len(self.skipped), len(self.failures), len(self.errors))
        self.tc.logger.info(msg)

    def _getTestResultDetails(self, case):
        result_types = {'failures': 'FAILED', 'errors': 'ERROR', 'skipped': 'SKIPPED',
                        'expectedFailures': 'EXPECTEDFAIL', 'successes': 'PASSED',
                        'unexpectedSuccesses' : 'PASSED'}

        for rtype in result_types:
            found = False
            for resultclass in getattr(self, rtype):
                # unexpectedSuccesses are just lists, not lists of tuples
                if isinstance(resultclass, tuple):
                    scase, msg = resultclass
                else:
                    scase, msg = resultclass, None
                if case.id() == scase.id():
                    found = True
                    break
                scase_str = str(scase.id())

                # When fails at module or class level the class name is passed as string
                # so figure out to see if match
                m = re.search(r"^setUpModule \((?P<module_name>.*)\).*$", scase_str)
                if m:
                    if case.__class__.__module__ == m.group('module_name'):
                        found = True
                        break

                m = re.search(r"^setUpClass \((?P<class_name>.*)\).*$", scase_str)
                if m:
                    class_name = "%s.%s" % (case.__class__.__module__,
                                            case.__class__.__name__)

                    if class_name == m.group('class_name'):
                        found = True
                        break

            if found:
                return result_types[rtype], msg

        return 'UNKNOWN', None

    def extractExtraResults(self, test, details = None):
        extraresults = None
        if details is not None and "extraresults" in details:
            extraresults = details.get("extraresults", {})
        elif hasattr(test, "extraresults"):
            extraresults = test.extraresults

        if extraresults is not None:
            for k, v in extraresults.items():
                # handle updating already existing entries (e.g. ptestresults.sections)
                if k in self.extraresults:
                    self.extraresults[k].update(v)
                else:
                    self.extraresults[k] = v

    def addError(self, test, *args, details = None):
        self.extractExtraResults(test, details = details)
        return super(OETestResult, self).addError(test, *args)

    def addFailure(self, test, *args, details = None):
        self.extractExtraResults(test, details = details)
        return super(OETestResult, self).addFailure(test, *args)

    def addSuccess(self, test, details = None):
        #Added so we can keep track of successes too
        self.successes.append((test, None))
        self.extractExtraResults(test, details = details)
        return super(OETestResult, self).addSuccess(test)

    def addExpectedFailure(self, test, *args, details = None):
        self.extractExtraResults(test, details = details)
        return super(OETestResult, self).addExpectedFailure(test, *args)

    def addUnexpectedSuccess(self, test, details = None):
        self.extractExtraResults(test, details = details)
        return super(OETestResult, self).addUnexpectedSuccess(test)

    def logDetails(self, json_file_dir=None, configuration=None, result_id=None,
            dump_streams=False):

        result = self.extraresults
        logs = {}
        if hasattr(self.tc, "extraresults"):
            result.update(self.tc.extraresults)

        for case_name in self.tc._registry['cases']:
            case = self.tc._registry['cases'][case_name]

            (status, log) = self._getTestResultDetails(case)

            t = ""
            duration = 0
            if case.id() in self.starttime and case.id() in self.endtime:
                duration = self.endtime[case.id()] - self.starttime[case.id()]
                t = " (" + "{0:.2f}".format(duration) + "s)"

            if status not in logs:
                logs[status] = []
            logs[status].append("RESULTS - %s: %s%s" % (case.id(), status, t))
            report = {'status': status}
            if log:
                report['log'] = log
                # Class setup failures wouldn't enter stopTest so would never display
                if case.id() not in self.shownmsg:
                    self.tc.logger.info("Failure (%s) for %s:\n" % (status, case.id()) + log)

            if duration:
                report['duration'] = duration

            alltags = []
            # pull tags from the case class
            if hasattr(case, "__oeqa_testtags"):
                alltags.extend(getattr(case, "__oeqa_testtags"))
            # pull tags from the method itself
            test_name = case._testMethodName
            if hasattr(case, test_name):
                method = getattr(case, test_name)
                if hasattr(method, "__oeqa_testtags"):
                    alltags.extend(getattr(method, "__oeqa_testtags"))
            if alltags:
                report['oetags'] = alltags

            if dump_streams and case.id() in self.logged_output:
                (stdout, stderr) = self.logged_output[case.id()]
                report['stdout'] = stdout
                report['stderr'] = stderr
            result[case.id()] = report

        self.tc.logger.info("RESULTS:")
        for i in ['PASSED', 'SKIPPED', 'EXPECTEDFAIL', 'ERROR', 'FAILED', 'UNKNOWN']:
            if i not in logs:
                continue
            for l in logs[i]:
                self.tc.logger.info(l)

        if json_file_dir:
            tresultjsonhelper = OETestResultJSONHelper()
            tresultjsonhelper.dump_testresult_file(json_file_dir, configuration, result_id, result)

    def wasSuccessful(self):
        # Override as we unexpected successes aren't failures for us
        return (len(self.failures) == len(self.errors) == 0)

    def hasAnyFailingTest(self):
        # Account for expected failures
        return not self.wasSuccessful() or len(self.expectedFailures)

class OEListTestsResult(object):
    def wasSuccessful(self):
        return True

class OETestRunner(_TestRunner):
    streamLoggerClass = OEStreamLogger

    def __init__(self, tc, *args, **kwargs):
        kwargs['stream'] = self.streamLoggerClass(tc.logger)
        super(OETestRunner, self).__init__(*args, **kwargs)
        self.tc = tc
        self.resultclass = OETestResult

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
        self._walked_cases = 0

        def _list_cases(logger, case):
            oetags = []
            if hasattr(case, '__oeqa_testtags'):
                oetags = getattr(case, '__oeqa_testtags')
            if oetags:
                logger.info("%s (%s)" % (case.id(), ",".join(oetags)))
            else:
                logger.info("%s" % (case.id()))

        self.tc.logger.info("Listing all available tests:")
        self._walked_cases = 0
        self.tc.logger.info("test (tags)")
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

class OETestResultJSONHelper(object):

    testresult_filename = 'testresults.json'

    def _get_existing_testresults_if_available(self, write_dir):
        testresults = {}
        file = os.path.join(write_dir, self.testresult_filename)
        if os.path.exists(file):
            with open(file, "r") as f:
                testresults = json.load(f)
        return testresults

    def _write_file(self, write_dir, file_name, file_content):
        file_path = os.path.join(write_dir, file_name)
        with open(file_path, 'w') as the_file:
            the_file.write(file_content)

    def dump_testresult_file(self, write_dir, configuration, result_id, test_result):
        try:
            import bb
            has_bb = True
            bb.utils.mkdirhier(write_dir)
            lf = bb.utils.lockfile(os.path.join(write_dir, 'jsontestresult.lock'))
        except ImportError:
            has_bb = False
            os.makedirs(write_dir, exist_ok=True)
        test_results = self._get_existing_testresults_if_available(write_dir)
        test_results[result_id] = {'configuration': configuration, 'result': test_result}
        json_testresults = json.dumps(test_results, sort_keys=True, indent=1)
        self._write_file(write_dir, self.testresult_filename, json_testresults)
        if has_bb:
            bb.utils.unlockfile(lf)
