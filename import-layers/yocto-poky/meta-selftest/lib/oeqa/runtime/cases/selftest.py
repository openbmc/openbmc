from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends

class Selftest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_install_package(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. Before the test socat must be installed using scp.
                  2. After the test socat must be uninstalled using ssh.
                     This can't be checked in this test.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertEqual(status, 0, msg="socat is not installed")

    @OETestDepends(['selftest.Selftest.test_install_package'])
    def test_verify_uninstall(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. test_install_package must uninstall socat.
                     This test is just to verify that.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertNotEqual(status, 0, msg="socat is still installed")
