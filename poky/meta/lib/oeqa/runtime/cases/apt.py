#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.utils.httpserver import HTTPService
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.data import skipIfNotDataVar, skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class AptTest(OERuntimeTestCase):

    def pkg(self, command, expected = 0):
        command = 'apt-get %s' % command
        status, output = self.target.run(command, 1500)
        message = os.linesep.join([command, output])
        self.assertEqual(status, expected, message)
        return output

class AptRepoTest(AptTest):

    @classmethod
    def setUpClass(cls):
        service_repo = os.path.join(cls.tc.td['DEPLOY_DIR_DEB'], '')
        cls.repo_server = HTTPService(service_repo,
                                      '0.0.0.0', port=cls.tc.target.server_port,
                                      logger=cls.tc.logger)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()

    def setup_source_config_for_package_install(self):
        apt_get_source_server = 'http://%s:%s/' % (self.tc.target.server_ip, self.repo_server.port)
        apt_get_sourceslist_dir = '/etc/apt/'
        self.target.run('cd %s; echo deb [ allow-insecure=yes ] %s/all ./ > sources.list' % (apt_get_sourceslist_dir, apt_get_source_server))

    def setup_source_config_for_package_install_signed(self):
        apt_get_source_server = 'http://%s:%s' % (self.tc.target.server_ip, self.repo_server.port)
        apt_get_sourceslist_dir = '/etc/apt/'
        self.target.run("cd %s; cp sources.list sources.list.bak; sed -i 's|\[trusted=yes\] http://bogus_ip:bogus_port|%s|g' sources.list" % (apt_get_sourceslist_dir, apt_get_source_server))

    def cleanup_source_config_for_package_install(self):
        apt_get_sourceslist_dir = '/etc/apt/'
        self.target.run('cd %s; rm sources.list' % (apt_get_sourceslist_dir))

    def cleanup_source_config_for_package_install_signed(self):
        apt_get_sourceslist_dir = '/etc/apt/'
        self.target.run('cd %s; mv sources.list.bak sources.list' % (apt_get_sourceslist_dir))

    def setup_key(self):
        # the key is found on the target /etc/pki/packagefeed-gpg/
        # named PACKAGEFEED-GPG-KEY-poky-branch
        self.target.run('cd %s; apt-key add P*' % ('/etc/pki/packagefeed-gpg'))

    @skipIfNotFeature('package-management',
                      'Test requires package-management to be in IMAGE_FEATURES')
    @skipIfNotDataVar('IMAGE_PKGTYPE', 'deb',
                      'DEB is not the primary package manager')
    @OEHasPackage(['apt'])
    def test_apt_install_from_repo(self):
        if not self.tc.td.get('PACKAGE_FEED_GPG_NAME'):
            self.setup_source_config_for_package_install()
            self.pkg('update')
            self.pkg('remove --yes run-postinsts-dev')
            self.pkg('install --yes --allow-unauthenticated run-postinsts-dev')
            self.cleanup_source_config_for_package_install()
        else:
            # when we are here a key has been set to sign the package feed and
            # public key and gnupg installed on the image by test_testimage_apt
            self.setup_source_config_for_package_install_signed()
            self.setup_key()
            self.pkg('update')
            self.pkg('install --yes run-postinsts-dev')
            self.pkg('remove --yes run-postinsts-dev')
            self.cleanup_source_config_for_package_install_signed()
