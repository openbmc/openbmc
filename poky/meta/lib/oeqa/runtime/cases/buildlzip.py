#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

from oeqa.runtime.utils.targetbuildproject import TargetBuildProject

class BuildLzipTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        uri = 'http://downloads.yoctoproject.org/mirror/sources'
        uri = '%s/lzip-1.19.tar.gz' % uri
        cls.project = TargetBuildProject(cls.tc.target,
                                         uri,
                                         dl_dir = cls.tc.td['DL_DIR'])

    @classmethod
    def tearDownClass(cls):
        cls.project.clean()

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['gcc'])
    @OEHasPackage(['make'])
    @OEHasPackage(['autoconf'])
    def test_lzip(self):
        self.project.download_archive()
        self.project.run_configure()
        self.project.run_make()
        self.project.run_install()

