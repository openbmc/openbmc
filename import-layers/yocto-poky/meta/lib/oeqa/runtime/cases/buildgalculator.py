from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature

from oeqa.runtime.utils.targetbuildproject import TargetBuildProject

class GalculatorTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        uri = 'http://galculator.mnim.org/downloads/galculator-2.1.4.tar.bz2'
        cls.project = TargetBuildProject(cls.tc.target,
                                         uri,
                                         dl_dir = cls.tc.td['DL_DIR'])
        cls.project.download_archive()

    @classmethod
    def tearDownClass(cls):
        cls.project.clean()

    @OETestID(1526)
    @skipIfNotFeature('tools-sdk',
                      'Test requires tools-sdk to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_galculator(self):
        self.project.run_configure()
        self.project.run_make()
