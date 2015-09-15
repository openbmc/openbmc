from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.targetbuild import TargetBuildProject

def setUpModule():
    if not oeRuntimeTest.hasFeature("tools-sdk"):
        skipModule("Image doesn't have tools-sdk in IMAGE_FEATURES")

class BuildCvsTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        self.project = TargetBuildProject(oeRuntimeTest.tc.target, oeRuntimeTest.tc.d,
                        "http://ftp.gnu.org/non-gnu/cvs/source/feature/1.12.13/cvs-1.12.13.tar.bz2")
        self.project.download_archive()

    @testcase(205)
    @skipUnlessPassed("test_ssh")
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
