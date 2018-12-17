# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import sys
import json
import time
import logging
import collections

from oeqa.core.loader import OETestLoader
from oeqa.core.runner import OETestRunner
from oeqa.core.exception import OEQAMissingManifest, OEQATestNotFound

class OETestContext(object):
    loaderClass = OETestLoader
    runnerClass = OETestRunner

    files_dir = os.path.abspath(os.path.join(os.path.dirname(
        os.path.abspath(__file__)), "../files"))

    def __init__(self, td=None, logger=None):
        if not type(td) is dict:
            raise TypeError("td isn't dictionary type")

        self.td = td
        self.logger = logger
        self._registry = {}
        self._registry['cases'] = collections.OrderedDict()

    def _read_modules_from_manifest(self, manifest):
        if not os.path.exists(manifest):
            raise OEQAMissingManifest("Manifest does not exist on %s" % manifest)

        modules = []
        for line in open(manifest).readlines():
            line = line.strip()
            if line and not line.startswith("#"):
                modules.append(line)

        return modules

    def skipTests(self, skips):
        if not skips:
            return
        for test in self.suites:
            for skip in skips:
                if test.id().startswith(skip):
                    setattr(test, 'setUp', lambda: test.skipTest('Skip by the command line argument "%s"' % skip))

    def loadTests(self, module_paths, modules=[], tests=[],
            modules_manifest="", modules_required=[], filters={}):
        if modules_manifest:
            modules = self._read_modules_from_manifest(modules_manifest)

        self.loader = self.loaderClass(self, module_paths, modules, tests,
                modules_required, filters)
        self.suites = self.loader.discover()

    def runTests(self, processes=None, skips=[]):
        self.runner = self.runnerClass(self, descriptions=False, verbosity=2)

        # Dinamically skip those tests specified though arguments
        self.skipTests(skips)

        self._run_start_time = time.time()
        if processes:
            from oeqa.core.utils.concurrencytest import ConcurrentTestSuite

            concurrent_suite = ConcurrentTestSuite(self.suites, processes)
            result = self.runner.run(concurrent_suite)
        else:
            self.runner.buffer = True
            result = self.runner.run(self.suites)
        self._run_end_time = time.time()

        return result

    def listTests(self, display_type):
        self.runner = self.runnerClass(self, verbosity=2)
        return self.runner.list_tests(self.suites, display_type)

class OETestContextExecutor(object):
    _context_class = OETestContext
    _script_executor = 'oe-test'

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

        group = self.parser.add_mutually_exclusive_group()
        group.add_argument('--run-tests', action='store', nargs='+',
                default=self.default_tests,
                help="tests to run in <module>[.<class>[.<name>]]")
        group.add_argument('--list-tests', action='store',
                choices=('module', 'class', 'name'),
                help="lists available tests")

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
        self.tc_kwargs['list'] = {}
        self.tc_kwargs['run']  = {}

        self.tc_kwargs['init']['logger'] = self._setup_logger(logger, args)
        if args.test_data_file:
            self.tc_kwargs['init']['td'] = json.load(
                    open(args.test_data_file, "r"))
        else:
            self.tc_kwargs['init']['td'] = {}

        if args.run_tests:
            self.tc_kwargs['load']['modules'] = args.run_tests
            self.tc_kwargs['load']['modules_required'] = args.run_tests
        else:
            self.tc_kwargs['load']['modules'] = []

        self.tc_kwargs['run']['skips'] = []

        self.module_paths = args.CASES_PATHS

    def _pre_run(self):
        pass

    def run(self, logger, args):
        self._process_args(logger, args)

        self.tc = self._context_class(**self.tc_kwargs['init'])
        try:
            self.tc.loadTests(self.module_paths, **self.tc_kwargs['load'])
        except OEQATestNotFound as ex:
            logger.error(ex)
            sys.exit(1)

        if args.list_tests:
            rc = self.tc.listTests(args.list_tests, **self.tc_kwargs['list'])
        else:
            self._pre_run()
            rc = self.tc.runTests(**self.tc_kwargs['run'])
            rc.logDetails()
            rc.logSummary(self.name)

        output_link = os.path.join(os.path.dirname(args.output_log),
                "%s-results.log" % self.name)
        if os.path.exists(output_link):
            os.remove(output_link)
        os.symlink(args.output_log, output_link)

        return rc

_executor_class = OETestContextExecutor
