#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class TrustedServicesTest(OERuntimeTestCase):

    def run_test_tool(self, cmd, expected_status=0 ):
        """ Run a test utility """

        status, output = self.target.run(cmd)
        self.assertEqual(status, expected_status, msg='\n'.join([cmd, output]))

    @OEHasPackage(['ts-demo'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_00_ts_demo(self):
        self.run_test_tool('ts-demo')

    @OEHasPackage(['ts-service-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_01_ts_service_test(self):
        self.run_test_tool('ts-service-test')

    @OEHasPackage(['ts-uefi-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_02_ts_uefi_test(self):
        self.run_test_tool('uefi-test')

    @OEHasPackage(['ts-psa-crypto-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_03_psa_crypto_api_test(self):
        # There are a few expected PSA Crypto tests failing
        self.run_test_tool('psa-crypto-api-test', expected_status=46)

    @OEHasPackage(['ts-psa-its-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_04_psa_its_api_test(self):
        self.run_test_tool('psa-its-api-test')

    @OEHasPackage(['ts-psa-ps-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_05_psa_ps_api_test(self):
        # There are a few expected PSA Storage tests failing
        self.run_test_tool('psa-ps-api-test', expected_status=46)

    @OEHasPackage(['ts-psa-iat-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_06_psa_iat_api_test(self):
        self.run_test_tool('psa-iat-api-test')
