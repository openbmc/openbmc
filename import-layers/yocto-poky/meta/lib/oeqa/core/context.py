# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import sys
import json
import time
import logging
import collections
import re

from oeqa.core.loader import OETestLoader
from oeqa.core.runner import OETestRunner, OEStreamLogger, xmlEnabled

class OETestContext(object):
    loaderClass = OETestLoader
    runnerClass = OETestRunner
    streamLoggerClass = OEStreamLogger

    files_dir = os.path.abspath(os.path.join(os.path.dirname(
        os.path.abspath(__file__)), "../files"))

    def __init__(self, td=None, logger=None):
        if not type(td) is dict:
            raise TypeError("td isn't dictionary type")

        self.td = td
        self.logger = logger
        self._registry = {}
        self._registry['cases'] = collections.OrderedDict()
        self._results = {}

    def _read_modules_from_manifest(self, manifest):
        if not os.path.exists(manifest):
            raise

        modules = []
        for line in open(manifest).readlines():
            line = line.strip()
            if line and not line.startswith("#"):
                modules.append(line)

        return modules

    def loadTests(self, module_paths, modules=[], tests=[],
            modules_manifest="", modules_required=[], filters={}):
        if modules_manifest:
            modules = self._read_modules_from_manifest(modules_manifest)

        self.loader = self.loaderClass(self, module_paths, modules, tests,
                modules_required, filters)
        self.suites = self.loader.discover()

    def runTests(self):
        streamLogger = self.streamLoggerClass(self.logger)
        self.runner = self.runnerClass(self, stream=streamLogger, verbosity=2)

        self._run_start_time = time.time()
        result = self.runner.run(self.suites)
        self._run_end_time = time.time()

        return result

    def logSummary(self, result, component, context_msg=''):
        self.logger.info("SUMMARY:")
        self.logger.info("%s (%s) - Ran %d test%s in %.3fs" % (component,
            context_msg, result.testsRun, result.testsRun != 1 and "s" or "",
            (self._run_end_time - self._run_start_time)))

        if result.wasSuccessful():
            msg = "%s - OK - All required tests passed" % component
        else:
            msg = "%s - FAIL - Required tests failed" % component
        skipped = len(self._results['skipped'])
        if skipped: 
            msg += " (skipped=%d)" % skipped
        self.logger.info(msg)

    def _getDetailsNotPassed(self, case, type, desc):
        found = False

        for (scase, msg) in self._results[type]:
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
        self.logger.info("RESULTS:")
        for case_name in self._registry['cases']:
            case = self._registry['cases'][case_name]

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
            for d in case.decorators:
                if hasattr(d, 'oeid'):
                    oeid = d.oeid
            
            if fail:
                self.logger.info("RESULTS - %s - Testcase %s: %s" % (case.id(),
                    oeid, desc))
                if msg:
                    self.logger.info(msg)
            else:
                self.logger.info("RESULTS - %s - Testcase %s: %s" % (case.id(),
                    oeid, 'PASSED'))

class OETestContextExecutor(object):
    _context_class = OETestContext

    name = 'core'
    help = 'core test component example'
    description = 'executes core test suite example'

    default_cases = [os.path.join(os.path.abspath(os.path.dirname(__file__)),
            'cases/example')]
    default_test_data = os.path.join(default_cases[0], 'data.json')
    default_tests = None

    def register_commands(self, logger, subparsers):
        self.parser = subparsers.add_parser(self.name, help=self.help,
                description=self.description, group='components')

        self.default_output_log = '%s-results-%s.log' % (self.name,
                time.strftime("%Y%m%d%H%M%S"))
        self.parser.add_argument('--output-log', action='store',
                default=self.default_output_log,
                help="results output log, default: %s" % self.default_output_log)
        self.parser.add_argument('--run-tests', action='store',
                default=self.default_tests,
                help="tests to run in <module>[.<class>[.<name>]] format. Just works for modules now")

        if self.default_test_data:
            self.parser.add_argument('--test-data-file', action='store',
                    default=self.default_test_data,
                    help="data file to load, default: %s" % self.default_test_data)
        else:
            self.parser.add_argument('--test-data-file', action='store',
                    help="data file to load")

        if self.default_cases:
            self.parser.add_argument('CASES_PATHS', action='store',
                    default=self.default_cases, nargs='*',
                    help="paths to directories with test cases, default: %s"\
                            % self.default_cases)
        else:
            self.parser.add_argument('CASES_PATHS', action='store',
                    nargs='+', help="paths to directories with test cases")

        self.parser.set_defaults(func=self.run)

    def _setup_logger(self, logger, args):
        formatter = logging.Formatter('%(asctime)s - ' + self.name + \
                ' - %(levelname)s - %(message)s')
        sh = logger.handlers[0]
        sh.setFormatter(formatter)
        fh = logging.FileHandler(args.output_log)
        fh.setFormatter(formatter)
        logger.addHandler(fh)

        return logger

    def _process_args(self, logger, args):
        self.tc_kwargs = {}
        self.tc_kwargs['init'] = {}
        self.tc_kwargs['load'] = {}
        self.tc_kwargs['run'] = {}

        self.tc_kwargs['init']['logger'] = self._setup_logger(logger, args)
        if args.test_data_file:
            self.tc_kwargs['init']['td'] = json.load(
                    open(args.test_data_file, "r"))
        else:
            self.tc_kwargs['init']['td'] = {}


        if args.run_tests:
            self.tc_kwargs['load']['modules'] = args.run_tests.split()
        else:
            self.tc_kwargs['load']['modules'] = None

        self.module_paths = args.CASES_PATHS

    def run(self, logger, args):
        self._process_args(logger, args)

        self.tc = self._context_class(**self.tc_kwargs['init'])
        self.tc.loadTests(self.module_paths, **self.tc_kwargs['load'])
        rc = self.tc.runTests(**self.tc_kwargs['run'])
        self.tc.logSummary(rc, self.name)
        self.tc.logDetails()

        output_link = os.path.join(os.path.dirname(args.output_log),
                "%s-results.log" % self.name)
        if os.path.exists(output_link):
            os.remove(output_link)
        os.symlink(args.output_log, output_link)

        return rc

_executor_class = OETestContextExecutor
