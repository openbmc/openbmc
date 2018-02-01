from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature

from oeqa.runtime.utils.targetbuildproject import TargetBuildProject

class BuildLzipTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        uri = 'http://downloads.yoctoproject.org/mirror/sources'
        uri = '%s/lzip-1.19.tar.gz' % uri
        cls.project = TargetBuildProject(cls.tc.target,
                                         uri,
                                         dl_dir = cls.tc.td['DL_DIR'])
        cls.project.download_archive()

    @classmethod
    def tearDownClass(cls):
        cls.project.clean()

    @OETestID(206)
    @skipIfNotFeature('tools-sdk',
                      'Test requires tools-sdk to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_lzip(self):
        self.project.run_configure()
        self.project.run_make()
        self.project.run_install()

    @classmethod
    def tearDownClass(self):
        self.project.clean()
