import unittest
import os
import fnmatch
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("package-management"):
            skipModule("rpm module skipped: target doesn't have package-management in IMAGE_FEATURES")
    if "package_rpm" != oeRuntimeTest.tc.d.getVar("PACKAGE_CLASSES", True).split()[0]:
            skipModule("rpm module skipped: target doesn't have rpm as primary package manager")


class RpmBasicTest(oeRuntimeTest):

    @testcase(960)
    @skipUnlessPassed('test_ssh')
    def test_rpm_help(self):
        (status, output) = self.target.run('rpm --help')
        self.assertEqual(status, 0, msg="status and output: %s and %s" % (status,output))

    @testcase(191)
    @skipUnlessPassed('test_rpm_help')
    def test_rpm_query(self):
        (status, output) = self.target.run('rpm -q rpm')
        self.assertEqual(status, 0, msg="status and output: %s and %s" % (status,output))

class RpmInstallRemoveTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        pkgarch = oeRuntimeTest.tc.d.getVar('TUNE_PKGARCH', True).replace("-", "_")
        rpmdir = os.path.join(oeRuntimeTest.tc.d.getVar('DEPLOY_DIR', True), "rpm", pkgarch)
        # pick rpm-doc as a test file to get installed, because it's small and it will always be built for standard targets
        for f in fnmatch.filter(os.listdir(rpmdir), "rpm-doc-*.%s.rpm" % pkgarch):
            testrpmfile = f
        oeRuntimeTest.tc.target.copy_to(os.path.join(rpmdir,testrpmfile), "/tmp/rpm-doc.rpm")

    @testcase(192)
    @skipUnlessPassed('test_rpm_help')
    def test_rpm_install(self):
        (status, output) = self.target.run('rpm -ivh /tmp/rpm-doc.rpm')
        self.assertEqual(status, 0, msg="Failed to install rpm-doc package: %s" % output)

    @testcase(194)
    @skipUnlessPassed('test_rpm_install')
    def test_rpm_remove(self):
        (status,output) = self.target.run('rpm -e rpm-doc')
        self.assertEqual(status, 0, msg="Failed to remove rpm-doc package: %s" % output)

    @testcase(1096)
    @skipUnlessPassed('test_ssh')
    def test_rpm_query_nonroot(self):
        (status, output) = self.target.run('useradd test1')
        self.assertTrue(status == 0, msg="Failed to create new user: " + output)
        (status, output) = self.target.run('sudo -u test1 id')
        self.assertTrue('(test1)' in output, msg="Failed to execute as new user")
        (status, output) = self.target.run('sudo -u test1 rpm -qa')
        self.assertEqual(status, 0, msg="status: %s. Cannot run rpm -qa: %s" % (status, output))

    @testcase(195)
    @skipUnlessPassed('test_rpm_install')
    def test_check_rpm_install_removal_log_file_size(self):
        """
        Summary:     Check rpm install/removal log file size
        Expected:    There should be some method to keep rpm log in a small size .
        Product:     BSPs
        Author:      Alexandru Georgescu <alexandru.c.georgescu@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """
        db_files_cmd = 'ls /var/lib/rpm/__db.*'
        get_log_size_cmd = "du /var/lib/rpm/log/log.* | awk '{print $1}'"

        # Make sure that some database files are under /var/lib/rpm as '__db.xxx'
        (status, output) = self.target.run(db_files_cmd)
        self.assertEqual(0, status, 'Failed to find database files under /var/lib/rpm/ as __db.xxx')

        # Remove the package just in case
        self.target.run('rpm -e rpm-doc')

        # Install/Remove a package 10 times
        for i in range(10):
            (status, output) = self.target.run('rpm -ivh /tmp/rpm-doc.rpm')
            self.assertEqual(0, status, "Failed to install rpm-doc package. Reason: {}".format(output))

            (status, output) = self.target.run('rpm -e rpm-doc')
            self.assertEqual(0, status, "Failed to remove rpm-doc package. Reason: {}".format(output))

        # Get the size of log file
        (status, output) = self.target.run(get_log_size_cmd)
        self.assertEqual(0, status, 'Failed to get the final size of the log file.')

        # Compare each log size
        for log_file_size in output:
            self.assertLessEqual(int(log_file_size), 11264,
                                   'Log file size is greater that expected (~10MB), found {} bytes'.format(log_file_size))

    @classmethod
    def tearDownClass(self):
        oeRuntimeTest.tc.target.run('rm -f /tmp/rpm-doc.rpm')

