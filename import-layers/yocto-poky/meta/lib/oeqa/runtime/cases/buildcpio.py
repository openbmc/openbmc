from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature

from oeqa.runtime.utils.targetbuildproject import TargetBuildProject

class BuildCpioTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        uri = 'https://ftp.gnu.org/gnu/cpio'
        uri = '%s/cpio-2.12.tar.bz2' % uri
        cls.project = TargetBuildProject(cls.tc.target,
                                         uri,
                                         dl_dir = cls.tc.td['DL_DIR'])
        cls.project.download_archive()

    @classmethod
    def tearDownClass(cls):
        cls.project.clean()

    @OETestID(205)
    @skipIfNotFeature('tools-sdk',
                      'Test requires tools-sdk to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_cpio(self):
        self.project.run_configure()
        self.project.run_make()
        self.project.run_install()
