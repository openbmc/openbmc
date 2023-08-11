#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import time
from oeqa.core.decorator import OETestTag
from oeqa.core.case import OEPTestResultTestCase
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars

def parse_values(content):
    for i in content:
        for v in ["PASS", "FAIL", "XPASS", "XFAIL", "UNRESOLVED", "UNSUPPORTED", "UNTESTED", "ERROR", "WARNING"]:
            if i.startswith(v + ": "):
                yield i[len(v) + 2:].strip(), v
                break

@OETestTag("toolchain-user", "toolchain-system")
class BinutilsCrossSelfTest(OESelftestTestCase, OEPTestResultTestCase):
    def test_binutils(self):
        self.run_binutils("binutils")

    def test_gas(self):
        self.run_binutils("gas")

    def test_ld(self):
        self.run_binutils("ld")

    def run_binutils(self, suite):
        features = []
        features.append('CHECK_TARGETS = "{0}"'.format(suite))
        self.write_config("\n".join(features))

        recipe = "binutils-cross-testsuite"
        bb_vars = get_bb_vars(["B", "TARGET_SYS", "T"], recipe)
        builddir, target_sys, tdir = bb_vars["B"], bb_vars["TARGET_SYS"], bb_vars["T"]

        start_time = time.time()

        bitbake("{0} -c check".format(recipe))

        end_time = time.time()

        sumspath = os.path.join(builddir, suite, "{0}.sum".format(suite))
        if not os.path.exists(sumspath):
            sumspath = os.path.join(builddir, suite, "testsuite", "{0}.sum".format(suite))
        logpath = os.path.splitext(sumspath)[0] + ".log"

        ptestsuite = "binutils-{}".format(suite) if suite != "binutils" else suite
        self.ptest_section(ptestsuite, duration = int(end_time - start_time), logfile = logpath)
        with open(sumspath, "r") as f:
            for test, result in parse_values(f):
                self.ptest_result(ptestsuite, test, result)

