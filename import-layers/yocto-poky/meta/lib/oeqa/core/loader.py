# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import sys
import unittest

from oeqa.core.utils.path import findFile
from oeqa.core.utils.test import getSuiteModules, getCaseID

from oeqa.core.case import OETestCase
from oeqa.core.decorator import decoratorClasses, OETestDecorator, \
        OETestFilter, OETestDiscover

def _make_failed_test(classname, methodname, exception, suiteClass):
    """
        When loading tests unittest framework stores the exception in a new
        class created for be displayed into run().

        For our purposes will be better to raise the exception in loading 
        step instead of wait to run the test suite.
    """
    raise exception
unittest.loader._make_failed_test = _make_failed_test

def _find_duplicated_modules(suite, directory):
    for module in getSuiteModules(suite):
        path = findFile('%s.py' % module, directory)
        if path:
            raise ImportError("Duplicated %s module found in %s" % (module, path))

class OETestLoader(unittest.TestLoader):
    caseClass = OETestCase

    kwargs_names = ['testMethodPrefix', 'sortTestMethodUsing', 'suiteClass',
            '_top_level_dir']

    def __init__(self, tc, module_paths, modules, tests, modules_required,
            filters, *args, **kwargs):
        self.tc = tc

        self.modules = modules
        self.tests = tests
        self.modules_required = modules_required

        self.filters = filters
        self.decorator_filters = [d for d in decoratorClasses if \
                issubclass(d, OETestFilter)]
        self._validateFilters(self.filters, self.decorator_filters)
        self.used_filters = [d for d in self.decorator_filters
                             for f in self.filters
                             if f in d.attrs]

        if isinstance(module_paths, str):
            module_paths = [module_paths]
        elif not isinstance(module_paths, list):
            raise TypeError('module_paths must be a str or a list of str')
        self.module_paths = module_paths

        for kwname in self.kwargs_names:
            if kwname in kwargs:
                setattr(self, kwname, kwargs[kwname])

        self._patchCaseClass(self.caseClass)

    def _patchCaseClass(self, testCaseClass):
        # Adds custom attributes to the OETestCase class
        setattr(testCaseClass, 'tc', self.tc)
        setattr(testCaseClass, 'td', self.tc.td)
        setattr(testCaseClass, 'logger', self.tc.logger)

    def _validateFilters(self, filters, decorator_filters):
        # Validate if filter isn't empty
        for key,value in filters.items():
            if not value:
                raise TypeError("Filter %s specified is empty" % key)

        # Validate unique attributes
        attr_filters = [attr for clss in decorator_filters \
                                for attr in clss.attrs]
        dup_attr = [attr for attr in attr_filters
                    if attr_filters.count(attr) > 1]
        if dup_attr:
            raise TypeError('Detected duplicated attribute(s) %s in filter'
                            ' decorators' % ' ,'.join(dup_attr))

        # Validate if filter is supported
        for f in filters:
            if f not in attr_filters:
                classes = ', '.join([d.__name__ for d in decorator_filters])
                raise TypeError('Found "%s" filter but not declared in any of '
                                '%s decorators' % (f, classes))

    def _registerTestCase(self, case):
        case_id = case.id()
        self.tc._registry['cases'][case_id] = case

    def _handleTestCaseDecorators(self, case):
        def _handle(obj):
            if isinstance(obj, OETestDecorator):
                if not obj.__class__ in decoratorClasses:
                    raise Exception("Decorator %s isn't registered" \
                            " in decoratorClasses." % obj.__name__)
                obj.bind(self.tc._registry, case)

        def _walk_closure(obj):
            if hasattr(obj, '__closure__') and obj.__closure__:
                for f in obj.__closure__:
                    obj = f.cell_contents
                    _handle(obj)
                    _walk_closure(obj)
        method = getattr(case, case._testMethodName, None)
        _walk_closure(method)

    def _filterTest(self, case):
        """
            Returns True if test case must be filtered, False otherwise.
        """
        if self.filters:
            filters = self.filters.copy()
            case_decorators = [cd for cd in case.decorators
                               if cd.__class__ in self.used_filters]

            # Iterate over case decorators to check if needs to be filtered.
            for cd in case_decorators:
                if cd.filtrate(filters):
                    return True

            # Case is missing one or more decorators for all the filters
            # being used, so filter test case.
            if filters:
                return True

        return False

    def _getTestCase(self, testCaseClass, tcName):
        if not hasattr(testCaseClass, '__oeqa_loader'):
            # In order to support data_vars validation
            # monkey patch the default setUp/tearDown{Class} to use
            # the ones provided by OETestCase
            setattr(testCaseClass, 'setUpClassMethod',
                    getattr(testCaseClass, 'setUpClass'))
            setattr(testCaseClass, 'tearDownClassMethod',
                    getattr(testCaseClass, 'tearDownClass'))
            setattr(testCaseClass, 'setUpClass',
                    testCaseClass._oeSetUpClass)
            setattr(testCaseClass, 'tearDownClass',
                    testCaseClass._oeTearDownClass)

            # In order to support decorators initialization
            # monkey patch the default setUp/tearDown to use
            # a setUpDecorators/tearDownDecorators that methods
            # will call setUp/tearDown original methods.
            setattr(testCaseClass, 'setUpMethod',
                    getattr(testCaseClass, 'setUp')) 
            setattr(testCaseClass, 'tearDownMethod',
                    getattr(testCaseClass, 'tearDown'))
            setattr(testCaseClass, 'setUp', testCaseClass._oeSetUp)
            setattr(testCaseClass, 'tearDown', testCaseClass._oeTearDown)

            setattr(testCaseClass, '__oeqa_loader', True)

        case = testCaseClass(tcName)
        setattr(case, 'decorators', [])

        return case

    def loadTestsFromTestCase(self, testCaseClass):
        """
            Returns a suite of all tests cases contained in testCaseClass.
        """
        if issubclass(testCaseClass, unittest.suite.TestSuite):
            raise TypeError("Test cases should not be derived from TestSuite." \
                                " Maybe you meant to derive %s from TestCase?" \
                                % testCaseClass.__name__)
        if not issubclass(testCaseClass, self.caseClass):
            raise TypeError("Test %s is not derived from %s" % \
                    (testCaseClass.__name__, self.caseClass.__name__))

        testCaseNames = self.getTestCaseNames(testCaseClass)
        if not testCaseNames and hasattr(testCaseClass, 'runTest'):
            testCaseNames = ['runTest']

        suite = []
        for tcName in testCaseNames:
            case = self._getTestCase(testCaseClass, tcName)
            # Filer by case id
            if not (self.tests and not 'all' in self.tests
                    and not getCaseID(case) in self.tests):
                self._handleTestCaseDecorators(case)

                # Filter by decorators
                if not self._filterTest(case):
                    self._registerTestCase(case)
                    suite.append(case)

        return self.suiteClass(suite)

    def discover(self):
        big_suite = self.suiteClass()
        for path in self.module_paths:
            _find_duplicated_modules(big_suite, path)
            suite = super(OETestLoader, self).discover(path,
                    pattern='*.py', top_level_dir=path)
            big_suite.addTests(suite)

        cases = None
        discover_classes = [clss for clss in decoratorClasses
                            if issubclass(clss, OETestDiscover)]
        for clss in discover_classes:
            cases = clss.discover(self.tc._registry)

        return self.suiteClass(cases) if cases else big_suite

    # XXX After Python 3.5, remove backward compatibility hacks for
    # use_load_tests deprecation via *args and **kws.  See issue 16662.
    if sys.version_info >= (3,5):
        def loadTestsFromModule(self, module, *args, pattern=None, **kws):
            """
                Returns a suite of all tests cases contained in module.
            """
            if module.__name__ in sys.builtin_module_names:
                msg = 'Tried to import %s test module but is a built-in'
                raise ImportError(msg % module.__name__)

            # Normal test modules are loaded if no modules were specified,
            # if module is in the specified module list or if 'all' is in
            # module list.
            # Underscore modules are loaded only if specified in module list.
            load_module = True if not module.__name__.startswith('_') \
                                  and (not self.modules \
                                       or module.__name__ in self.modules \
                                       or 'all' in self.modules) \
                               else False

            load_underscore = True if module.__name__.startswith('_') \
                                      and module.__name__ in self.modules \
                                   else False

            if load_module or load_underscore:
                return super(OETestLoader, self).loadTestsFromModule(
                        module, *args, pattern=pattern, **kws)
            else:
                return self.suiteClass()
    else:
        def loadTestsFromModule(self, module, use_load_tests=True):
            """
                Returns a suite of all tests cases contained in module.
            """
            if module.__name__ in sys.builtin_module_names:
                msg = 'Tried to import %s test module but is a built-in'
                raise ImportError(msg % module.__name__)

            # Normal test modules are loaded if no modules were specified,
            # if module is in the specified module list or if 'all' is in
            # module list.
            # Underscore modules are loaded only if specified in module list.
            load_module = True if not module.__name__.startswith('_') \
                                  and (not self.modules \
                                       or module.__name__ in self.modules \
                                       or 'all' in self.modules) \
                               else False

            load_underscore = True if module.__name__.startswith('_') \
                                      and module.__name__ in self.modules \
                                   else False

            if load_module or load_underscore:
                return super(OETestLoader, self).loadTestsFromModule(
                        module, use_load_tests)
            else:
                return self.suiteClass()
