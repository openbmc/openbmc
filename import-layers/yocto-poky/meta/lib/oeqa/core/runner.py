# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import time
import unittest
import logging

xmlEnabled = False
try:
    import xmlrunner
    from xmlrunner.result import _XMLTestResult as _TestResult
    from xmlrunner.runner import XMLTestRunner as _TestRunner
    xmlEnabled = True
except ImportError:
    # use the base runner instead
    from unittest import TextTestResult as _TestResult
    from unittest import TextTestRunner as _TestRunner

class OEStreamLogger(object):
    def __init__(self, logger):
        self.logger = logger
        self.buffer = ""

    def write(self, msg):
        if len(msg) > 1 and msg[0] != '\n':
            self.buffer += msg
        else:
            self.logger.log(logging.INFO, self.buffer.rstrip("\n"))
            self.buffer = ""

    def flush(self):
        for handler in self.logger.handlers:
            handler.flush()

class OETestResult(_TestResult):
    def __init__(self, tc, *args, **kwargs):
        super(OETestResult, self).__init__(*args, **kwargs)

        self.tc = tc

        self.tc._results['failures'] = self.failures
        self.tc._results['errors'] = self.errors
        self.tc._results['skipped'] = self.skipped
        self.tc._results['expectedFailures'] = self.expectedFailures

    def startTest(self, test):
        super(OETestResult, self).startTest(test)

class OETestRunner(_TestRunner):
    def __init__(self, tc, *args, **kwargs):
        if xmlEnabled:
            if not kwargs.get('output'):
                kwargs['output'] = os.path.join(os.getcwd(),
                        'TestResults_%s_%s' % (time.strftime("%Y%m%d%H%M%S"), os.getpid()))

        super(OETestRunner, self).__init__(*args, **kwargs)
        self.tc = tc
        self.resultclass = OETestResult

    # XXX: The unittest-xml-reporting package defines _make_result method instead
    # of _makeResult standard on unittest.
    if xmlEnabled:
        def _make_result(self):
            """
            Creates a TestResult object which will be used to store
            information about the executed tests.
            """
            # override in subclasses if necessary.
            return self.resultclass(self.tc,
                self.stream, self.descriptions, self.verbosity, self.elapsed_times
            )
    else:
        def _makeResult(self):
            return self.resultclass(self.tc, self.stream, self.descriptions,
                    self.verbosity)
