import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
        #check if DEFAULTTUNE is set and it's value is: x86-64-x32
        defaulttune = oeRuntimeTest.tc.d.getVar("DEFAULTTUNE", True)
        if "x86-64-x32" not in defaulttune:
            skipModule("DEFAULTTUNE is not set to x86-64-x32")

class X32libTest(oeRuntimeTest):

    @testcase(281)
    @skipUnlessPassed("test_ssh")
    def test_x32_file(self):
        status1 = self.target.run("readelf -h /bin/ls | grep Class | grep ELF32")[0]
        status2 = self.target.run("readelf -h /bin/ls | grep Machine | grep X86-64")[0]
        self.assertTrue(status1 == 0 and status2 == 0, msg="/bin/ls isn't an X86-64 ELF32 binary. readelf says: %s" % self.target.run("readelf -h /bin/ls")[1])
