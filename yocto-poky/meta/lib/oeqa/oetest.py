# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# Main unittest module used by testimage.bbclass
# This provides the oeRuntimeTest base class which is inherited by all tests in meta/lib/oeqa/runtime.

# It also has some helper functions and it's responsible for actually starting the tests

import os, re, mmap
import unittest
import inspect
import subprocess
try:
    import bb
except ImportError:
    pass
import logging
from oeqa.utils.decorators import LogResults, gettag, getResults

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

def loadTests(tc, type="runtime"):
    if type == "runtime":
        # set the context object passed from the test class
        setattr(oeTest, "tc", tc)
        # set ps command to use
        setattr(oeRuntimeTest, "pscmd", "ps -ef" if oeTest.hasPackage("procps") else "ps")
        # prepare test suite, loader and runner
        suite = unittest.TestSuite()
    elif type == "sdk":
        # set the context object passed from the test class
        setattr(oeTest, "tc", tc)
    testloader = unittest.TestLoader()
    testloader.sortTestMethodsUsing = None
    suites = [testloader.loadTestsFromName(name) for name in tc.testslist]
    suites = filterByTagExp(suites, getattr(tc, "tagexp", None))

    def getTests(test):
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
                for t2 in getTests(t1):
                    yield t2

    # Determine dependencies between suites by looking for @skipUnlessPassed
    # method annotations. Suite A depends on suite B if any method in A
    # depends on a method on B.
    for suite in suites:
        suite.dependencies = []
        suite.depth = 0
        for test in getTests(suite):
            methodname = getattr(test, '_testMethodName', None)
            if methodname:
                method = getattr(test, methodname)
                depends_on = getattr(method, '_depends_on', None)
                if depends_on:
                    for dep_suite in suites:
                        if depends_on in [getattr(t, '_testMethodName', None) for t in getTests(dep_suite)]:
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
    suites.sort(cmp=lambda a,b: cmp((a.depth, a.index), (b.depth, b.index)))
    return testloader.suiteClass(suites)

_buffer = ""

def custom_verbose(msg, *args, **kwargs):
    global _buffer
    if msg[-1] != "\n":
        _buffer += msg
    else:
        _buffer += msg
        try:
            bb.plain(_buffer.rstrip("\n"), *args, **kwargs)
        except NameError:
            logger.info(_buffer.rstrip("\n"), *args, **kwargs)
        _buffer = ""

def runTests(tc, type="runtime"):

    suite = loadTests(tc, type)
    logger.info("Test modules  %s" % tc.testslist)
    if hasattr(tc, "tagexp") and tc.tagexp:
        logger.info("Filter test cases by tags: %s" % tc.tagexp)
    logger.info("Found %s tests" % suite.countTestCases())
    runner = unittest.TextTestRunner(verbosity=2)
    try:
        if bb.msg.loggerDefaultVerbose:
            runner.stream.write = custom_verbose
    except NameError:
        # Not in bb environment?
        pass
    result = runner.run(suite)

    return result

@LogResults
class oeTest(unittest.TestCase):

    longMessage = True

    @classmethod
    def hasPackage(self, pkg):
        for item in oeTest.tc.pkgmanifest.split('\n'):
            if re.match(pkg, item):
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
        res = getResults()
        # If a test fails or there is an exception dump
        # for QemuTarget only
        if (type(self.target).__name__ == "QemuTarget" and
                (self.id() in res.getErrorList() or
                self.id() in  res.getFailList())):
            self.tc.host_dumper.create_dir(self._testMethodName)
            self.tc.host_dumper.dump_host()
            self.target.target_dumper.dump_target(
                    self.tc.host_dumper.dump_dir)
            print ("%s dump data stored in %s" % (self._testMethodName,
                     self.tc.host_dumper.dump_dir))

        self.tearDownLocal()

    # Method to be run after tearDown and implemented by child classes
    def tearDownLocal(self):
        pass

    #TODO: use package_manager.py to install packages on any type of image
    def install_packages(self, packagelist):
        for package in packagelist:
            (status, result) = self.target.run("smart install -y "+package)
            if status != 0:
                return status

class oeSDKTest(oeTest):
    def __init__(self, methodName='runTest'):
        self.sdktestdir = oeSDKTest.tc.sdktestdir
        super(oeSDKTest, self).__init__(methodName)

    @classmethod
    def hasHostPackage(self, pkg):

        if re.search(pkg, oeTest.tc.hostpkgmanifest):
            return True
        return False

    def _run(self, cmd):
        return subprocess.check_output(". %s; " % self.tc.sdkenv + cmd, shell=True)

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
