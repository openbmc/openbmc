# LTP runtime
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
from oeqa.utils.logparser import LtpParser

class LtpTestBase(OERuntimeTestCase):

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

        cls.ltptest_log_dir_link = os.path.join(test_log_dir, 'ltp_log')
        cls.ltptest_log_dir = '%s.%s' % (cls.ltptest_log_dir_link, timestamp)
        os.makedirs(cls.ltptest_log_dir)

        cls.tc.target.run("mkdir -p /opt/ltp/results")

        if not hasattr(cls.tc, "extraresults"):
            cls.tc.extraresults = {}
        cls.extras = cls.tc.extraresults
        cls.extras['ltpresult.rawlogs'] = {'log': ""}

 
    @classmethod
    def ltp_finishup(cls):
        cls.extras['ltpresult.sections'] =  cls.sections

        # update symlink to ltp_log
        if os.path.exists(cls.ltptest_log_dir_link):
            os.remove(cls.ltptest_log_dir_link)
        os.symlink(os.path.basename(cls.ltptest_log_dir), cls.ltptest_log_dir_link)

        if cls.failmsg:
            cls.fail(cls.failmsg)

class LtpTest(LtpTestBase):

    ltp_groups = ["math", "syscalls", "dio", "io", "mm", "ipc", "sched", "nptl", "pty", "containers", "controllers", "filecaps", "cap_bounds", "fcntl-locktests", "connectors", "commands", "net.ipv6_lib", "input","fs_perms_simple"]

    ltp_fs = ["fs", "fsx", "fs_bind"]
    # skip kernel cpuhotplug
    ltp_kernel = ["power_management_tests", "hyperthreading ", "kernel_misc", "hugetlb"]
    ltp_groups += ltp_fs

    def runltp(self, ltp_group):
            cmd = '/opt/ltp/runltp -f %s -p -q -r /opt/ltp -l /opt/ltp/results/%s -I 1 -d /opt/ltp' % (ltp_group, ltp_group)
            starttime = time.time()
            (status, output) = self.target.run(cmd)
            endtime = time.time()

            with open(os.path.join(self.ltptest_log_dir, "%s-raw.log" % ltp_group), 'w') as f:
                f.write(output)

            self.extras['ltpresult.rawlogs']['log'] = self.extras['ltpresult.rawlogs']['log'] + output

            # copy nice log from DUT
            dst = os.path.join(self.ltptest_log_dir, "%s" %  ltp_group )
            remote_src = "/opt/ltp/results/%s" % ltp_group 
            (status, output) = self.target.copyFrom(remote_src, dst, True)
            msg = 'File could not be copied. Output: %s' % output
            if status:
                self.target.logger.warning(msg)

            parser = LtpParser()
            results, sections  = parser.parse(dst)

            runtime = int(endtime-starttime)
            sections['duration'] = runtime
            self.sections[ltp_group] =  sections

            failed_tests = {}
            for test in results:
                result = results[test]
                testname = ("ltpresult." + ltp_group + "." + test)
                self.extras[testname] = {'status': result}
                if result == 'FAILED':
                    failed_tests[ltp_group] = test 

            if failed_tests:
                self.failmsg = self.failmsg + "Failed ptests:\n%s" % pprint.pformat(failed_tests)

    # LTP runtime tests
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["ltp"])
    def test_ltp_help(self):
        (status, output) = self.target.run('/opt/ltp/runltp --help')
        msg = 'Failed to get ltp help. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['ltp.LtpTest.test_ltp_help'])
    def test_ltp_groups(self):
        for ltp_group in self.ltp_groups: 
            self.runltp(ltp_group)

    @OETestDepends(['ltp.LtpTest.test_ltp_groups'])
    def test_ltp_runltp_cve(self):
        self.runltp("cve")
