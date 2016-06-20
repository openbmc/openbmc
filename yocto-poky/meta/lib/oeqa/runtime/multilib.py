import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    multilibs = oeRuntimeTest.tc.d.getVar("MULTILIBS", True) or ""
    if "multilib:lib32" not in multilibs:
        skipModule("this isn't a multilib:lib32 image")


class MultilibTest(oeRuntimeTest):

    def archtest(self, binary, arch):
        """
        Check that ``binary`` has the ELF class ``arch`` (e.g. ELF32/ELF64).
        """

        (status, output) = self.target.run("readelf -h %s" % binary)
        self.assertEqual(status, 0, "Failed to readelf %s" % binary)

        l = [l.split()[1] for l in output.split('\n') if "Class:" in l]
        if l:
            theclass = l[0]
        else:
            self.fail("Cannot parse readelf output\n" + s)

        self.assertEqual(theclass, arch, msg="%s isn't %s (is %s)" % (binary, arch, theclass))

    @skipUnlessPassed('test_ssh')
    def test_check_multilib_libc(self):
        """
        Check that a multilib image has both 32-bit and 64-bit libc in.
        """
        self.archtest("/lib/libc.so.6", "ELF32")
        self.archtest("/lib64/libc.so.6", "ELF64")

    @testcase('279')
    @skipUnlessPassed('test_check_multilib_libc')
    def test_file_connman(self):
        self.assertTrue(oeRuntimeTest.hasPackage('lib32-connman'), msg="This test assumes lib32-connman is installed")

        self.archtest("/usr/sbin/connmand", "ELF32")
