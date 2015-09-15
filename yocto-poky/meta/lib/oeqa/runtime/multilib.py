import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    multilibs = oeRuntimeTest.tc.d.getVar("MULTILIBS", True) or ""
    if "multilib:lib32" not in multilibs:
        skipModule("this isn't a multilib:lib32 image")


class MultilibTest(oeRuntimeTest):

    def parse(self, s):
        """
        Parse the output of readelf -h and return the binary class, or fail.
        """
        l = [l.split()[1] for l in s.split('\n') if "Class:" in l]
        if l:
            return l[0]
        else:
            self.fail("Cannot parse readelf output\n" + s)

    @skipUnlessPassed('test_ssh')
    def test_check_multilib_libc(self):
        """
        Check that a multilib image has both 32-bit and 64-bit libc in.
        """

        (status, output) = self.target.run("readelf -h /lib/libc.so.6")
        self.assertEqual(status, 0, "Failed to readelf /lib/libc.so.6")
        class32 = self.parse(output)

        (status, output) = self.target.run("readelf -h /lib64/libc.so.6")
        self.assertEqual(status, 0, "Failed to readelf /lib64/libc.so.6")
        class64 = self.parse(output)

        self.assertEqual(class32, "ELF32", msg="/lib/libc.so.6 isn't ELF32 (is %s)" % class32)
        self.assertEqual(class64, "ELF64", msg="/lib64/libc.so.6 isn't ELF64 (is %s)" % class64)

    @testcase('279')
    @skipUnlessPassed('test_check_multilib_libc')
    def test_file_connman(self):
        self.assertTrue(oeRuntimeTest.hasPackage('lib32-connman-gnome'), msg="This test assumes lib32-connman-gnome is installed")

        (status, output) = self.target.run("readelf -h /usr/bin/connman-applet")
        self.assertEqual(status, 0, "Failed to readelf /usr/bin/connman-applet")
        theclass = self.parse(output)
        self.assertEqual(theclass, "ELF32", msg="connman-applet isn't ELF32 (is %s)" % theclass)
