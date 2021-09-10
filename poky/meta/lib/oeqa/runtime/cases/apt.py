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
        service_repo = os.path.join(cls.tc.td['DEPLOY_DIR_DEB'], 'all')
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
        self.target.run('cd %s; echo deb [ allow-insecure=yes ] %s ./ > sources.list' % (apt_get_sourceslist_dir, apt_get_source_server))

    def cleanup_source_config_for_package_install(self):
        apt_get_sourceslist_dir = '/etc/apt/'
        self.target.run('cd %s; rm sources.list' % (apt_get_sourceslist_dir))

    @skipIfNotFeature('package-management',
                      'Test requires package-management to be in IMAGE_FEATURES')
    @skipIfNotDataVar('IMAGE_PKGTYPE', 'deb',
                      'DEB is not the primary package manager')
    @OEHasPackage(['apt'])
    def test_apt_install_from_repo(self):
        self.setup_source_config_for_package_install()
        self.pkg('update')
        self.pkg('remove --yes run-postinsts-dev')
        self.pkg('install --yes --allow-unauthenticated run-postinsts-dev')
        self.cleanup_source_config_for_package_install()
