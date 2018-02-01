from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.cases.dnf import DnfTest
from oeqa.utils.httpserver import HTTPService

class Selftest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_install_package(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. Before the test socat must be installed using scp.
                  2. After the test socat must be uninstalled using ssh.
                     This can't be checked in this test.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertEqual(status, 0, msg="socat is not installed")

    @OETestDepends(['selftest.Selftest.test_install_package'])
    def test_verify_uninstall(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. test_install_package must uninstall socat.
                     This test is just to verify that.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertNotEqual(status, 0, msg="socat is still installed")


class DnfSelftest(DnfTest):

    @classmethod
    def setUpClass(cls):
        cls.repo_server = HTTPService(os.path.join(cls.tc.td['WORKDIR'], 'oe-rootfs-repo'),
                                      cls.tc.target.server_ip)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_verify_package_feeds(self):
        """
        Summary: Check correct setting of PACKAGE_FEED_URIS var
        Expected: 1. Feeds were correctly set for dnf
                  2. Update recovers packages from host's repo
        Author: Humberto Ibarra <humberto.ibarra.lopez@intel.com>
        Author: Alexander Kanavin <alexander.kanavin@intel.com>
        """
        # When we created an image, we had to supply fake ip and port
        # for the feeds. Now we can patch the real ones into the config file.
        import tempfile
        temp_file = tempfile.TemporaryDirectory(prefix="oeqa-remotefeeds-").name
        self.tc.target.copyFrom("/etc/yum.repos.d/oe-remote-repo.repo", temp_file)
        fixed_config = open(temp_file, "r").read().replace("bogus_ip", self.tc.target.server_ip).replace("bogus_port", str(self.repo_server.port))
        open(temp_file, "w").write(fixed_config)
        self.tc.target.copyTo(temp_file, "/etc/yum.repos.d/oe-remote-repo.repo")

        import re
        output_makecache = self.dnf('makecache')
        self.assertTrue(re.match(r".*Metadata cache created", output_makecache, re.DOTALL) is not None, msg = "dnf makecache failed: %s" %(output_makecache))

        output_repoinfo = self.dnf('repoinfo')
        matchobj = re.match(r".*Repo-pkgs\s*:\s*(?P<n_pkgs>[0-9]+)", output_repoinfo, re.DOTALL)
        self.assertTrue(matchobj is not None, msg = "Could not find the amount of packages in dnf repoinfo output: %s" %(output_repoinfo))
        self.assertTrue(int(matchobj.group('n_pkgs')) > 0, msg = "Amount of remote packages is not more than zero: %s\n" %(output_repoinfo))
