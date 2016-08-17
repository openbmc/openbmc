import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("x11-base"):
            skipModule("target doesn't have x11 in IMAGE_FEATURES")


class XorgTest(oeRuntimeTest):

    @testcase(1151)
    @skipUnlessPassed('test_ssh')
    def test_xorg_running(self):
        (status, output) = self.target.run(oeRuntimeTest.pscmd + ' |  grep -v xinit | grep [X]org')
        self.assertEqual(status, 0, msg="Xorg does not appear to be running %s" % self.target.run(oeRuntimeTest.pscmd)[1])
