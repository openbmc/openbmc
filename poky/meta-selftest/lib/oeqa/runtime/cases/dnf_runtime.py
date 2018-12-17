from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.cases.dnf import DnfTest
from oeqa.utils.httpserver import HTTPService
from oeqa.core.decorator.data import skipIfDataVar

class DnfSelftest(DnfTest):

    @classmethod
    def setUpClass(cls):
        import tempfile
        cls.temp_dir = tempfile.TemporaryDirectory(prefix="oeqa-remotefeeds-")
        cls.repo_server = HTTPService(os.path.join(cls.tc.td['WORKDIR'], 'oe-rootfs-repo'),
                                      cls.tc.target.server_ip)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()
        cls.temp_dir.cleanup()

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @skipIfDataVar('PACKAGE_FEED_URIS', None,
                   'Not suitable as PACKAGE_FEED_URIS is not set')
    def test_verify_package_feeds(self):
        """
        Summary: Check correct setting of PACKAGE_FEED_URIS var
        Expected: 1. Feeds were correctly set for dnf
                  2. Update recovers packages from host's repo
        Author: Humberto Ibarra <humberto.ibarra.lopez@intel.com>
        Author: Alexander Kanavin <alex.kanavin@gmail.com>
        """
        # When we created an image, we had to supply fake ip and port
        # for the feeds. Now we can patch the real ones into the config file.
        temp_file = os.path.join(self.temp_dir.name, 'tmp.repo')
        self.tc.target.copyFrom("/etc/yum.repos.d/oe-remote-repo.repo", temp_file)
        fixed_config = open(temp_file, "r").read().replace("bogus_ip", self.tc.target.server_ip).replace("bogus_port", str(self.repo_server.port))
        with open(temp_file, "w") as f:
            f.write(fixed_config)
        self.tc.target.copyTo(temp_file, "/etc/yum.repos.d/oe-remote-repo.repo")

        import re
        # Use '-y' for non-interactive mode: automatically import the feed signing key
        output_makecache = self.dnf('-vy makecache')
        self.assertTrue(re.match(r".*Failed to synchronize cache", output_makecache, re.DOTALL) is None, msg = "dnf makecache failed to synchronize repo: %s" %(output_makecache))
        self.assertTrue(re.match(r".*Metadata cache created", output_makecache, re.DOTALL) is not None, msg = "dnf makecache failed: %s" %(output_makecache))

        output_repoinfo = self.dnf('-v repoinfo')
        matchobj = re.match(r".*Repo-pkgs\s*:\s*(?P<n_pkgs>[0-9]+)", output_repoinfo, re.DOTALL)
        self.assertTrue(matchobj is not None, msg = "Could not find the amount of packages in dnf repoinfo output: %s" %(output_repoinfo))
        self.assertTrue(int(matchobj.group('n_pkgs')) > 0, msg = "Amount of remote packages is not more than zero: %s\n" %(output_repoinfo))
