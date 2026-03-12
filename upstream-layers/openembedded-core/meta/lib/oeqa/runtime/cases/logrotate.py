#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=289 testcase
# Note that the image under test must have logrotate installed

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class LogrotateTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['logrotate'])
    def test_logrotate_wtmp(self):
        # /var/log/wtmp may not always exist initially, so use touch to ensure it is present
        status, output = self.target.run('touch /var/log/wtmp')
        msg = ('Could not create/update /var/log/wtmp with touch')
        self.assertEqual(status, 0, msg = msg)

        # Create a folder to store rotated file and add the corresponding
        # configuration option
        self.addCleanup(self.target.run, 'rm -rf /var/log/logrotate_dir')
        status, output = self.target.run('mkdir /var/log/logrotate_dir')
        msg = ('Could not create logrotate_dir. Output: %s' % output)
        self.assertEqual(status, 0, msg = msg)

        status, output = self.target.run('echo "create \n olddir /var/log/logrotate_dir \n include /etc/logrotate.d/wtmp" > /tmp/logrotate-test.conf')
        msg = ('Could not write to /tmp/logrotate-test.conf')
        self.assertEqual(status, 0, msg = msg)

        # Call logrotate -f to force the rotation immediately
        # If logrotate fails to rotate the log, view the verbose output of logrotate to see what prevented it
        _, logrotate_output = self.target.run('logrotate -vf /tmp/logrotate-test.conf')
        status, _ = self.target.run('find /var/log/logrotate_dir -type f | grep wtmp.1')
        msg = ("logrotate did not successfully rotate the wtmp log. Output from logrotate -vf: \n%s" % (logrotate_output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['logrotate.LogrotateTest.test_logrotate_wtmp'])
    def test_logrotate_newlog(self):
        status, output = self.target.run('echo "oeqa logrotate test file" > /var/log/logrotate_testfile')
        msg = ('Could not create logrotate test file in /var/log')
        self.assertEqual(status, 0, msg = msg)

        # Create a new configuration file dedicated to a /var/log/logrotate_testfile
        self.addCleanup(self.target.run, 'rm -f /var/log/logrotate_testfile')
        status, output = self.target.run('echo "/var/log/logrotate_testfile {\n missingok \n monthly \n rotate 1}" > /etc/logrotate.d/logrotate_testfile')
        msg = ('Could not write to /etc/logrotate.d/logrotate_testfile')
        self.assertEqual(status, 0, msg = msg)

        self.addCleanup(self.target.run, 'rm -rf /var/log/logrotate_dir')
        status, output = self.target.run('mkdir /var/log/logrotate_dir')
        msg = ('Could not create logrotate_dir. Output: %s' % output)
        self.assertEqual(status, 0, msg = msg)

        self.addCleanup(self.target.run, 'rm -f /etc/logrotate.d/logrotate_testfile')
        status, output = self.target.run('echo "create \n olddir /var/log/logrotate_dir \n include /etc/logrotate.d/logrotate_testfile" > /tmp/logrotate-test2.conf')
        msg = ('Could not write to /tmp/logrotate_test2.conf')
        self.assertEqual(status, 0, msg = msg)

        status, output = self.target.run('find /var/log/logrotate_dir -type f | grep logrotate_testfile.1')
        msg = ('A rotated log for logrotate_testfile is already present in logrotate_dir')
        self.assertEqual(status, 1, msg = msg)

        # Call logrotate -f to force the rotation immediately
        # If logrotate fails to rotate the log, view the verbose output of logrotate instead of just listing the files in olddir
        _, logrotate_output = self.target.run('logrotate -vf /tmp/logrotate-test2.conf')
        status, _ = self.target.run('find /var/log/logrotate_dir -type f | grep logrotate_testfile.1')
        msg = ('logrotate did not successfully rotate the logrotate_test log. Output from logrotate -vf: \n%s' % (logrotate_output))
        self.assertEqual(status, 0, msg = msg)
