# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import sys
import os

import unittest
import logging
import os

logger = logging.getLogger("oeqa")
logger.setLevel(logging.INFO)
consoleHandler = logging.StreamHandler()
formatter = logging.Formatter('OEQATest: %(message)s')
consoleHandler.setFormatter(formatter)
logger.addHandler(consoleHandler)

def setup_sys_path():
    directory = os.path.dirname(os.path.abspath(__file__))
    oeqa_lib = os.path.realpath(os.path.join(directory, '../../../'))
    if not oeqa_lib in sys.path:
        sys.path.insert(0, oeqa_lib)

class TestBase(unittest.TestCase):
    def setUp(self):
        self.logger = logger
        directory = os.path.dirname(os.path.abspath(__file__))
        self.cases_path = os.path.join(directory, 'cases')

    def _testLoader(self, d={}, modules=[], tests=[], filters={}):
        from oeqa.core.context import OETestContext
        tc = OETestContext(d, self.logger)
        tc.loadTests(self.cases_path, modules=modules, tests=tests,
                     filters=filters)
        return tc
