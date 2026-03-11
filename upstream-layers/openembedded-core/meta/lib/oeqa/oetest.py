#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# Main unittest module used by testimage.bbclass
# This provides the oeRuntimeTest base class which is inherited by all tests in meta/lib/oeqa/runtime.

# It also has some helper functions and it's responsible for actually starting the tests

import os, re, sys
import unittest
import inspect
import subprocess
import signal
import shutil
import functools
try:
    import bb
except ImportError:
    pass
import logging

import oeqa.runtime
# Exported test doesn't require sdkext
try:
    import oeqa.sdkext
except ImportError:
    pass
from oeqa.utils.decorators import LogResults, gettag

logger = logging.getLogger("BitBake")

def getVar(obj):
    #extend form dict, if a variable didn't exists, need find it in testcase
    class VarDict(dict):
        def __getitem__(self, key):
            return gettag(obj, key)
    return VarDict()

def checkTags(tc, tagexp):
    return eval(tagexp, None, getVar(tc))

def filterByTagExp(testsuite, tagexp):
    if not tagexp:
        return testsuite
    caseList = []
    for each in testsuite:
        if not isinstance(each, unittest.BaseTestSuite):
            if checkTags(each, tagexp):
                caseList.append(each)
        else:
            caseList.append(filterByTagExp(each, tagexp))
    return testsuite.__class__(caseList)

@LogResults
class oeTest(unittest.TestCase):

    longMessage = True

    @classmethod
    def hasPackage(self, pkg):
        """
        True if the full package name exists in the manifest, False otherwise.
        """
        return pkg in oeTest.tc.pkgmanifest

    @classmethod
    def hasPackageMatch(self, match):
        """
        True if match exists in the manifest as a regular expression substring,
        False otherwise.
        """
        for s in oeTest.tc.pkgmanifest:
            if re.match(match, s):
                return True
        return False

    @classmethod
    def hasFeature(self,feature):
        if feature in oeTest.tc.imagefeatures or \
                feature in oeTest.tc.distrofeatures:
            return True
        else:
            return False

class oeRuntimeTest(oeTest):
    def __init__(self, methodName='runTest'):
        self.target = oeRuntimeTest.tc.target
        super(oeRuntimeTest, self).__init__(methodName)

    def setUp(self):
        # Install packages in the DUT
        self.tc.install_uninstall_packages(self.id())

        # Check if test needs to run
        if self.tc.sigterm:
            self.fail("Got SIGTERM")
        elif (type(self.target).__name__ == "QemuTarget"):
            self.assertTrue(self.target.check(), msg = "Qemu not running?")

        self.setUpLocal()

    # a setup method before tests but after the class instantiation
    def setUpLocal(self):
        pass

    def tearDown(self):
        # Uninstall packages in the DUT
        self.tc.install_uninstall_packages(self.id(), False)
        self.tearDownLocal()

    # Method to be run after tearDown and implemented by child classes
    def tearDownLocal(self):
        pass

def getmodule(pos=2):
    # stack returns a list of tuples containg frame information
    # First element of the list the is current frame, caller is 1
    frameinfo = inspect.stack()[pos]
    modname = inspect.getmodulename(frameinfo[1])
    #modname = inspect.getmodule(frameinfo[0]).__name__
    return modname

def skipModule(reason, pos=2):
    modname = getmodule(pos)
    if modname not in oeTest.tc.testsrequired:
        raise unittest.SkipTest("%s: %s" % (modname, reason))
    else:
        raise Exception("\nTest %s wants to be skipped.\nReason is: %s" \
                "\nTest was required in TEST_SUITES, so either the condition for skipping is wrong" \
                "\nor the image really doesn't have the required feature/package when it should." % (modname, reason))

def skipModuleIf(cond, reason):

    if cond:
        skipModule(reason, 3)

def skipModuleUnless(cond, reason):

    if not cond:
        skipModule(reason, 3)

_buffer_logger = ""
def custom_verbose(msg, *args, **kwargs):
    global _buffer_logger
    if msg[-1] != "\n":
        _buffer_logger += msg
    else:
        _buffer_logger += msg
        try:
            bb.plain(_buffer_logger.rstrip("\n"), *args, **kwargs)
        except NameError:
            logger.info(_buffer_logger.rstrip("\n"), *args, **kwargs)
        _buffer_logger = ""

