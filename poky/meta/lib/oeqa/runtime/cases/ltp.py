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

    ltp_groups = ["math", "syscalls", "dio", "io", "mm", "ipc", "sched", "nptl", "pty", "containers", "controllers", "filecaps", "cap_bounds", "fcntl-locktests", "commands", "net.ipv6_lib", "input","fs_perms_simple", "cve", "crypto", "ima", "net.nfs", "net_stress.ipsec_icmp", "net.ipv6", "numa", "uevent", "ltp-aiodio.part1", "ltp-aiodio.part2", "ltp-aiodio.part3", "ltp-aiodio.part4"]

    ltp_fs = ["fs", "fs_bind"]
    # skip kernel cpuhotplug
    ltp_kernel = ["power_management_tests", "hyperthreading ", "kernel_misc", "hugetlb"]
    ltp_groups += ltp_fs

    def runltp(self, ltp_group):
            # LTP appends to log files, so ensure we start with a clean log
            self.target.deleteFiles("/opt/ltp/results/", ltp_group)

            cmd = '/opt/ltp/runltp -f %s -q -r /opt/ltp -l /opt/ltp/results/%s -I 1 -d /opt/ltp' % (ltp_group, ltp_group)

            starttime = time.time()
            (status, output) = self.target.run(cmd, timeout=1200)
            endtime = time.time()

            # status of 1 is 'just' tests failing. 255 likely was a command output timeout 
            if status and status != 1:
                msg = 'Command %s returned exit code %s' % (cmd, status)
                self.target.logger.warning(msg)

            # Write the console log to disk for convenience
            with open(os.path.join(self.ltptest_log_dir, "%s-raw.log" % ltp_group), 'w') as f:
                f.write(output)

            # Also put the console log into the test result JSON
            self.extras['ltpresult.rawlogs']['log'] = self.extras['ltpresult.rawlogs']['log'] + output

            # Copy the machine-readable test results locally so we can parse it
            dst = os.path.join(self.ltptest_log_dir, ltp_group)
            remote_src = "/opt/ltp/results/%s" % ltp_group 
            (status, output) = self.target.copyFrom(remote_src, dst, True)
            if status:
                msg = 'File could not be copied. Output: %s' % output
                self.target.logger.warning(msg)

            parser = LtpParser()
            results, sections  = parser.parse(dst)

            sections['duration'] = int(endtime-starttime)
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
