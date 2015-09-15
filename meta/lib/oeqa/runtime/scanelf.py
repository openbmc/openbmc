import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("pax-utils"):
        skipModule("pax-utils package not installed")

class ScanelfTest(oeRuntimeTest):

    def setUp(self):
        self.scancmd = 'scanelf --quiet --recursive --mount --ldpath --path'

    @testcase(966)
    @skipUnlessPassed('test_ssh')
    def test_scanelf_textrel(self):
        # print TEXTREL information
        self.scancmd += " --textrel"
        (status, output) = self.target.run(self.scancmd)
        self.assertEqual(output.strip(), "", "\n".join([self.scancmd, output]))

    @testcase(967)
    @skipUnlessPassed('test_ssh')
    def test_scanelf_rpath(self):
        # print RPATH information
        self.scancmd += " --rpath"
        (status, output) = self.target.run(self.scancmd)
        self.assertEqual(output.strip(), "", "\n".join([self.scancmd, output]))
