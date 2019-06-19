#!/usr/bin/env python
#
# Authors:  Cristina Moraru <cristina.moraru@intel.com>
#           Alexandru Cornea <alexandru.cornea@intel.com>

import string
from time import sleep
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.core.decorator.data import skipIfDataVar, skipIfNotDataVar
import bb
blacklist = ["/usr/bin/uz", "/bin/su.shadow"]

class IMACheck(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        locations = ["/bin", "/usr/bin"]
        cls.binaries = []
        for l in locations:
            status, output = cls.tc.target.run("find %s -type f" % l)
            cls.binaries.extend(output.split("\n"))

        cls.total = len(cls.binaries)


    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_ima_enabled(self):
        ''' Test if IMA policy is loaded before systemd starts'''

        ima_search = "ima: "
        systemd_search = "systemd .* running"
        status, output = self.target.run("dmesg | grep -n '%s'" % ima_search)
        self.assertEqual( status, 0, "Did not find '%s' in dmesg" % ima_search)


    @skipIfNotFeature('systemd',
                      'Test requires systemd to be in DISTRO_FEATURES')
    @skipIfNotDataVar('VIRTUAL-RUNTIME_init_manager', 'systemd',
                      'systemd is not the init manager for this image')
    @OETestDepends(['ima.IMACheck.test_ima_enabled'])
    def test_ima_before_systemd(self):
        ''' Test if IMA policy is loaded before systemd starts'''
        ima_search = "ima: "
        systemd_search = "systemd .* running"
        status, output = self.target.run("dmesg | grep -n '%s'" % ima_search)
        self.assertEqual( status, 0, "Did not find '%s' in dmesg" % ima_search)
        ima_id = int(output.split(":")[0])
        status, output = self.target.run("dmesg | grep -n '%s'" % systemd_search)
        self.assertEqual(status, 0, "Did not find '%s' in dmesg" % systemd_search)
        init_id = int(output.split(":")[0])
        if ima_id > init_id:
            self.fail("IMA does not start before systemd")


    @OETestDepends(['ima.IMACheck.test_ima_enabled'])
    def test_ima_hash(self):
        ''' Test if IMA stores correct file hash '''
        filename = "/etc/filetest"
        ima_measure_file = "/sys/kernel/security/ima/ascii_runtime_measurements"
        status, output = self.target.run("echo test > %s" % filename)
        self.assertEqual(status, 0, "Cannot create file %s on target" % filename)

        # wait for the IMA system to update the entry
        maximum_tries = 30
        tries = 0
        status, output = self.target.run("sha1sum %s" %filename)
        sleep(2)
        current_hash = output.split()[0]
        ima_hash = ""

        while tries < maximum_tries:
            status, output = self.target.run("cat %s | grep %s" \
                % (ima_measure_file, filename))
            # get last entry, 4th field
            if status == 0:
                tokens = output.split("\n")[-1].split()[3]
                ima_hash = tokens.split(":")[1]
                if ima_hash == current_hash:
                    break

            tries += 1
            sleep(1)

        # clean target
        self.target.run("rm %s" % filename)
        if ima_hash != current_hash:
            self.fail("Hash stored by IMA does not match actual hash")


    @OETestDepends(['ima.IMACheck.test_ima_enabled'])
    def test_ima_signature(self):
        ''' Test if IMA stores correct signature for system binaries'''
        passed = 0
        failed = 0
        for b in self.binaries:
            if b in blacklist:
                continue
            status, output = self.target.run("evmctl ima_verify %s" % b)
            if status != 0:
                failed += 1
            else:
                passed += 1

        if failed == self.total:
             self.fail("Signature verifications failed (%s)" % self.total)

        #bb.warn("pass: %s, fail: %s, Total: %s" % (passed, failed, total))

    @OETestDepends(['ima.IMACheck.test_ima_enabled'])
    def test_ima_overwrite(self):
        ''' Test if IMA prevents overwriting signed files '''
        passed = 0
        failed = 0
        for b in self.binaries:
            if b in blacklist:
                continue
            self.target.run("echo 'foo' >> %s" % b )
            status, output = self.target.run("evmctl ima_verify %s" % b)

            if status != 0:
                failed += 1
            else:
                passed += 1

        if failed == self.total:
             self.fail("Overwritting verifications failed (%s)" % self.total)
