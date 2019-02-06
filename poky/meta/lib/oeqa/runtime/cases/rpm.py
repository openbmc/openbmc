import os
import fnmatch

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.utils.path import findFile

class RpmBasicTest(OERuntimeTestCase):

    @OETestID(960)
    @OEHasPackage(['rpm'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_rpm_help(self):
        status, output = self.target.run('rpm --help')
        msg = 'status and output: %s and %s' % (status, output)
        self.assertEqual(status, 0, msg=msg)

    @OETestID(191)
    @OETestDepends(['rpm.RpmBasicTest.test_rpm_help'])
    def test_rpm_query(self):
        status, output = self.target.run('ls /var/lib/rpm/')
        if status != 0:
            self.skipTest('No /var/lib/rpm on target')
        status, output = self.target.run('rpm -q rpm')
        msg = 'status and output: %s and %s' % (status, output)
        self.assertEqual(status, 0, msg=msg)

class RpmInstallRemoveTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        pkgarch = cls.td['TUNE_PKGARCH'].replace('-', '_')
        rpmdir = os.path.join(cls.tc.td['DEPLOY_DIR'], 'rpm', pkgarch)
        # Pick base-passwd-doc as a test file to get installed, because it's small
        # and it will always be built for standard targets
        rpm_doc = 'base-passwd-doc-*.%s.rpm' % pkgarch
        if not os.path.exists(rpmdir):
            return
        for f in fnmatch.filter(os.listdir(rpmdir), rpm_doc):
            cls.test_file = os.path.join(rpmdir, f)
        cls.dst = '/tmp/base-passwd-doc.rpm'

    @OETestID(192)
    @OETestDepends(['rpm.RpmBasicTest.test_rpm_query'])
    def test_rpm_install(self):
        self.tc.target.copyTo(self.test_file, self.dst)
        status, output = self.target.run('rpm -ivh /tmp/base-passwd-doc.rpm')
        msg = 'Failed to install base-passwd-doc package: %s' % output
        self.assertEqual(status, 0, msg=msg)
        self.tc.target.run('rm -f %s' % self.dst)

    @OETestID(194)
    @OETestDepends(['rpm.RpmInstallRemoveTest.test_rpm_install'])
    def test_rpm_remove(self):
        status,output = self.target.run('rpm -e base-passwd-doc')
        msg = 'Failed to remove base-passwd-doc package: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestID(1096)
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

        def unset_up_test_user(u):
            status, output = self.target.run('userdel -r %s' % u)
            msg = 'Failed to erase user: %s' % output
            self.assertTrue(status == 0, msg=msg)

        tuser = 'test1'

        try:
            set_up_test_user(tuser)
            exec_as_test_user(tuser)
        finally:
            unset_up_test_user(tuser)

    @OETestID(195)
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
        db_files_cmd = 'ls /var/lib/rpm/__db.*'
        check_log_cmd = "grep RPM /var/log/messages | wc -l"

        # Make sure that some database files are under /var/lib/rpm as '__db.xxx'
        status, output = self.target.run(db_files_cmd)
        msg =  'Failed to find database files under /var/lib/rpm/ as __db.xxx'
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

        # if using systemd this should ensure all entries are flushed to /var
        status, output = self.target.run("journalctl --sync")
        # Get the amount of entries in the log file
        status, output = self.target.run(check_log_cmd)
        msg = 'Failed to get the final size of the log file.'
        self.assertEqual(0, status, msg=msg)

        # Check that there's enough of them
        self.assertGreaterEqual(int(output), 80,
                                   'Cound not find sufficient amount of rpm entries in /var/log/messages, found {} entries'.format(output))
