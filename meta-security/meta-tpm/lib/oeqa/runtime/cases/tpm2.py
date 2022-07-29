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

    @OEHasPackage(['tpm2-tools'])
    @OEHasPackage(['tpm2-abrmd'])
    @OEHasPackage(['swtpm'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_tpm2_swtpm_socket(self):
        cmds = [
                'mkdir /tmp/myvtpm',
                'swtpm socket --tpmstate dir=/tmp/myvtpm --tpm2 --ctrl type=tcp,port=2322 --server type=tcp,port=2321 --flags not-need-init &',
                'export TPM2TOOLS_TCTI="swtpm:port=2321"',
                'tpm2_startup -c'
               ]

        for cmd in cmds:
            status, output = self.target.run(cmd)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_swtpm_socket'])
    def test_tpm2_pcrread(self):
         (status, output) = self.target.run('tpm2_pcrread')
         expected_endlines = []
         expected_endlines.append('  sha1:')
         expected_endlines.append('    0 : 0x0000000000000000000000000000000000000000')
         expected_endlines.append('    1 : 0x0000000000000000000000000000000000000000')
         expected_endlines.append('  sha256:')
         expected_endlines.append('    0 : 0x0000000000000000000000000000000000000000000000000000000000000000')
         expected_endlines.append('    1 : 0x0000000000000000000000000000000000000000000000000000000000000000')


         self.check_endlines(output, expected_endlines)


    @OEHasPackage(['p11-kit'])
    @OEHasPackage(['tpm2-pkcs11'])
    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_swtpm_socket'])
    def test_tpm2_pkcs11(self):
         (status, output) = self.target.run('p11-kit list-modules -v')
         self.assertEqual(status, 0, msg="Modules missing: %s" % output)

    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_pkcs11'])
    def test_tpm2_swtpm_reset(self):
         (status, output) = self.target.run('swtpm_ioctl -i --tcp :2322')
         self.assertEqual(status, 0, msg="swtpm reset failed: %s" % output)
