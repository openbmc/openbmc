# Copyright (C) 2022 Armin Kuster <akuster808@gmail.com>
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.data import skipIfNotFeature

class SwTpmTest(OERuntimeTestCase):
    @classmethod
    def setUpClass(cls):
        cls.tc.target.run('swtpm_ioctl -s --tcp :2322')
        cls.tc.target.run('mkdir /tmp/myvtpm2')
        cls.tc.target.run('chown tss:root /tmp/myvtpm2')

    @classmethod
    def tearDownClass(cls):
        cls.tc.target.run('swtpm_ioctl -s --tcp :2322')
        cls.tc.target.run('rm -fr /tmp/myvtpm2')

    @skipIfNotFeature('tpm2','Test tpm2_swtpm_socket requires tpm2 to be in DISTRO_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['swtpm'])
    def test_swtpm2_ek_cert(self):
            cmd = 'swtpm_setup --tpmstate /tmp/myvtpm2 --create-ek-cert --create-platform-cert --tpm2',
            status, output = self.target.run(cmd)
            self.assertEqual(status, 0, msg="swtpm create-ek-cert failed: %s" % output)
