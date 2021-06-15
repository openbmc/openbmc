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
        self.project.run_configure('--disable-maintainer-mode',
                                   'sed -i -e "/char \*program_name/d" src/global.c;')
        self.project.run_make()
        self.project.run_install()
