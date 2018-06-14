from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotInDataVar
from oeqa.runtime.decorator.package import OEHasPackage

class MultilibTest(OERuntimeTestCase):

    def archtest(self, binary, arch):
        """
        Check that ``binary`` has the ELF class ``arch`` (e.g. ELF32/ELF64).
        """

        status, output = self.target.run('readelf -h %s' % binary)
        self.assertEqual(status, 0, 'Failed to readelf %s' % binary)

        l = [l.split()[1] for l in output.split('\n') if "Class:" in l]
        if l:
            theclass = l[0]
        else:
            self.fail('Cannot parse readelf. Output:\n%s' % output)

        msg = "%s isn't %s (is %s)" % (binary, arch, theclass)
        self.assertEqual(theclass, arch, msg=msg)

    @OETestID(1593)
    @skipIfNotInDataVar('MULTILIBS', 'multilib:lib32',
                        "This isn't a multilib:lib32 image")
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['binutils'])
    @OEHasPackage(['lib32-libc6'])
    def test_check_multilib_libc(self):
        """
        Check that a multilib image has both 32-bit and 64-bit libc in.
        """
        self.archtest("/lib/libc.so.6", "ELF32")
        self.archtest("/lib64/libc.so.6", "ELF64")

    @OETestID(279)
    @OETestDepends(['multilib.MultilibTest.test_check_multilib_libc'])
    @OEHasPackage(['lib32-connman', '!connman'])
    def test_file_connman(self):
        self.archtest("/usr/sbin/connmand", "ELF32")
