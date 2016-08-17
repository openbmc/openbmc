from oeqa.oetest import oeSDKTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.targetbuild import SDKBuildProject

def setUpModule():
    if not oeSDKTest.hasPackage("gtk\+"):
        skipModule("Image doesn't have gtk+ in manifest")

class SudokuTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        self.project = SDKBuildProject(oeSDKTest.tc.sdktestdir + "/sudoku/", oeSDKTest.tc.sdkenv, oeSDKTest.tc.d,
                        "http://downloads.sourceforge.net/project/sudoku-savant/sudoku-savant/sudoku-savant-1.3/sudoku-savant-1.3.tar.bz2")
        self.project.download_archive()

    def test_sudoku(self):
        self.assertEqual(self.project.run_configure(), 0,
                        msg="Running configure failed")

        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")

    @classmethod
    def tearDownClass(self):
        self.project.clean()
