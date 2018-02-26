# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import unittest

from oeqa.core.exception import OEQAMissingVariable

def _validate_td_vars(td, td_vars, type_msg):
    if td_vars:
        for v in td_vars:
            if not v in td:
                raise OEQAMissingVariable("Test %s need %s variable but"\
                        " isn't into td" % (type_msg, v))

class OETestCase(unittest.TestCase):
    # TestContext and Logger instance set by OETestLoader.
    tc = None
    logger = None

    # td has all the variables needed by the test cases
    # is the same across all the test cases.
    td = None

    # td_vars has the variables needed by a test class
    # or test case instance, if some var isn't into td a
    # OEQAMissingVariable exception is raised
    td_vars = None

    @classmethod
    def _oeSetUpClass(clss):
        _validate_td_vars(clss.td, clss.td_vars, "class")
        clss.setUpClassMethod()

    @classmethod
    def _oeTearDownClass(clss):
        clss.tearDownClassMethod()

    def _oeSetUp(self):
        for d in self.decorators:
            d.setUpDecorator()
        self.setUpMethod()

    def _oeTearDown(self):
        for d in self.decorators:
            d.tearDownDecorator()
        self.tearDownMethod()
