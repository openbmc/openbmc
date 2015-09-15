from oeqa.oetest import oeSDKTest
from oeqa.utils.decorators import *
from oeqa.utils.targetbuild import SDKBuildProject


class BuildIptablesTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        self.project = SDKBuildProject(oeSDKTest.tc.sdktestdir + "/iptables/", oeSDKTest.tc.sdkenv, oeSDKTest.tc.d,
                        "http://netfilter.org/projects/iptables/files/iptables-1.4.13.tar.bz2")
        self.project.download_archive()

    def test_iptables(self):
        self.assertEqual(self.project.run_configure(), 0,
                        msg="Running configure failed")

        self.assertEqual(self.project.run_make(), 0,
                        msg="Running make failed")

        self.assertEqual(self.project.run_install(), 0,
                        msg="Running make install failed")

    @classmethod
    def tearDownClass(self):
        self.project.clean()
