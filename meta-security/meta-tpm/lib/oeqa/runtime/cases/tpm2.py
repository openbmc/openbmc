# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class Tpm2Test(OERuntimeTestCase):
    def check_endlines(self, results,  expected_endlines): 
        for line in results.splitlines():
            for el in expected_endlines:
                if line == el:
                    expected_endlines.remove(el)
                    break

        if expected_endlines:
            self.fail('Missing expected line endings:\n  %s' % '\n  '.join(expected_endlines))

    @OEHasPackage(['tpm2-tss'])
    @OEHasPackage(['tpm2-abrmd'])
    @OEHasPackage(['tpm2-tools'])
    @OEHasPackage(['ibmswtpm2'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_tpm2_sim(self):
        cmds = [
                'tpm_server &',
                'tpm2-abrmd --allow-root --tcti=mssim &'
               ]

        for cmd in cmds:
            status, output = self.target.run(cmd)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_sim'])
    def test_tpm2(self):
         (status, output) = self.target.run('tpm2_pcrlist')
         expected_endlines = []
         expected_endlines.append('sha1 :')
         expected_endlines.append('  0  : 0000000000000000000000000000000000000003')
         expected_endlines.append('  1  : 0000000000000000000000000000000000000000')

         self.check_endlines(output, expected_endlines)

