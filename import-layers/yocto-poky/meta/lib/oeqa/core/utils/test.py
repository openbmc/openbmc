# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import inspect
import unittest

def getSuiteCases(suite):
    """
        Returns individual test from a test suite.
    """
    tests = []

    if isinstance(suite, unittest.TestCase):
        tests.append(suite)
    elif isinstance(suite, unittest.suite.TestSuite):
        for item in suite:
            tests.extend(getSuiteCases(item))

    return tests

def getSuiteModules(suite):
    """
        Returns modules in a test suite.
    """
    modules = set()
    for test in getSuiteCases(suite):
        modules.add(getCaseModule(test))
    return modules

def getSuiteCasesInfo(suite, func):
    """
        Returns test case info from suite. Info is fetched from func.
    """
    tests = []
    for test in getSuiteCases(suite):
        tests.append(func(test))
    return tests

def getSuiteCasesNames(suite):
    """
        Returns test case names from suite.
    """
    return getSuiteCasesInfo(suite, getCaseMethod)

def getSuiteCasesIDs(suite):
    """
        Returns test case ids from suite.
    """
    return getSuiteCasesInfo(suite, getCaseID)

def getSuiteCasesFiles(suite):
    """
        Returns test case files paths from suite.
    """
    return getSuiteCasesInfo(suite, getCaseFile)

def getCaseModule(test_case):
    """
        Returns test case module name.
    """
    return test_case.__module__

def getCaseClass(test_case):
    """
        Returns test case class name.
    """
    return test_case.__class__.__name__

def getCaseID(test_case):
    """
        Returns test case complete id.
    """
    return test_case.id()

def getCaseFile(test_case):
    """
        Returns test case file path.
    """
    return inspect.getsourcefile(test_case.__class__)

def getCaseMethod(test_case):
    """
        Returns test case method name.
    """
    return getCaseID(test_case).split('.')[-1]
