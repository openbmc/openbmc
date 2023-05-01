#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

from oeqa.runtime.utils.targetbuildproject import TargetBuildProject

class BuildCpioTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        uri = 'https://downloads.yoctoproject.org/mirror/sources/cpio-2.13.tar.gz'
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
    def test_cpio(self):
        self.project.download_archive()
        self.project.run_configure('--disable-maintainer-mode')
        # This sed is needed until
        # https://git.savannah.gnu.org/cgit/cpio.git/commit/src/global.c?id=641d3f489cf6238bb916368d4ba0d9325a235afb
        # is in a release.
        self.project._run(r'sed -i -e "/char \*program_name/d" %s/src/global.c' % self.project.targetdir)
        self.project.run_make()
        self.project.run_install()
