# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import re
import sys
import unittest
import inspect

from oeqa.core.utils.path import findFile
from oeqa.core.utils.test import getSuiteModules, getCaseID

from oeqa.core.exception import OEQATestNotFound
from oeqa.core.case import OETestCase
from oeqa.core.decorator import decoratorClasses, OETestDecorator, \
        OETestFilter, OETestDiscover

# When loading tests, the unittest framework stores any exceptions and
# displays them only when the run method is called.
#
# For our purposes, it is better to raise the exceptions in the loading
# step rather than waiting to run the test suite.
#
# Generate the function definition because this differ across python versions
# Python >= 3.4.4 uses tree parameters instead four but for example Python 3.5.3
# ueses four parameters so isn't incremental.
_failed_test_args = inspect.getfullargspec(unittest.loader._make_failed_test).args
exec("""def _make_failed_test(%s): raise exception""" % ', '.join(_failed_test_args))
unittest.loader._make_failed_test = _make_failed_test

def _find_duplicated_modules(suite, directory):
    for module in getSuiteModules(suite):
        path = findFile('%s.py' % module, directory)
        if path:
            raise ImportError("Duplicated %s module found in %s" % (module, path))

def _built_modules_dict(modules):
    modules_dict = {}

    if modules == None:
        return modules_dict

    for module in modules:
        # Assumption: package and module names do not contain upper case
        # characters, whereas class names do
        m = re.match(r'^(\w+)(?:\.(\w[^.]*)(?:\.([^.]+))?)?$', module, flags=re.ASCII)

        module_name, class_name, test_name = m.groups()

        if module_name and module_name not in modules_dict:
            modules_dict[module_name] = {}
        if class_name and class_name not in modules_dict[module_name]:
            modules_dict[module_name][class_name] = []
        if test_name and test_name not in modules_dict[module_name][class_name]:
            modules_dict[module_name][class_name].append(test_name)

    return modules_dict

class OETestLoader(unittest.TestLoader):
    caseClass = OETestCase

    kwargs_names = ['testMethodPrefix', 'sortTestMethodUsing', 'suiteClass',
            '_top_level_dir']

    def __init__(self, tc, module_paths, modules, tests, modules_required,
            filters, *args, **kwargs):
        self.tc = tc

        self.modules = _built_modules_dict(modules)

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

        super(OETestLoader, self).__init__()

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
        # XXX; If the module has more than one namespace only use
        # the first to support run the whole module specifying the
        # <module_name>.[test_class].[test_name]
        module_name_small = case.__module__.split('.')[0]
        module_name = case.__module__

        class_name = case.__class__.__name__
        test_name = case._testMethodName

        # 'auto' is a reserved key word to run test cases automatically
        # warn users if their test case belong to a module named 'auto'
        if module_name_small == "auto":
            bb.warn("'auto' is a reserved key word for TEST_SUITES. "
                    "But test case '%s' is detected to belong to auto module. "
                    "Please condier using a new name for your module." % str(case))

        # check if case belongs to any specified module
        # if 'auto' is specified, such check is skipped
        if self.modules and not 'auto' in self.modules:
            module = None
            try:
                module = self.modules[module_name_small]
            except KeyError:
                try:
                    module = self.modules[module_name]
                except KeyError:
                    return True

            if module:
                if not class_name in module:
                    return True

                if module[class_name]:
                    if test_name not in module[class_name]:
                        return True

        # Decorator filters
        if self.filters and isinstance(case, OETestCase):
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
        if not hasattr(testCaseClass, '__oeqa_loader') and \
                issubclass(testCaseClass, OETestCase):
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
        if isinstance(case, OETestCase):
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
        if not issubclass(testCaseClass, unittest.case.TestCase):
            raise TypeError("Test %s is not derived from %s" % \
                    (testCaseClass.__name__, unittest.case.TestCase.__name__))

        testCaseNames = self.getTestCaseNames(testCaseClass)
        if not testCaseNames and hasattr(testCaseClass, 'runTest'):
            testCaseNames = ['runTest']

        suite = []
        for tcName in testCaseNames:
            case = self._getTestCase(testCaseClass, tcName)
            # Filer by case id
            if not (self.tests and not 'auto' in self.tests
                    and not getCaseID(case) in self.tests):
                self._handleTestCaseDecorators(case)

                # Filter by decorators
                if not self._filterTest(case):
                    self._registerTestCase(case)
                    suite.append(case)

        return self.suiteClass(suite)

    def _required_modules_validation(self):
        """
            Search in Test context registry if a required
            test is found, raise an exception when not found.
        """

        for module in self.modules_required:
            found = False

            # The module name is splitted to only compare the
            # first part of a test case id.
            comp_len = len(module.split('.'))
            for case in self.tc._registry['cases']:
                case_comp = '.'.join(case.split('.')[0:comp_len])
                if module == case_comp:
                    found = True
                    break

            if not found:
                raise OEQATestNotFound("Not found %s in loaded test cases" % \
                        module)

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

        if self.modules_required:
            self._required_modules_validation()

        return self.suiteClass(cases) if cases else big_suite

    def _filterModule(self, module):
        if module.__name__ in sys.builtin_module_names:
            msg = 'Tried to import %s test module but is a built-in'
            raise ImportError(msg % module.__name__)

        # XXX; If the module has more than one namespace only use
        # the first to support run the whole module specifying the
        # <module_name>.[test_class].[test_name]
        module_name_small = module.__name__.split('.')[0]
        module_name = module.__name__

        # Normal test modules are loaded if no modules were specified,
        # if module is in the specified module list or if 'auto' is in
        # module list.
        # Underscore modules are loaded only if specified in module list.
        load_module = True if not module_name.startswith('_') \
                              and (not self.modules \
                                   or module_name in self.modules \
                                   or module_name_small in self.modules \
                                   or 'auto' in self.modules) \
                           else False

        load_underscore = True if module_name.startswith('_') \
                                  and (module_name in self.modules or \
                                  module_name_small in self.modules) \
                               else False

        return (load_module, load_underscore)


    # XXX After Python 3.5, remove backward compatibility hacks for
    # use_load_tests deprecation via *args and **kws.  See issue 16662.
    if sys.version_info >= (3,5):
        def loadTestsFromModule(self, module, *args, pattern=None, **kws):
            """
                Returns a suite of all tests cases contained in module.
            """
            load_module, load_underscore = self._filterModule(module)

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
            load_module, load_underscore = self._filterModule(module)

            if load_module or load_underscore:
                return super(OETestLoader, self).loadTestsFromModule(
                        module, use_load_tests)
            else:
                return self.suiteClass()
