from oeqa.selftest.case import OESelftestTestCase
from oeqa.sdk.utils.sdkbuildproject import SDKBuildProject
from oeqa.utils.commands import bitbake, get_bb_vars, runCmd
from oeqa.core.decorator.oeid import OETestID
import tempfile
import shutil

class MetaIDE(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(MetaIDE, cls).setUpClass()
        bitbake('meta-ide-support')
        bb_vars = get_bb_vars(['MULTIMACH_TARGET_SYS', 'TMPDIR', 'COREBASE'])
        cls.environment_script = 'environment-setup-%s' % bb_vars['MULTIMACH_TARGET_SYS']
        cls.tmpdir = bb_vars['TMPDIR']
        cls.environment_script_path = '%s/%s' % (cls.tmpdir, cls.environment_script)
        cls.corebasedir = bb_vars['COREBASE']
        cls.tmpdir_metaideQA = tempfile.mkdtemp(prefix='metaide')
        
    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmpdir_metaideQA, ignore_errors=True)
        super(MetaIDE, cls).tearDownClass()

    @OETestID(1982)
    def test_meta_ide_had_installed_meta_ide_support(self):
        self.assertExists(self.environment_script_path)

    @OETestID(1983)
    def test_meta_ide_can_compile_c_program(self):
        runCmd('cp %s/test.c %s' % (self.tc.files_dir, self.tmpdir_metaideQA))
        runCmd("cd %s; . %s; $CC test.c -lm" % (self.tmpdir_metaideQA, self.environment_script_path))
        compiled_file = '%s/a.out' % self.tmpdir_metaideQA
        self.assertExists(compiled_file)

    @OETestID(1984)
    def test_meta_ide_can_build_cpio_project(self):
        dl_dir = self.td.get('DL_DIR', None)
        self.project = SDKBuildProject(self.tmpdir_metaideQA + "/cpio/", self.environment_script_path,
                        "https://ftp.gnu.org/gnu/cpio/cpio-2.12.tar.gz",
                        self.tmpdir_metaideQA, self.td['DATETIME'], dl_dir=dl_dir)
        self.project.download_archive()
        self.assertEqual(self.project.run_configure(), 0,
                        msg="Running configure failed")
        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")
        self.assertEqual(self.project.run_install(), 0,
                        msg="Running make install failed")
