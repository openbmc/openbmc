import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("tools-sdk"):
        skipModule("Image doesn't have tools-sdk in IMAGE_FEATURES")

class LddTest(oeRuntimeTest):

    @testcase(962)
    @skipUnlessPassed('test_ssh')
    def test_ldd_exists(self):
        (status, output) = self.target.run('which ldd')
        self.assertEqual(status, 0, msg = "ldd does not exist in PATH: which ldd: %s" % output)

    @testcase(239)
    @skipUnlessPassed('test_ldd_exists')
    def test_ldd_rtldlist_check(self):
        (status, output) = self.target.run('for i in $(which ldd | xargs cat | grep "^RTLDLIST"|cut -d\'=\' -f2|tr -d \'"\'); do test -f $i && echo $i && break; done')
        self.assertEqual(status, 0, msg = "ldd path not correct or RTLDLIST files don't exist. ")
