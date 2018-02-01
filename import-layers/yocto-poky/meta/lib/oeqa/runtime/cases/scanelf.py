from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class ScanelfTest(OERuntimeTestCase):
    scancmd = 'scanelf --quiet --recursive --mount --ldpath --path'

    @OETestID(966)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['pax-utils'])
    def test_scanelf_textrel(self):
        # print TEXTREL information
        cmd = '%s --textrel' % self.scancmd
        status, output = self.target.run(cmd)
        msg = '\n'.join([cmd, output])
        self.assertEqual(output.strip(), '', msg=msg)

    @OETestID(967)
    @OETestDepends(['scanelf.ScanelfTest.test_scanelf_textrel'])
    def test_scanelf_rpath(self):
        # print RPATH information
        cmd = '%s --textrel --rpath' % self.scancmd
        status, output = self.target.run(cmd)
        msg = '\n'.join([cmd, output])
        self.assertEqual(output.strip(), '', msg=msg)
