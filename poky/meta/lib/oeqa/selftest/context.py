#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import time
import glob
import sys
import importlib
import subprocess
import unittest
from random import choice

import oeqa
import oe
import bb.utils

from oeqa.core.context import OETestContext, OETestContextExecutor
from oeqa.core.exception import OEQAPreRun, OEQATestNotFound

from oeqa.utils.commands import runCmd, get_bb_vars, get_test_layer

class NonConcurrentTestSuite(unittest.TestSuite):
    def __init__(self, suite, processes, setupfunc, removefunc):
        super().__init__([suite])
        self.processes = processes
        self.suite = suite
        self.setupfunc = setupfunc
        self.removefunc = removefunc

    def run(self, result):
        (builddir, newbuilddir) = self.setupfunc("-st", None, self.suite)
        ret = super().run(result)
        os.chdir(builddir)
        if newbuilddir and ret.wasSuccessful() and self.removefunc:
            self.removefunc(newbuilddir)

def removebuilddir(d):
    delay = 5
    while delay and os.path.exists(d + "/bitbake.lock"):
        time.sleep(1)
        delay = delay - 1
    # Deleting these directories takes a lot of time, use autobuilder
    # clobberdir if its available
    clobberdir = os.path.expanduser("~/yocto-autobuilder-helper/janitor/clobberdir")
    if os.path.exists(clobberdir):
        try:
            subprocess.check_call([clobberdir, d])
            return
        except subprocess.CalledProcessError:
            pass
    bb.utils.prunedir(d, ionice=True)

