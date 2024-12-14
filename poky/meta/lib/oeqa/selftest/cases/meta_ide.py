#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.sdk.utils.sdkbuildproject import SDKBuildProject
from oeqa.utils.commands import bitbake, get_bb_vars, runCmd
from oeqa.core.decorator import OETestTag
import tempfile
import shutil

@OETestTag("machine")
class MetaIDE(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(MetaIDE, cls).setUpClass()
        bitbake('meta-ide-support')
        bitbake('build-sysroots -c build_native_sysroot')
        bitbake('build-sysroots -c build_target_sysroot')
        bb_vars = get_bb_vars(['MACHINE_ARCH', 'TARGET_VENDOR', 'TARGET_OS', 'DEPLOY_DIR_IMAGE', 'COREBASE'])
        cls.environment_script = 'environment-setup-%s%s-%s' % (bb_vars['MACHINE_ARCH'], bb_vars['TARGET_VENDOR'], bb_vars['TARGET_OS'])
        cls.deploydir = bb_vars['DEPLOY_DIR_IMAGE']
        cls.environment_script_path = '%s/%s' % (cls.deploydir, cls.environment_script)
        cls.corebasedir = bb_vars['COREBASE']
        cls.tmpdir_metaideQA = tempfile.mkdtemp(prefix='metaide')

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmpdir_metaideQA, ignore_errors=True)
        super(MetaIDE, cls).tearDownClass()

    def test_meta_ide_had_installed_meta_ide_support(self):
        self.assertExists(self.environment_script_path)

    def test_meta_ide_can_compile_c_program(self):
        runCmd('cp %s/test.c %s' % (self.tc.files_dir, self.tmpdir_metaideQA))
        runCmd("cd %s; . %s; $CC test.c -lm" % (self.tmpdir_metaideQA, self.environment_script_path))
        compiled_file = '%s/a.out' % self.tmpdir_metaideQA
        self.assertExists(compiled_file)

    def test_meta_ide_can_build_cpio_project(self):
        dl_dir = self.td.get('DL_DIR', None)
        self.project = SDKBuildProject(self.tmpdir_metaideQA + "/cpio/", self.environment_script_path,
                        "https://ftp.gnu.org/gnu/cpio/cpio-2.15.tar.gz",
                        self.tmpdir_metaideQA, self.td['DATETIME'], dl_dir=dl_dir)
        self.project.download_archive()
        self.assertEqual(self.project.run_configure('$CONFIGURE_FLAGS'), 0,
                        msg="Running configure failed")
        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")
        self.assertEqual(self.project.run_install(), 0,
                        msg="Running make install failed")

    def test_meta_ide_can_run_sdk_tests(self):
        bitbake('-c populate_sysroot gtk+3')
        bitbake('build-sysroots -c build_target_sysroot')
        bitbake('-c testsdk meta-ide-support')
