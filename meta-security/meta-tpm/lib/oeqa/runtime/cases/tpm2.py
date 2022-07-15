# Copyright (C) 2019 - 2022 Armin Kuster <akuster808@gmail.com>
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.data import skipIfNotFeature

class Tpm2Test(OERuntimeTestCase):
    @classmethod
    def setUpClass(cls):
        cls.tc.target.run('swtpm_ioctl -s --tcp :2322')
        cls.tc.target.run('mkdir /tmp/myvtpm2')

    @classmethod
    def tearDownClass(cls):
        cls.tc.target.run('swtpm_ioctl -s --tcp :2322')
        cls.tc.target.run('rm -fr /tmp/myvtpm2')

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
    @skipIfNotFeature('tpm2','Test tpm2_startup requires tpm2 to be in DISTRO_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_tpm2_startup(self):
        cmds = [
                'swtpm socket -d --tpmstate dir=/tmp/myvtpm2 --tpm2 --ctrl type=tcp,port=2322 --server type=tcp,port=2321 --flags not-need-init',
                'tpm2_startup -c -T "swtpm:port=2321"',
               ]

        for cmd in cmds:
            status, output = self.target.run(cmd)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_startup'])
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
    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_pcrread'])
    def test_tpm2_pkcs11(self):
         (status, output) = self.target.run('p11-kit list-modules -v')
         self.assertEqual(status, 0, msg="Modules missing: %s" % output)

    @OETestDepends(['tpm2.Tpm2Test.test_tpm2_pkcs11'])
    def test_tpm2_swtpm_reset(self):
         (status, output) = self.target.run('swtpm_ioctl -i --tcp :2322')
         self.assertEqual(status, 0, msg="swtpm reset failed: %s" % output)