class OESelftestTestContext(OETestContext):
    def __init__(self, td=None, logger=None, machines=None, config_paths=None, newbuilddir=None, keep_builddir=None):
        super(OESelftestTestContext, self).__init__(td, logger)

        self.machines = machines
        self.custommachine = None
        self.config_paths = config_paths
        self.newbuilddir = newbuilddir

        if keep_builddir:
            self.removebuilddir = None
        else:
            self.removebuilddir = removebuilddir

    def setup_builddir(self, suffix, selftestdir, suite):
        builddir = os.environ['BUILDDIR']
        if not selftestdir:
            selftestdir = get_test_layer()
        if self.newbuilddir:
            newbuilddir = os.path.join(self.newbuilddir, 'build' + suffix)
        else:
            newbuilddir = builddir + suffix
        newselftestdir = newbuilddir + "/meta-selftest"

        if os.path.exists(newbuilddir):
            self.logger.error("Build directory %s already exists, aborting" % newbuilddir)
            sys.exit(1)

        bb.utils.mkdirhier(newbuilddir)
        oe.path.copytree(builddir + "/conf", newbuilddir + "/conf")
        oe.path.copytree(builddir + "/cache", newbuilddir + "/cache")
        oe.path.copytree(selftestdir, newselftestdir)

        for e in os.environ:
            if builddir + "/" in os.environ[e]:
                os.environ[e] = os.environ[e].replace(builddir + "/", newbuilddir + "/")
            if os.environ[e].endswith(builddir):
                os.environ[e] = os.environ[e].replace(builddir, newbuilddir)

        subprocess.check_output("git init; git add *; git commit -a -m 'initial'", cwd=newselftestdir, shell=True)

        # Tried to used bitbake-layers add/remove but it requires recipe parsing and hence is too slow
        subprocess.check_output("sed %s/conf/bblayers.conf -i -e 's#%s#%s#g'" % (newbuilddir, selftestdir, newselftestdir), cwd=newbuilddir, shell=True)

        os.chdir(newbuilddir)

        def patch_test(t):
            if not hasattr(t, "tc"):
                return
            cp = t.tc.config_paths
            for p in cp:
                if selftestdir in cp[p] and newselftestdir not in cp[p]:
                    cp[p] = cp[p].replace(selftestdir, newselftestdir)
                if builddir in cp[p] and newbuilddir not in cp[p]:
                    cp[p] = cp[p].replace(builddir, newbuilddir)

        def patch_suite(s):
            for x in s:
                if isinstance(x, unittest.TestSuite):
                    patch_suite(x)
                else:
                    patch_test(x)

        patch_suite(suite)

        return (builddir, newbuilddir)

    def prepareSuite(self, suites, processes):
        if processes:
            from oeqa.core.utils.concurrencytest import ConcurrentTestSuite

            return ConcurrentTestSuite(suites, processes, self.setup_builddir, self.removebuilddir)
        else:
            return NonConcurrentTestSuite(suites, processes, self.setup_builddir, self.removebuilddir)

    def runTests(self, processes=None, machine=None, skips=[]):
        if machine:
            self.custommachine = machine
            if machine == 'random':
                self.custommachine = choice(self.machines)
            self.logger.info('Run tests with custom MACHINE set to: %s' % \
                    self.custommachine)
        return super(OESelftestTestContext, self).runTests(processes, skips)

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

        parser.add_argument('-j', '--num-processes', dest='processes', action='store',
                type=int, help="number of processes to execute in parallel with")

        parser.add_argument('--machine', required=False, choices=['random', 'all'],
                            help='Run tests on different machines (random/all).')

        parser.add_argument('-t', '--select-tag', dest="select_tags",
                action='append', default=None,
                help='Filter all (unhidden) tests to any that match any of the specified tag(s).')
        parser.add_argument('-T', '--exclude-tag', dest="exclude_tags",
                action='append', default=None,
                help='Exclude all (unhidden) tests that match any of the specified tag(s). (exclude applies before select)')

        parser.add_argument('-K', '--keep-builddir', action='store_true',
                help='Keep the test build directory even if all tests pass')

        parser.add_argument('-B', '--newbuilddir', help='New build directory to use for tests.')
        parser.add_argument('-v', '--verbose', action='store_true')
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
        args.test_start_time = time.strftime("%Y%m%d%H%M%S")
        args.test_data_file = None
        args.CASES_PATHS = None

        bbvars = get_bb_vars()
        logdir = os.environ.get("BUILDDIR")
        if 'LOG_DIR' in bbvars:
            logdir = bbvars['LOG_DIR']
        bb.utils.mkdirhier(logdir)
        args.output_log = logdir + '/%s-results-%s.log' % (self.name, args.test_start_time)

        super(OESelftestTestContextExecutor, self)._process_args(logger, args)

        if args.list_modules:
            args.list_tests = 'module'
        elif args.list_classes:
            args.list_tests = 'class'
        elif args.list_tests:
            args.list_tests = 'name'

        self.tc_kwargs['init']['td'] = bbvars
        self.tc_kwargs['init']['machines'] = self._get_available_machines()

        builddir = os.environ.get("BUILDDIR")
        self.tc_kwargs['init']['config_paths'] = {}
        self.tc_kwargs['init']['config_paths']['testlayer_path'] = get_test_layer()
        self.tc_kwargs['init']['config_paths']['builddir'] = builddir
        self.tc_kwargs['init']['config_paths']['localconf'] = os.path.join(builddir, "conf/local.conf")
        self.tc_kwargs['init']['config_paths']['bblayers'] = os.path.join(builddir, "conf/bblayers.conf")
        self.tc_kwargs['init']['newbuilddir'] = args.newbuilddir
        self.tc_kwargs['init']['keep_builddir'] = args.keep_builddir

        def tag_filter(tags):
            if args.exclude_tags:
                if any(tag in args.exclude_tags for tag in tags):
                    return True
            if args.select_tags:
                if not tags or not any(tag in args.select_tags for tag in tags):
                    return True
            return False

        if args.select_tags or args.exclude_tags:
            self.tc_kwargs['load']['tags_filter'] = tag_filter

        self.tc_kwargs['run']['skips'] = args.skips
        self.tc_kwargs['run']['processes'] = args.processes

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
                self.tc.logger.warning("meta-selftest layer not found in BBLAYERS, adding it")
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
                importlib.reload(oeqa.selftest)

        _check_required_env_variables(["BUILDDIR"])
        _check_presence_meta_selftest()

        if "buildhistory.bbclass" in self.tc.td["BBINCLUDED"]:
            self.tc.logger.error("You have buildhistory enabled already and this isn't recommended for selftest, please disable it first.")
            raise OEQAPreRun

        if "rm_work.bbclass" in self.tc.td["BBINCLUDED"]:
            self.tc.logger.error("You have rm_work enabled which isn't recommended while running oe-selftest. Please disable it before continuing.")
            raise OEQAPreRun

        if "PRSERV_HOST" in self.tc.td:
            self.tc.logger.error("Please unset PRSERV_HOST in order to run oe-selftest")
            raise OEQAPreRun

        if "SANITY_TESTED_DISTROS" in self.tc.td:
            self.tc.logger.error("Please unset SANITY_TESTED_DISTROS in order to run oe-selftest")
            raise OEQAPreRun

        _add_layer_libs()

        self.tc.logger.info("Running bitbake -e to test the configuration is valid/parsable")
        runCmd("bitbake -e")

    def get_json_result_dir(self, args):
        json_result_dir = os.path.join(self.tc.td["LOG_DIR"], 'oeqa')
        if "OEQA_JSON_RESULT_DIR" in self.tc.td:
            json_result_dir = self.tc.td["OEQA_JSON_RESULT_DIR"]

        return json_result_dir

    def get_configuration(self, args):
        import platform
        from oeqa.utils.metadata import metadata_from_bb
        metadata = metadata_from_bb()
        configuration = {'TEST_TYPE': 'oeselftest',
                        'STARTTIME': args.test_start_time,
                        'MACHINE': self.tc.td["MACHINE"],
                        'HOST_DISTRO': oe.lsb.distro_identifier().replace(' ', '-'),
                        'HOST_NAME': metadata['hostname'],
                        'LAYERS': metadata['layers']}
        return configuration

    def get_result_id(self, configuration):
        return '%s_%s_%s_%s' % (configuration['TEST_TYPE'], configuration['HOST_DISTRO'], configuration['MACHINE'], configuration['STARTTIME'])

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
            configuration = self.get_configuration(args)
            rc.logDetails(self.get_json_result_dir(args),
                          configuration,
                          self.get_result_id(configuration))
            rc.logSummary(self.name)

        return rc

    def run(self, logger, args):
        self._process_args(logger, args)

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

            output_link = os.path.join(os.path.dirname(args.output_log),
                    "%s-results.log" % self.name)
            if os.path.lexists(output_link):
                os.remove(output_link)
            os.symlink(args.output_log, output_link)

        return rc

_executor_class = OESelftestTestContextExecutor