class TestContext(object):
    def __init__(self, d, exported=False):
        self.d = d

        self.testsuites = self._get_test_suites()

        if exported:
            path = [os.path.dirname(os.path.abspath(__file__))]
            extrapath = ""
        else:
            path = d.getVar("BBPATH").split(':')
            extrapath = "lib/oeqa"

        self.testslist = self._get_tests_list(path, extrapath)
        self.testsrequired = self._get_test_suites_required()

        self.filesdir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "runtime/files")
        self.corefilesdir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "files")
        self.imagefeatures = d.getVar("IMAGE_FEATURES").split()
        self.distrofeatures = d.getVar("DISTRO_FEATURES").split()

    # get testcase list from specified file
    # if path is a relative path, then relative to build/conf/
    def _read_testlist(self, fpath, builddir):
        if not os.path.isabs(fpath):
            fpath = os.path.join(builddir, "conf", fpath)
        if not os.path.exists(fpath):
            bb.fatal("No such manifest file: ", fpath)
        tcs = []
        for line in open(fpath).readlines():
            line = line.strip()
            if line and not line.startswith("#"):
                tcs.append(line)
        return " ".join(tcs)

    # return test list by type also filter if TEST_SUITES is specified
    def _get_tests_list(self, bbpath, extrapath):
        testslist = []

        type = self._get_test_namespace()

        # This relies on lib/ under each directory in BBPATH being added to sys.path
        # (as done by default in base.bbclass)
        for testname in self.testsuites:
            if testname != "auto":
                if testname.startswith("oeqa."):
                    testslist.append(testname)
                    continue
                found = False
                for p in bbpath:
                    if os.path.exists(os.path.join(p, extrapath, type, testname + ".py")):
                        testslist.append("oeqa." + type + "." + testname)
                        found = True
                        break
                    elif os.path.exists(os.path.join(p, extrapath, type, testname.split(".")[0] + ".py")):
                        testslist.append("oeqa." + type + "." + testname)
                        found = True
                        break
                if not found:
                    bb.fatal('Test %s specified in TEST_SUITES could not be found in lib/oeqa/runtime under BBPATH' % testname)

        if "auto" in self.testsuites:
            def add_auto_list(path):
                files = sorted([f for f in os.listdir(path) if f.endswith('.py') and not f.startswith('_')])
                for f in files:
                    module = 'oeqa.' + type + '.' + f[:-3]
                    if module not in testslist:
                        testslist.append(module)

            for p in bbpath:
                testpath = os.path.join(p, 'lib', 'oeqa', type)
                bb.debug(2, 'Searching for tests in %s' % testpath)
                if os.path.exists(testpath):
                    add_auto_list(testpath)

        return testslist

    def getTestModules(self):
        """
        Returns all the test modules in the testlist.
        """

        import pkgutil

        modules = []
        for test in self.testslist:
            if re.search(r"\w+\.\w+\.test_\S+", test):
                test = '.'.join(t.split('.')[:3])
            module = pkgutil.get_loader(test)
            modules.append(module)

        return modules

    def getModulefromID(self, test_id):
        """
        Returns the test module based on a test id.
        """

        module_name = ".".join(test_id.split(".")[:3])
        modules = self.getTestModules()
        for module in modules:
            if module.name == module_name:
                return module

        return None

    def getTests(self, test):
        '''Return all individual tests executed when running the suite.'''
        # Unfortunately unittest does not have an API for this, so we have
        # to rely on implementation details. This only needs to work
        # for TestSuite containing TestCase.
        method = getattr(test, '_testMethodName', None)
        if method:
            # leaf case: a TestCase
            yield test
        else:
            # Look into TestSuite.
            tests = getattr(test, '_tests', [])
            for t1 in tests:
                for t2 in self.getTests(t1):
                    yield t2

    def loadTests(self):
        setattr(oeTest, "tc", self)

        testloader = unittest.TestLoader()
        testloader.sortTestMethodsUsing = None
        suites = [testloader.loadTestsFromName(name) for name in self.testslist]
        suites = filterByTagExp(suites, getattr(self, "tagexp", None))

        # Determine dependencies between suites by looking for @skipUnlessPassed
        # method annotations. Suite A depends on suite B if any method in A
        # depends on a method on B.
        for suite in suites:
            suite.dependencies = []
            suite.depth = 0
            for test in self.getTests(suite):
                methodname = getattr(test, '_testMethodName', None)
                if methodname:
                    method = getattr(test, methodname)
                    depends_on = getattr(method, '_depends_on', None)
                    if depends_on:
                        for dep_suite in suites:
                            if depends_on in [getattr(t, '_testMethodName', None) for t in self.getTests(dep_suite)]:
                                if dep_suite not in suite.dependencies and \
                                   dep_suite is not suite:
                                    suite.dependencies.append(dep_suite)
                                break
                        else:
                            logger.warning("Test %s was declared as @skipUnlessPassed('%s') but that test is either not defined or not active. Will run the test anyway." %
                                    (test, depends_on))

        # Use brute-force topological sort to determine ordering. Sort by
        # depth (higher depth = must run later), with original ordering to
        # break ties.
        def set_suite_depth(suite):
            for dep in suite.dependencies:
                new_depth = set_suite_depth(dep) + 1
                if new_depth > suite.depth:
                    suite.depth = new_depth
            return suite.depth

        for index, suite in enumerate(suites):
            set_suite_depth(suite)
            suite.index = index

        def cmp(a, b):
            return (a > b) - (a < b)

        def cmpfunc(a, b):
            return cmp((a.depth, a.index), (b.depth, b.index))

        suites.sort(key=functools.cmp_to_key(cmpfunc))

        self.suite = testloader.suiteClass(suites)

        return self.suite

    def runTests(self):
        logger.info("Test modules  %s" % self.testslist)
        if hasattr(self, "tagexp") and self.tagexp:
            logger.info("Filter test cases by tags: %s" % self.tagexp)
        logger.info("Found %s tests" % self.suite.countTestCases())
        runner = unittest.TextTestRunner(verbosity=2)
        if 'bb' in sys.modules:
            runner.stream.write = custom_verbose

        return runner.run(self.suite)

