from oeqa.oetest import oeSDKTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.targetbuild import SDKBuildProject

class BuildCvsTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        self.project = SDKBuildProject(oeSDKTest.tc.sdktestdir + "/cvs/", oeSDKTest.tc.sdkenv, oeSDKTest.tc.d,
                        "http://ftp.gnu.org/non-gnu/cvs/source/feature/1.12.13/cvs-1.12.13.tar.bz2")
        self.project.download_archive()

    def test_cvs(self):
        self.assertEqual(self.project.run_configure(), 0,
                        msg="Running configure failed")

        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")

        self.assertEqual(self.project.run_install(), 0,
                        msg="Running make install failed")

    @classmethod
    def tearDownClass(self):
        self.project.clean()
