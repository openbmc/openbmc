#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import fnmatch
import time

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.utils.path import findFile

class RpmBasicTest(OERuntimeTestCase):

    @OEHasPackage(['rpm'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_rpm_help(self):
        status, output = self.target.run('rpm --help')
        msg = 'status and output: %s and %s' % (status, output)
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['rpm.RpmBasicTest.test_rpm_help'])
    def test_rpm_query(self):
        status, output = self.target.run('ls /var/lib/rpm/')
        if status != 0:
            self.skipTest('No /var/lib/rpm on target')
        status, output = self.target.run('rpm -q rpm')
        msg = 'status and output: %s and %s' % (status, output)
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['rpm.RpmBasicTest.test_rpm_query'])
    def test_rpm_query_nonroot(self):

        def set_up_test_user(u):
            status, output = self.target.run('id -u %s' % u)
            if status:
                status, output = self.target.run('useradd %s' % u)
                msg = 'Failed to create new user: %s' % output
                self.assertTrue(status == 0, msg=msg)

        def exec_as_test_user(u):
            status, output = self.target.run('su -c id %s' % u)
            msg = 'Failed to execute as new user'
            self.assertTrue("({0})".format(u) in output, msg=msg)

            status, output = self.target.run('su -c "rpm -qa" %s ' % u)
            msg = 'status: %s. Cannot run rpm -qa: %s' % (status, output)
            self.assertEqual(status, 0, msg=msg)

        def wait_for_no_process_for_user(u, timeout = 120):
            timeout_at = time.time() + timeout
            while time.time() < timeout_at:
                _, output = self.target.run(self.tc.target_cmds['ps'])
                if u + ' ' not in output:
                    return
                time.sleep(1)
            user_pss = [ps for ps in output.split("\n") if u + ' ' in ps]
            msg = "User %s has processes still running: %s" % (u, "\n".join(user_pss))
            self.fail(msg=msg)

        def unset_up_test_user(u):
            # ensure no test1 process in running
            wait_for_no_process_for_user(u)
            status, output = self.target.run('userdel -r %s' % u)
            msg = 'Failed to erase user: %s' % output
            self.assertTrue(status == 0, msg=msg)

        tuser = 'test1'

        try:
            set_up_test_user(tuser)
            exec_as_test_user(tuser)
        finally:
            unset_up_test_user(tuser)


class RpmInstallRemoveTest(OERuntimeTestCase):

    def _find_test_file(self):
        pkgarch = self.td['TUNE_PKGARCH'].replace('-', '_')
        rpmdir = os.path.join(self.tc.td['DEPLOY_DIR'], 'rpm', pkgarch)
        # Pick base-passwd-doc as a test file to get installed, because it's small
        # and it will always be built for standard targets
        rpm_doc = 'base-passwd-doc-*.%s.rpm' % pkgarch
        if not os.path.exists(rpmdir):
            self.fail("Rpm directory {} does not exist".format(rpmdir))
        for f in fnmatch.filter(os.listdir(rpmdir), rpm_doc):
            self.test_file = os.path.join(rpmdir, f)
            break
        else:
            self.fail("Couldn't find the test rpm file {} in {}".format(rpm_doc, rpmdir))
        self.dst = '/tmp/base-passwd-doc.rpm'

    @OETestDepends(['rpm.RpmBasicTest.test_rpm_query'])
    def test_rpm_install(self):
        self._find_test_file()
        self.tc.target.copyTo(self.test_file, self.dst)
        status, output = self.target.run('rpm -ivh /tmp/base-passwd-doc.rpm')
        msg = 'Failed to install base-passwd-doc package: %s' % output
        self.assertEqual(status, 0, msg=msg)
        self.tc.target.run('rm -f %s' % self.dst)

    @OETestDepends(['rpm.RpmInstallRemoveTest.test_rpm_install'])
    def test_rpm_remove(self):
        status,output = self.target.run('rpm -e base-passwd-doc')
        msg = 'Failed to remove base-passwd-doc package: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['rpm.RpmInstallRemoveTest.test_rpm_remove'])
    def test_check_rpm_install_removal_log_file_size(self):
        """
        Summary:     Check that rpm writes into /var/log/messages
        Expected:    There should be some RPM prefixed entries in the above file.
        Product:     BSPs
        Author:      Alexandru Georgescu <alexandru.c.georgescu@intel.com>
        Author:      Alexander Kanavin <alex.kanavin@gmail.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """
        self._find_test_file()
        db_files_cmd = 'ls /var/lib/rpm/rpmdb.sqlite*'
        check_log_cmd = "grep RPM /var/log/messages | wc -l"

        # Make sure that some database files are under /var/lib/rpm as 'rpmdb.sqlite'
        status, output = self.target.run(db_files_cmd)
        msg =  'Failed to find database files under /var/lib/rpm/ as rpmdb.sqlite'
        self.assertEqual(0, status, msg=msg)

        self.tc.target.copyTo(self.test_file, self.dst)

        # Remove the package just in case
        self.target.run('rpm -e base-passwd-doc')

        # Install/Remove a package 10 times
        for i in range(10):
            status, output = self.target.run('rpm -ivh /tmp/base-passwd-doc.rpm')
            msg = 'Failed to install base-passwd-doc package. Reason: {}'.format(output)
            self.assertEqual(0, status, msg=msg)

            status, output = self.target.run('rpm -e base-passwd-doc')
            msg = 'Failed to remove base-passwd-doc package. Reason: {}'.format(output)
            self.assertEqual(0, status, msg=msg)

        self.tc.target.run('rm -f %s' % self.dst)


