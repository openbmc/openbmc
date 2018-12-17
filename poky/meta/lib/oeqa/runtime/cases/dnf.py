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
                                      cls.tc.target.server_ip, logger=cls.tc.logger)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()

    def dnf_with_repo(self, command):
        pkgarchs = os.listdir(os.path.join(self.tc.td['WORKDIR'], 'oe-testimage-repo'))
        deploy_url = 'http://%s:%s/' %(self.target.server_ip, self.repo_server.port)
        cmdlinerepoopts = ["--repofrompath=oe-testimage-repo-%s,%s%s" %(arch, deploy_url, arch) for arch in pkgarchs]

        output = self.dnf(" ".join(cmdlinerepoopts) + " --nogpgcheck " + command)
        return output

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
        output = self.dnf_with_repo('list run-postinsts-dev')
        if 'Installed Packages' in output:
            self.dnf_with_repo('remove -y run-postinsts-dev')
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

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_makecache'])
    @OETestID(1771)
    def test_dnf_installroot(self):
        rootpath = '/home/root/chroot/test'
        #Copy necessary files to avoid errors with not yet installed tools on
        #installroot directory.
        self.target.run('mkdir -p %s/etc' % rootpath, 1500)
        self.target.run('mkdir -p %s/bin %s/sbin %s/usr/bin %s/usr/sbin' % (rootpath, rootpath, rootpath, rootpath), 1500)
        self.target.run('mkdir -p %s/dev' % rootpath, 1500)
        #Handle different architectures lib dirs
        self.target.run('mkdir -p %s/lib' % rootpath, 1500)
        self.target.run('mkdir -p %s/libx32' % rootpath, 1500)
        self.target.run('mkdir -p %s/lib64' % rootpath, 1500)
        self.target.run('cp /lib/libtinfo.so.5 %s/lib' % rootpath, 1500)
        self.target.run('cp /libx32/libtinfo.so.5 %s/libx32' % rootpath, 1500)
        self.target.run('cp /lib64/libtinfo.so.5 %s/lib64' % rootpath, 1500)
        self.target.run('cp -r /etc/rpm %s/etc' % rootpath, 1500)
        self.target.run('cp -r /etc/dnf %s/etc' % rootpath, 1500)
        self.target.run('cp /bin/sh %s/bin' % rootpath, 1500)
        self.target.run('mount -o bind /dev %s/dev/' % rootpath, 1500)
        self.dnf_with_repo('install --installroot=%s -v -y --rpmverbosity=debug busybox run-postinsts' % rootpath)
        status, output = self.target.run('test -e %s/var/cache/dnf' % rootpath, 1500)
        self.assertEqual(0, status, output)
        status, output = self.target.run('test -e %s/bin/busybox' % rootpath, 1500)
        self.assertEqual(0, status, output)

    @OETestDepends(['dnf.DnfRepoTest.test_dnf_makecache'])
    @OETestID(1772)
    def test_dnf_exclude(self):
        excludepkg = 'curl-dev'
        self.dnf_with_repo('install -y curl*')
        self.dnf('list %s' % excludepkg, 0)
        #Avoid remove dependencies to skip some errors on different archs and images
        self.dnf_with_repo('remove --setopt=clean_requirements_on_remove=0 -y curl*')
        #check curl-dev is not installed adter removing all curl occurrences
        status, output = self.target.run('dnf list --installed | grep %s'% excludepkg, 1500)
        self.assertEqual(1, status, "%s was not removed,  is listed as installed"%excludepkg)
        self.dnf_with_repo('install -y --exclude=%s --exclude=curl-staticdev curl*' % excludepkg)
        #check curl-dev is not installed after being excluded
        status, output = self.target.run('dnf list --installed | grep %s'% excludepkg , 1500)
        self.assertEqual(1, status, "%s was not excluded, is listed as installed"%excludepkg)
