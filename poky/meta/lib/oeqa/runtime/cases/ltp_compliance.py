# LTP compliance runtime
#
# Copyright (c) 2019 MontaVista Software, LLC
#
# SPDX-License-Identifier: GPL-2.0-only
#

import time
import datetime
import pprint

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.utils.logparser import LtpComplianceParser

class LtpPosixBase(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        cls.ltp_startup()

    @classmethod
    def tearDownClass(cls):
        cls.ltp_finishup()

    @classmethod
    def ltp_startup(cls):
        cls.sections = {}
        cls.failmsg = ""
        test_log_dir = os.path.join(cls.td.get('WORKDIR', ''), 'testimage')
        timestamp = datetime.datetime.now().strftime('%Y%m%d%H%M%S')

        cls.ltptest_log_dir_link = os.path.join(test_log_dir, 'ltpcomp_log')
        cls.ltptest_log_dir = '%s.%s' % (cls.ltptest_log_dir_link, timestamp)
        os.makedirs(cls.ltptest_log_dir)

        cls.tc.target.run("mkdir -p /opt/ltp/results")

        if not hasattr(cls.tc, "extraresults"):
            cls.tc.extraresults = {}
        cls.extras = cls.tc.extraresults
        cls.extras['ltpposixresult.rawlogs'] = {'log': ""}

 
    @classmethod
    def ltp_finishup(cls):
        cls.extras['ltpposixresult.sections'] =  cls.sections

        # update symlink to ltp_log
        if os.path.exists(cls.ltptest_log_dir_link):
            os.remove(cls.ltptest_log_dir_link)

        os.symlink(os.path.basename(cls.ltptest_log_dir), cls.ltptest_log_dir_link)

        if cls.failmsg:
            cls.fail(cls.failmsg)

class LtpPosixTest(LtpPosixBase):
    posix_groups = ["AIO", "MEM", "MSG", "SEM", "SIG",  "THR", "TMR", "TPS"]

    def runltp(self, posix_group):
            cmd = "/opt/ltp/bin/run-posix-option-group-test.sh %s 2>@1 | tee /opt/ltp/results/%s" % (posix_group, posix_group)
            starttime = time.time()
            (status, output) = self.target.run(cmd)
            endtime = time.time()

            with open(os.path.join(self.ltptest_log_dir, "%s" % posix_group), 'w') as f:
                f.write(output)

            self.extras['ltpposixresult.rawlogs']['log'] = self.extras['ltpposixresult.rawlogs']['log'] + output

            parser = LtpComplianceParser()
            results, sections  = parser.parse(os.path.join(self.ltptest_log_dir, "%s" % posix_group))

            runtime = int(endtime-starttime)
            sections['duration'] = runtime
            self.sections[posix_group] =  sections
 
            failed_tests = {}
            for test in results:
                result = results[test]
                testname = ("ltpposixresult." + posix_group + "." + test)
                self.extras[testname] = {'status': result}
                if result == 'FAILED':
                    failed_tests[posix_group] = test 

            if failed_tests:
                self.failmsg = self.failmsg + "Failed ptests:\n%s" % pprint.pformat(failed_tests)

    # LTP Posix compliance runtime tests

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["ltp"])
    def test_posix_groups(self):
        for posix_group in self.posix_groups: 
            self.runltp(posix_group)
