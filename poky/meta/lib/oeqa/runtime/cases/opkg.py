#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.utils.httpserver import HTTPService
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.data import skipIfNotDataVar, skipIfNotFeature, skipIfFeature
from oeqa.runtime.decorator.package import OEHasPackage

class OpkgTest(OERuntimeTestCase):

    def pkg(self, command, expected = 0):
        command = 'opkg %s' % command
        status, output = self.target.run(command, 1500)
        message = os.linesep.join([command, output])
        self.assertEqual(status, expected, message)
        return output

class OpkgRepoTest(OpkgTest):

    @classmethod
    def setUp(cls):
        allarchfeed = 'all'
        if cls.tc.td["MULTILIB_VARIANTS"]:
            allarchfeed = cls.tc.td["TUNE_PKGARCH"]
        service_repo = os.path.join(cls.tc.td['DEPLOY_DIR_IPK'], allarchfeed)
        cls.repo_server = HTTPService(service_repo,
                                      '0.0.0.0', port=cls.tc.target.server_port,
                                      logger=cls.tc.logger)
        cls.repo_server.start()

    @classmethod
    def tearDown(cls):
        cls.repo_server.stop()

    def setup_source_config_for_package_install(self):
        apt_get_source_server = 'http://%s:%s/' % (self.tc.target.server_ip, self.repo_server.port)
        apt_get_sourceslist_dir = '/etc/opkg/'
        self.target.run('cd %s; echo src/gz all %s >> opkg.conf' % (apt_get_sourceslist_dir, apt_get_source_server))
        
    def cleanup_source_config_for_package_install(self):
        apt_get_sourceslist_dir = '/etc/opkg/'
        self.target.run('cd %s; sed -i "/^src/d" opkg.conf' % (apt_get_sourceslist_dir))
        
    @skipIfNotFeature('package-management',
                      'Test requires package-management to be in IMAGE_FEATURES')
    @skipIfNotDataVar('IMAGE_PKGTYPE', 'ipk',
                      'IPK is not the primary package manager')
    @skipIfFeature('read-only-rootfs',
                   'Test does not work with read-only-rootfs in IMAGE_FEATURES')
    @OEHasPackage(['opkg'])
    def test_opkg_install_from_repo(self):
        self.setup_source_config_for_package_install()
        self.pkg('update')
        self.pkg('remove run-postinsts-dev')
        self.pkg('install run-postinsts-dev')
        self.cleanup_source_config_for_package_install()
