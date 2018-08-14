import os
import re
import subprocess
from oeqa.utils.httpserver import HTTPService

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotDataVar, skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class DnfTest(OERuntimeTestCase):

    def dnf(self, command, expected = 0):
        command = 'dnf %s' % command
        status, output = self.target.run(command, 1500)
        message = os.linesep.join([command, output])
        self.assertEqual(status, expected, message)
        return output

class DnfBasicTest(DnfTest):

    @skipIfNotFeature('package-management',
                      'Test requires package-management to be in IMAGE_FEATURES')
    @skipIfNotDataVar('IMAGE_PKGTYPE', 'rpm',
                      'RPM is not the primary package manager')
    @OEHasPackage(['dnf'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OETestID(1735)
    def test_dnf_help(self):
        self.dnf('--help')

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @OETestID(1739)
    def test_dnf_version(self):
        self.dnf('--version')

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @OETestID(1737)
    def test_dnf_info(self):
        self.dnf('info dnf')

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @OETestID(1738)
    def test_dnf_search(self):
        self.dnf('search dnf')

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @OETestID(1736)
    def test_dnf_history(self):
        self.dnf('history')

class DnfRepoTest(DnfTest):

    @classmethod
    def setUpClass(cls):
        cls.repo_server = HTTPService(os.path.join(cls.tc.td['WORKDIR'], 'oe-testimage-repo'),
                                      cls.tc.target.server_ip)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()

    def dnf_with_repo(self, command):
        pkgarchs = os.listdir(os.path.join(self.tc.td['WORKDIR'], 'oe-testimage-repo'))
        deploy_url = 'http://%s:%s/' %(self.target.server_ip, self.repo_server.port)
        cmdlinerepoopts = ["--repofrompath=oe-testimage-repo-%s,%s%s" %(arch, deploy_url, arch) for arch in pkgarchs]

        self.dnf(" ".join(cmdlinerepoopts) + " --nogpgcheck " + command)

    @OETestDepends(['dnf.DnfBasicTest.test_dnf_help'])
    @OETestID(1744)
    def test_dnf_makecache(self):
        self.dnf_with_repo('makecache')


# Does not work when repo is specified on the command line
#    @OETestDepends(['dnf.DnfRepoTest.test_dnf_makecache'])
#    def test_dnf_repolist(self):
#        self.dnf_with_repo('repolist')

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_makecache'])
    @OETestID(1746)
    def test_dnf_repoinfo(self):
        self.dnf_with_repo('repoinfo')

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_makecache'])
    @OETestID(1740)
    def test_dnf_install(self):
        self.dnf_with_repo('install -y run-postinsts-dev')

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_install'])
    @OETestID(1741)
    def test_dnf_install_dependency(self):
        self.dnf_with_repo('remove -y run-postinsts')
        self.dnf_with_repo('install -y run-postinsts-dev')

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_install_dependency'])
    @OETestID(1742)
    def test_dnf_install_from_disk(self):
        self.dnf_with_repo('remove -y run-postinsts-dev')
        self.dnf_with_repo('install -y --downloadonly run-postinsts-dev')
        status, output = self.target.run('find /var/cache/dnf -name run-postinsts-dev*rpm', 1500)
        self.assertEqual(status, 0, output)
        self.dnf_with_repo('install -y %s' % output)

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_install_from_disk'])
    @OETestID(1743)
    def test_dnf_install_from_http(self):
        output = subprocess.check_output('%s %s -name run-postinsts-dev*' % (bb.utils.which(os.getenv('PATH'), "find"),
                                                                           os.path.join(self.tc.td['WORKDIR'], 'oe-testimage-repo')), shell=True).decode("utf-8")
        rpm_path = output.split("/")[-2] + "/" + output.split("/")[-1]
        url = 'http://%s:%s/%s' %(self.target.server_ip, self.repo_server.port, rpm_path)
        self.dnf_with_repo('remove -y run-postinsts-dev')
        self.dnf_with_repo('install -y %s' % url)

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_install'])
    @OETestID(1745)
    def test_dnf_reinstall(self):
        self.dnf_with_repo('reinstall -y run-postinsts-dev')


