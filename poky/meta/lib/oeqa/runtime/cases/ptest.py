#
# SPDX-License-Identifier: MIT
#

import os
import unittest
import pprint
import datetime

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.utils.logparser import PtestParser

class PtestRunnerTest(OERuntimeTestCase):

    @skipIfNotFeature('ptest', 'Test requires ptest to be in DISTRO_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['ptest-runner'])
    @unittest.expectedFailure
    def test_ptestrunner_expectfail(self):
        if not self.td.get('PTEST_EXPECT_FAILURE'):
            self.skipTest('Cannot run ptests with @expectedFailure as ptests are required to pass')
        self.do_ptestrunner()

    @skipIfNotFeature('ptest', 'Test requires ptest to be in DISTRO_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['ptest-runner'])
    def test_ptestrunner_expectsuccess(self):
        if self.td.get('PTEST_EXPECT_FAILURE'):
            self.skipTest('Cannot run ptests without @expectedFailure as ptests are expected to fail')
        self.do_ptestrunner()

    def do_ptestrunner(self):
        status, output = self.target.run('which ptest-runner', 0)
        if status != 0:
            self.skipTest("No -ptest packages are installed in the image")

        test_log_dir = self.td.get('TEST_LOG_DIR', '')
        # The TEST_LOG_DIR maybe NULL when testimage is added after
        # testdata.json is generated.
        if not test_log_dir:
            test_log_dir = os.path.join(self.td.get('WORKDIR', ''), 'testimage')
        # Don't use self.td.get('DATETIME'), it's from testdata.json, not
        # up-to-date, and may cause "File exists" when re-reun.
        timestamp = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
        ptest_log_dir_link = os.path.join(test_log_dir, 'ptest_log')
        ptest_log_dir = '%s.%s' % (ptest_log_dir_link, timestamp)
        ptest_runner_log = os.path.join(ptest_log_dir, 'ptest-runner.log')

        status, output = self.target.run('ptest-runner', 0)
        os.makedirs(ptest_log_dir)
        with open(ptest_runner_log, 'w') as f:
            f.write(output)

        # status != 0 is OK since some ptest tests may fail
        self.assertTrue(status != 127, msg="Cannot execute ptest-runner!")

        if not hasattr(self.tc, "extraresults"):
            self.tc.extraresults = {}
        extras = self.tc.extraresults
        extras['ptestresult.rawlogs'] = {'log': output}

        # Parse and save results
        parser = PtestParser()
        results, sections = parser.parse(ptest_runner_log)
        parser.results_as_files(ptest_log_dir)
        if os.path.exists(ptest_log_dir_link):
            # Remove the old link to create a new one
            os.remove(ptest_log_dir_link)
        os.symlink(os.path.basename(ptest_log_dir), ptest_log_dir_link)

        extras['ptestresult.sections'] = sections

        trans = str.maketrans("()", "__")
        for section in results:
            for test in results[section]:
                result = results[section][test]
                testname = "ptestresult." + (section or "No-section") + "." + "_".join(test.translate(trans).split())
                extras[testname] = {'status': result}

        failed_tests = {}

        for section in sections:
            if 'exitcode' in sections[section].keys():
                failed_tests[section] = sections[section]["log"]

        for section in results:
            failed_testcases = [ "_".join(test.translate(trans).split()) for test in results[section] if results[section][test] == 'FAILED' ]
            if failed_testcases:
                failed_tests[section] = failed_testcases

        failmsg = ""
        status, output = self.target.run('dmesg | grep "Killed process"', 0)
        if output:
            failmsg = "ERROR: Processes were killed by the OOM Killer:\n%s\n" % output

        if failed_tests:
            failmsg = failmsg + "Failed ptests:\n%s" % pprint.pformat(failed_tests)

        if failmsg:
            self.fail(failmsg)
