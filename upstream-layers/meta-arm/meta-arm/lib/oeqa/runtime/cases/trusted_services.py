#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.data import skipIfNotInDataVar

class TrustedServicesTest(OERuntimeTestCase):

    def run_test_tool(self, cmd, expected_status=0, expected_output=None ):
        """ Run a test utility """

        status, output = self.target.run(cmd)
        self.assertEqual(status, expected_status, msg='\n'.join([cmd, output]))
        if expected_output is not None:
            self.assertEqual(output, expected_output, msg='\n'.join([cmd, output]))

    @OEHasPackage(['ts-demo'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_00_ts_demo(self):
        self.run_test_tool('ts-demo')

    @OEHasPackage(['ts-uefi-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_02_ts_uefi_test(self):
        self.run_test_tool('uefi-test')

    @OEHasPackage(['ts-psa-crypto-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_03_psa_crypto_api_test(self):
        self.run_test_tool('psa-crypto-api-test')

    @OEHasPackage(['ts-psa-its-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_04_psa_its_api_test(self):
        self.run_test_tool('psa-its-api-test')

    @OEHasPackage(['ts-psa-ps-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_05_psa_ps_api_test(self):
        self.run_test_tool('psa-ps-api-test')

    @OEHasPackage(['ts-psa-iat-api-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_06_psa_iat_api_test(self):
        self.run_test_tool('psa-iat-api-test')

    @OEHasPackage(['ts-service-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_09_ts_service_grp_check(self):
        # If this test fails, available test groups in ts-service-test have changed and all
        # tests using the test executable need to be double checked to ensure test group to
        # TS SP mapping is still valid.
        test_grp_list="FwuServiceTests PsServiceTests ItsServiceTests AttestationProvisioningTests"
        test_grp_list+=" AttestationServiceTests CryptoKeyDerivationServicePackedcTests"
        test_grp_list+=" CryptoMacServicePackedcTests CryptoCipherServicePackedcTests"
        test_grp_list+=" CryptoHashServicePackedcTests CryptoServicePackedcTests"
        test_grp_list+=" CryptoServiceProtobufTests CryptoServiceLimitTests"
        self.run_test_tool('ts-service-test -lg', expected_output=test_grp_list)

    @OEHasPackage(['optee-test'])
    @skipIfNotInDataVar('MACHINE_FEATURES', 'optee-spmc-test', 'SPMC Test SPs are not included')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_07_spmc_test(self):
        self.run_test_tool('xtest -t ffa_spmc')

    @OEHasPackage(['ts-service-test'])
    @skipIfNotInDataVar('MACHINE_FEATURES', 'ts-fwu', 'FWU SP is not included')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_10_fwu_service_tests(self):
        self.run_test_tool('ts-service-test -g FwuServiceTests')

    @OEHasPackage(['ts-service-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_11_ps_service_tests(self):
        if 'ts-storage' not in self.tc.td['MACHINE_FEATURES'] and \
           'ts-se-proxy' not in self.tc.td['MACHINE_FEATURES']:
            self.skipTest('Storage SP is not deployed in the system.')
        self.run_test_tool('ts-service-test -g PsServiceTests')

    @OEHasPackage(['ts-service-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_12_its_service_tests(self):
        if 'ts-its' not in self.tc.td['MACHINE_FEATURES'] and \
           'ts-se-proxy' not in self.tc.td['MACHINE_FEATURES']:
            self.skipTest('Internal Storage SP is not deployed in the system.')
        self.run_test_tool('ts-service-test -g ItsServiceTests')

    @OEHasPackage(['ts-service-test'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_14_attestation_service_tests(self):
        if 'ts-attestation' not in self.tc.td['MACHINE_FEATURES'] and \
           'ts-se-proxy' not in self.tc.td['MACHINE_FEATURES']:
            self.skipTest('Attestation SP is not deployed in the system.')
        self.run_test_tool('ts-service-test -g Attestation')

    @OEHasPackage(['ts-service-test'])
    @skipIfNotInDataVar('MACHINE_FEATURES', 'ts-crypto', 'Crypto SP is not included')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_15_crypto_service_tests(self):
        if 'ts-crypto' not in self.tc.td['MACHINE_FEATURES'] and \
           'ts-se-proxy' not in self.tc.td['MACHINE_FEATURES']:
            self.skipTest('Crypto SP is not deployed in the system.')
        self.run_test_tool('ts-service-test -g Crypto')
