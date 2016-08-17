# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=287 testcase
# Note that the image under test must have "pam" in DISTRO_FEATURES

import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("pam"):
        skipModule("target doesn't have 'pam' in DISTRO_FEATURES")


class PamBasicTest(oeRuntimeTest):

    @testcase(287)
    @skipUnlessPassed('test_ssh')
    def test_pam(self):
        (status, output) = self.target.run('login --help')
        self.assertEqual(status, 1, msg = "login command does not work as expected. Status and output:%s and %s" %(status, output))
        (status, output) = self.target.run('passwd --help')
        self.assertEqual(status, 0, msg = "passwd command does not work as expected. Status and output:%s and %s" %(status, output))
        (status, output) = self.target.run('su --help')
        self.assertEqual(status, 0, msg = "su command does not work as expected. Status and output:%s and %s" %(status, output))
        (status, output) = self.target.run('useradd --help')
        self.assertEqual(status, 0, msg = "useradd command does not work as expected. Status and output:%s and %s" %(status, output))
