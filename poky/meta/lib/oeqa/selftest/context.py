# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import time
import glob
import sys
import imp
import signal
from shutil import copyfile
from random import choice

import oeqa

from oeqa.core.context import OETestContext, OETestContextExecutor
from oeqa.core.exception import OEQAPreRun, OEQATestNotFound

from oeqa.utils.commands import runCmd, get_bb_vars, get_test_layer

class OESelftestTestContext(OETestContext):
    def __init__(self, td=None, logger=None, machines=None, config_paths=None):
        super(OESelftestTestContext, self).__init__(td, logger)

        self.machines = machines
        self.custommachine = None
        self.config_paths = config_paths

    def runTests(self, machine=None, skips=[]):
        if machine:
            self.custommachine = machine
            if machine == 'random':
                self.custommachine = choice(self.machines)
            self.logger.info('Run tests with custom MACHINE set to: %s' % \
                    self.custommachine)
        return super(OESelftestTestContext, self).runTests(skips)

    def listTests(self, display_type, machine=None):
        return super(OESelftestTestContext, self).listTests(display_type)

class OESelftestTestContextExecutor(OETestContextExecutor):
    _context_class = OESelftestTestContext
    _script_executor = 'oe-selftest'

    name = 'oe-selftest'
    help = 'oe-selftest test component'
    description = 'Executes selftest tests'

    def register_commands(self, logger, parser):
        group = parser.add_mutually_exclusive_group(required=True)

        group.add_argument('-a', '--run-all-tests', default=False,
                action="store_true", dest="run_all_tests",
                help='Run all (unhidden) tests')
        group.add_argument('-R', '--skip-tests', required=False, action='store',
                nargs='+', dest="skips", default=None,
                help='Run all (unhidden) tests except the ones specified. Format should be <module>[.<class>[.<test_method>]]')
        group.add_argument('-r', '--run-tests', required=False, action='store',
                nargs='+', dest="run_tests", default=None,
                help='Select what tests to run (modules, classes or test methods). Format should be: <module>.<class>.<test_method>')

        group.add_argument('-m', '--list-modules', required=False,
                action="store_true", default=False,
                help='List all available test modules.')
        group.add_argument('--list-classes', required=False,
                action="store_true", default=False,
                help='List all available test classes.')
        group.add_argument('-l', '--list-tests', required=False,
                action="store_true", default=False,
                help='List all available tests.')

        parser.add_argument('--machine', required=False, choices=['random', 'all'],
                            help='Run tests on different machines (random/all).')
        
        parser.set_defaults(func=self.run)

    def _get_available_machines(self):
        machines = []

        bbpath = self.tc_kwargs['init']['td']['BBPATH'].split(':')
    
        for path in bbpath:
            found_machines = glob.glob(os.path.join(path, 'conf', 'machine', '*.conf'))
            if found_machines:
                for i in found_machines:
                    # eg: '/home/<user>/poky/meta-intel/conf/machine/intel-core2-32.conf'
                    machines.append(os.path.splitext(os.path.basename(i))[0])
    
        return machines

    def _get_cases_paths(self, bbpath):
        cases_paths = []
        for layer in bbpath:
            cases_dir = os.path.join(layer, 'lib', 'oeqa', 'selftest', 'cases')
            if os.path.isdir(cases_dir):
                cases_paths.append(cases_dir)
        return cases_paths

    def _process_args(self, logger, args):
        args.output_log = '%s-results-%s.log' % (self.name,
                time.strftime("%Y%m%d%H%M%S"))
        args.test_data_file = None
        args.CASES_PATHS = None

        super(OESelftestTestContextExecutor, self)._process_args(logger, args)

        if args.list_modules:
            args.list_tests = 'module'
        elif args.list_classes:
            args.list_tests = 'class'
        elif args.list_tests:
            args.list_tests = 'name'

        self.tc_kwargs['init']['td'] = get_bb_vars()
        self.tc_kwargs['init']['machines'] = self._get_available_machines()

        builddir = os.environ.get("BUILDDIR")
        self.tc_kwargs['init']['config_paths'] = {}
        self.tc_kwargs['init']['config_paths']['testlayer_path'] = \
                get_test_layer()
        self.tc_kwargs['init']['config_paths']['builddir'] = builddir
        self.tc_kwargs['init']['config_paths']['localconf'] = \
                os.path.join(builddir, "conf/local.conf")
        self.tc_kwargs['init']['config_paths']['localconf_backup'] = \
                os.path.join(builddir, "conf/local.conf.orig")
        self.tc_kwargs['init']['config_paths']['localconf_class_backup'] = \
                os.path.join(builddir, "conf/local.conf.bk")
        self.tc_kwargs['init']['config_paths']['bblayers'] = \
                os.path.join(builddir, "conf/bblayers.conf")
        self.tc_kwargs['init']['config_paths']['bblayers_backup'] = \
                os.path.join(builddir, "conf/bblayers.conf.orig")
        self.tc_kwargs['init']['config_paths']['bblayers_class_backup'] = \
                os.path.join(builddir, "conf/bblayers.conf.bk")

        copyfile(self.tc_kwargs['init']['config_paths']['localconf'],
                self.tc_kwargs['init']['config_paths']['localconf_backup'])
        copyfile(self.tc_kwargs['init']['config_paths']['bblayers'], 
                self.tc_kwargs['init']['config_paths']['bblayers_backup'])

        self.tc_kwargs['run']['skips'] = args.skips

    def _pre_run(self):
        def _check_required_env_variables(vars):
            for var in vars:
                if not os.environ.get(var):
                    self.tc.logger.error("%s is not set. Did you forget to source your build environment setup script?" % var)
                    raise OEQAPreRun

        def _check_presence_meta_selftest():
            builddir = os.environ.get("BUILDDIR")
            if os.getcwd() != builddir:
                self.tc.logger.info("Changing cwd to %s" % builddir)
                os.chdir(builddir)

            if not "meta-selftest" in self.tc.td["BBLAYERS"]:
                self.tc.logger.warn("meta-selftest layer not found in BBLAYERS, adding it")
                meta_selftestdir = os.path.join(
                    self.tc.td["BBLAYERS_FETCH_DIR"], 'meta-selftest')
                if os.path.isdir(meta_selftestdir):
                    runCmd("bitbake-layers add-layer %s" %meta_selftestdir)
                    # reload data is needed because a meta-selftest layer was add
                    self.tc.td = get_bb_vars()
                    self.tc.config_paths['testlayer_path'] = get_test_layer()
                else:
                    self.tc.logger.error("could not locate meta-selftest in:\n%s" % meta_selftestdir)
                    raise OEQAPreRun

        def _add_layer_libs():
            bbpath = self.tc.td['BBPATH'].split(':')
            layer_libdirs = [p for p in (os.path.join(l, 'lib') \
                    for l in bbpath) if os.path.exists(p)]
            if layer_libdirs:
                self.tc.logger.info("Adding layer libraries:")
                for l in layer_libdirs:
                    self.tc.logger.info("\t%s" % l)

                sys.path.extend(layer_libdirs)
                imp.reload(oeqa.selftest)

        _check_required_env_variables(["BUILDDIR"])
        _check_presence_meta_selftest()

        if "buildhistory.bbclass" in self.tc.td["BBINCLUDED"]:
            self.tc.logger.error("You have buildhistory enabled already and this isn't recommended for selftest, please disable it first.")
            raise OEQAPreRun

        if "PRSERV_HOST" in self.tc.td:
            self.tc.logger.error("Please unset PRSERV_HOST in order to run oe-selftest")
            raise OEQAPreRun

        if "SANITY_TESTED_DISTROS" in self.tc.td:
            self.tc.logger.error("Please unset SANITY_TESTED_DISTROS in order to run oe-selftest")
            raise OEQAPreRun

        _add_layer_libs()

        self.tc.logger.info("Running bitbake -p")
        runCmd("bitbake -p")

    def _internal_run(self, logger, args):
        self.module_paths = self._get_cases_paths(
                self.tc_kwargs['init']['td']['BBPATH'].split(':'))

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

        return rc

    def _signal_clean_handler(self, signum, frame):
        sys.exit(1)
    
    def run(self, logger, args):
        self._process_args(logger, args)

        signal.signal(signal.SIGTERM, self._signal_clean_handler)

        rc = None
        try:
            if args.machine:
                logger.info('Custom machine mode enabled. MACHINE set to %s' %
                        args.machine)

                if args.machine == 'all':
                    results = []
                    for m in self.tc_kwargs['init']['machines']:
                        self.tc_kwargs['run']['machine'] = m
                        results.append(self._internal_run(logger, args))

                        # XXX: the oe-selftest script only needs to know if one
                        # machine run fails
                        for r in results:
                            rc = r
                            if not r.wasSuccessful():
                                break

                else:
                    self.tc_kwargs['run']['machine'] = args.machine
                    return self._internal_run(logger, args)

            else:
                self.tc_kwargs['run']['machine'] = args.machine
                rc = self._internal_run(logger, args)
        finally:
            config_paths = self.tc_kwargs['init']['config_paths']
            if os.path.exists(config_paths['localconf_backup']):
                copyfile(config_paths['localconf_backup'],
                        config_paths['localconf'])
                os.remove(config_paths['localconf_backup'])

            if os.path.exists(config_paths['bblayers_backup']):
                copyfile(config_paths['bblayers_backup'], 
                        config_paths['bblayers'])
                os.remove(config_paths['bblayers_backup'])

            if os.path.exists(config_paths['localconf_class_backup']):
                os.remove(config_paths['localconf_class_backup'])
            if os.path.exists(config_paths['bblayers_class_backup']):
                os.remove(config_paths['bblayers_class_backup'])

            output_link = os.path.join(os.path.dirname(args.output_log),
                    "%s-results.log" % self.name)
            if os.path.exists(output_link):
                os.remove(output_link)
            os.symlink(args.output_log, output_link)

        return rc

_executor_class = OESelftestTestContextExecutor