class RuntimeTestContext(TestContext):
    def __init__(self, d, target, exported=False):
        super(RuntimeTestContext, self).__init__(d, exported)

        self.target = target

        self.pkgmanifest = {}
        manifest = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"),
                d.getVar("IMAGE_LINK_NAME") + ".manifest")
        nomanifest = d.getVar("IMAGE_NO_MANIFEST")
        if nomanifest is None or nomanifest != "1":
            try:
                with open(manifest) as f:
                    for line in f:
                        (pkg, arch, version) = line.strip().split()
                        self.pkgmanifest[pkg] = (version, arch)
            except IOError as e:
                bb.fatal("No package manifest file found. Did you build the image?\n%s" % e)

    def _get_test_namespace(self):
        return "runtime"

    def _get_test_suites(self):
        testsuites = []

        manifests = (self.d.getVar("TEST_SUITES_MANIFEST") or '').split()
        if manifests:
            for manifest in manifests:
                testsuites.extend(self._read_testlist(manifest,
                                  self.d.getVar("TOPDIR")).split())

        else:
            testsuites = self.d.getVar("TEST_SUITES").split()

        return testsuites

    def _get_test_suites_required(self):
        return [t for t in self.d.getVar("TEST_SUITES").split() if t != "auto"]

    def extract_packages(self):
        """
        Find packages that will be needed during runtime.
        """

        modules = self.getTestModules()
        bbpaths = self.d.getVar("BBPATH").split(":")

        shutil.rmtree(self.d.getVar("TEST_EXTRACTED_DIR"))
        shutil.rmtree(self.d.getVar("TEST_PACKAGED_DIR"))
        for module in modules:
            json_file = self._getJsonFile(module)
            if json_file:
                needed_packages = self._getNeededPackages(json_file)
                self._perform_package_extraction(needed_packages)

    def _perform_package_extraction(self, needed_packages):
        """
        Extract packages that will be needed during runtime.
        """

        import oe.path

        extracted_path = self.d.getVar("TEST_EXTRACTED_DIR")
        packaged_path = self.d.getVar("TEST_PACKAGED_DIR")

        for key,value in needed_packages.items():
            packages = ()
            if isinstance(value, dict):
                packages = (value, )
            elif isinstance(value, list):
                packages = value
            else:
                bb.fatal("Failed to process needed packages for %s; "
                         "Value must be a dict or list" % key)

            for package in packages:
                pkg = package["pkg"]
                rm = package.get("rm", False)
                extract = package.get("extract", True)
                if extract:
                    dst_dir = os.path.join(extracted_path, pkg)
                else:
                    dst_dir = os.path.join(packaged_path)

                # Extract package and copy it to TEST_EXTRACTED_DIR
                pkg_dir = self._extract_in_tmpdir(pkg)
                if extract:

                    # Same package used for more than one test,
                    # don't need to extract again.
                    if os.path.exists(dst_dir):
                        continue
                    oe.path.copytree(pkg_dir, dst_dir)
                    shutil.rmtree(pkg_dir)

                # Copy package to TEST_PACKAGED_DIR
                else:
                    self._copy_package(pkg)

    def _getJsonFile(self, module):
        """
        Returns the path of the JSON file for a module, empty if doesn't exitst.
        """

        module_file = module.path
        json_file = "%s.json" % module_file.rsplit(".", 1)[0]
        if os.path.isfile(module_file) and os.path.isfile(json_file):
            return json_file
        else:
            return ""

    def _getNeededPackages(self, json_file, test=None):
        """
        Returns a dict with needed packages based on a JSON file.


        If a test is specified it will return the dict just for that test.
        """

        import json

        needed_packages = {}

        with open(json_file) as f:
            test_packages = json.load(f)
        for key,value in test_packages.items():
            needed_packages[key] = value

        if test:
            if test in needed_packages:
                needed_packages = needed_packages[test]
            else:
                needed_packages = {}

        return needed_packages

    def _extract_in_tmpdir(self, pkg):
        """"
        Returns path to a temp directory where the package was
        extracted without dependencies.
        """

        from oeqa.utils.package_manager import get_package_manager

        pkg_path = os.path.join(self.d.getVar("TEST_INSTALL_TMP_DIR"), pkg)
        pm = get_package_manager(self.d, pkg_path)
        extract_dir = pm.extract(pkg)
        shutil.rmtree(pkg_path)

        return extract_dir

    def _copy_package(self, pkg):
        """
        Copy the RPM, DEB or IPK package to dst_dir
        """

        from oeqa.utils.package_manager import get_package_manager

        pkg_path = os.path.join(self.d.getVar("TEST_INSTALL_TMP_DIR"), pkg)
        dst_dir = self.d.getVar("TEST_PACKAGED_DIR")
        pm = get_package_manager(self.d, pkg_path)
        pkg_info = pm.package_info(pkg)
        file_path = pkg_info[pkg]["filepath"]
        shutil.copy2(file_path, dst_dir)
        shutil.rmtree(pkg_path)

    def install_uninstall_packages(self, test_id, pkg_dir, install):
        """
        Check if the test requires a package and Install/Uninstall it in the DUT
        """

        test = test_id.split(".")[4]
        module = self.getModulefromID(test_id)
        json = self._getJsonFile(module)
        if json:
            needed_packages = self._getNeededPackages(json, test)
            if needed_packages:
                self._install_uninstall_packages(needed_packages, pkg_dir, install)

    def _install_uninstall_packages(self, needed_packages, pkg_dir, install=True):
        """
        Install/Uninstall packages in the DUT without using a package manager
        """

        if isinstance(needed_packages, dict):
            packages = [needed_packages]
        elif isinstance(needed_packages, list):
            packages = needed_packages

        for package in packages:
            pkg = package["pkg"]
            rm = package.get("rm", False)
            extract = package.get("extract", True)
            src_dir = os.path.join(pkg_dir, pkg)

            # Install package
            if install and extract:
                self.target.connection.copy_dir_to(src_dir, "/")

            # Uninstall package
            elif not install and rm:
                self.target.connection.delete_dir_structure(src_dir, "/")

