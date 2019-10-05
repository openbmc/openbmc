#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import base64
import zlib
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
        if hasattr(clss, 'setUpHooker') and callable(getattr(clss, 'setUpHooker')):
            clss.setUpHooker()
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

class OEPTestResultTestCase:
    """
    Mix-in class to provide functions to make interacting with extraresults for
    the purposes of storing ptestresult data.
    """
    @staticmethod
    def _compress_log(log):
        logdata = log.encode("utf-8") if isinstance(log, str) else log
        logdata = zlib.compress(logdata)
        logdata = base64.b64encode(logdata).decode("utf-8")
        return {"compressed" : logdata}

    def ptest_rawlog(self, log):
        if not hasattr(self, "extraresults"):
            self.extraresults = {"ptestresult.sections" : {}}
        self.extraresults["ptestresult.rawlogs"] = {"log" : self._compress_log(log)}

    def ptest_section(self, section, duration = None, log = None, logfile = None, exitcode = None):
        if not hasattr(self, "extraresults"):
            self.extraresults = {"ptestresult.sections" : {}}

        sections = self.extraresults.get("ptestresult.sections")
        if section not in sections:
            sections[section] = {}

        if log is not None:
            sections[section]["log"] = self._compress_log(log)
        elif logfile is not None:
            with open(logfile, "rb") as f:
                sections[section]["log"] = self._compress_log(f.read())

        if duration is not None:
            sections[section]["duration"] = duration
        if exitcode is not None:
            sections[section]["exitcode"] = exitcode

    def ptest_result(self, section, test, result):
        if not hasattr(self, "extraresults"):
            self.extraresults = {"ptestresult.sections" : {}}

        sections = self.extraresults.get("ptestresult.sections")
        if section not in sections:
            sections[section] = {}
        resultname = "ptestresult.{}.{}".format(section, test)
        self.extraresults[resultname] = {"status" : result}

