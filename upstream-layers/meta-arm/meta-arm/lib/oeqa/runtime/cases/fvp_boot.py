# SPDX-License-Identifier: MIT

from oeqa.runtime.case import OERuntimeTestCase

class FVPBootTest(OERuntimeTestCase):
    """
    This test waits for a Linux login prompt on the default console. It is
    dependent on the OEFVPTarget test controller
    """

    def test_fvp_boot(self):
        import pexpect

        self.target.transition("off")
        timeout = int(self.td.get('TEST_FVP_LINUX_BOOT_TIMEOUT') or 10*60)
        self.target.transition("linux", timeout)

        # Check for common error patterns on all consoles
        for console in self.target.config['consoles']:
            # "expect" a timeout when searching for the error patterns
            match = self.target.expect(console,
                               [br'(\[ERR\]|\[ERROR\]|ERROR\:)',
                                pexpect.TIMEOUT],
                                timeout=0)
            self.assertEqual(match, 1)