class ImageTestContext(RuntimeTestContext):
    def __init__(self, d, target, host_dumper):
        super(ImageTestContext, self).__init__(d, target)

        self.tagexp = d.getVar("TEST_SUITES_TAGS")

        self.host_dumper = host_dumper

        self.sigterm = False
        self.origsigtermhandler = signal.getsignal(signal.SIGTERM)
        signal.signal(signal.SIGTERM, self._sigterm_exception)

    def _sigterm_exception(self, signum, stackframe):
        bb.warn("TestImage received SIGTERM, shutting down...")
        self.sigterm = True
        self.target.stop()

    def install_uninstall_packages(self, test_id, install=True):
        """
        Check if the test requires a package and Install/Uninstall it in the DUT
        """

        pkg_dir = self.d.getVar("TEST_EXTRACTED_DIR")
        super(ImageTestContext, self).install_uninstall_packages(test_id, pkg_dir, install)

class ExportTestContext(RuntimeTestContext):
    def __init__(self, d, target, exported=False, parsedArgs={}):
        """
        This class is used when exporting tests and when are executed outside OE environment.

        parsedArgs can contain the following:
            - tag:      Filter test by tag.
        """
        super(ExportTestContext, self).__init__(d, target, exported)

        tag = parsedArgs.get("tag", None)
        self.tagexp = tag if tag != None else d.getVar("TEST_SUITES_TAGS")

        self.sigterm = None

    def install_uninstall_packages(self, test_id, install=True):
        """
        Check if the test requires a package and Install/Uninstall it in the DUT
        """

        export_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
        extracted_dir = self.d.getVar("TEST_EXPORT_EXTRACTED_DIR")
        pkg_dir = os.path.join(export_dir, extracted_dir)
        super(ExportTestContext, self).install_uninstall_packages(test_id, pkg_dir, install)
