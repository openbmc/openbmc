# LTP Stress runtime
#
# Copyright (c) 2019 MontaVista Software, LLC
#
# SPDX-License-Identifier: MIT
#

import time
import datetime
import pprint

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.data import skipIfQemu
from oeqa.utils.logparser import LtpParser

class LtpStressBase(OERuntimeTestCase):

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

        cls.ltptest_log_dir_link = os.path.join(test_log_dir, 'ltpstress_log')
        cls.ltptest_log_dir = '%s.%s' % (cls.ltptest_log_dir_link, timestamp)
        os.makedirs(cls.ltptest_log_dir)

        cls.tc.target.run("mkdir -p /opt/ltp/results")

        if not hasattr(cls.tc, "extraresults"):
            cls.tc.extraresults = {}
        cls.extras = cls.tc.extraresults
        cls.extras['ltpstressresult.rawlogs'] = {'log': ""}

 
    @classmethod
    def ltp_finishup(cls):
        cls.extras['ltpstressresult.sections'] =  cls.sections

        # update symlink to ltp_log
        if os.path.exists(cls.ltptest_log_dir_link):
            os.remove(cls.ltptest_log_dir_link)

        os.symlink(os.path.basename(cls.ltptest_log_dir), cls.ltptest_log_dir_link)

        if cls.failmsg:
            cls.fail(cls.failmsg)

class LtpStressTest(LtpStressBase):

    def runltp(self, stress_group):
            cmd = '/opt/ltp/runltp -f %s -p -q 2>@1 | tee /opt/ltp/results/%s' % (stress_group, stress_group)
            starttime = time.time()
            (status, output) = self.target.run(cmd)
            endtime = time.time()
            with open(os.path.join(self.ltptest_log_dir, "%s" % stress_group), 'w') as f:
                f.write(output)

            self.extras['ltpstressresult.rawlogs']['log'] = self.extras['ltpstressresult.rawlogs']['log'] + output

            parser = LtpParser()
            results, sections  = parser.parse(os.path.join(self.ltptest_log_dir, "%s" % stress_group))

            runtime = int(endtime-starttime)
            sections['duration'] = runtime
            self.sections[stress_group] =  sections
 
            failed_tests = {}
            for test in results:
                result = results[test]
                testname = ("ltpstressresult." + stress_group + "." + test)
                self.extras[testname] = {'status': result}
                if result == 'FAILED':
                    failed_tests[stress_group] = test 

            if failed_tests:
                self.failmsg = self.failmsg + "Failed ptests:\n%s" % pprint.pformat(failed_tests)

    # LTP stress runtime tests
    #
    @skipIfQemu()
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["ltp"])
    def test_ltp_stress(self):
        self.tc.target.run("sed -i -r 's/^fork12.*//' /opt/ltp/runtest/crashme")
        self.runltp('crashme')
