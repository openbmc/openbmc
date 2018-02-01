import unittest
from oeqa.sdk.case import OESDKTestCase
from oeqa.sdk.utils.sdkbuildproject import SDKBuildProject

class BuildCpioTest(OESDKTestCase):
    td_vars = ['DATETIME']

    @classmethod
    def setUpClass(self):
        dl_dir = self.td.get('DL_DIR', None)

        self.project = SDKBuildProject(self.tc.sdk_dir + "/cpio/", self.tc.sdk_env,
                        "https://ftp.gnu.org/gnu/cpio/cpio-2.12.tar.gz",
                        self.tc.sdk_dir, self.td['DATETIME'], dl_dir=dl_dir)
        self.project.download_archive()

        machine = self.td.get("MACHINE")
        if not self.tc.hasHostPackage("packagegroup-cross-canadian-%s" % machine):
            raise unittest.SkipTest("SDK doesn't contain a cross-canadian toolchain")

    def test_cpio(self):
        self.assertEqual(self.project.run_configure(), 0,
                        msg="Running configure failed")

        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")

        self.assertEqual(self.project.run_install(), 0,
                        msg="Running make install failed")

    @classmethod
    def tearDownClass(self):
        self.project.clean()
