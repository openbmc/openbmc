from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.targetbuild import TargetBuildProject

def setUpModule():
    if not oeRuntimeTest.hasFeature("tools-sdk"):
        skipModule("Image doesn't have tools-sdk in IMAGE_FEATURES")

class GalculatorTest(oeRuntimeTest):
    @skipUnlessPassed("test_ssh")
    def test_galculator(self):
        try:
            project = TargetBuildProject(oeRuntimeTest.tc.target, oeRuntimeTest.tc.d,
                                      "http://galculator.mnim.org/downloads/galculator-2.1.4.tar.bz2")
            project.download_archive()

            self.assertEqual(project.run_configure(), 0,
                            msg="Running configure failed")

            self.assertEqual(project.run_make(), 0,
                            msg="Running make failed")
        finally:
            project.clean()
