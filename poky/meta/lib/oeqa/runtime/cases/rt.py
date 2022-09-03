#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends

class RtTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_is_rt(self):
        """
        Check that the kernel has CONFIG_PREEMPT_RT enabled.
        """
        status, output = self.target.run("uname -a")
        self.assertEqual(status, 0, msg=output)
        # Split so we don't get a substring false-positive
        self.assertIn("PREEMPT_RT", output.split())
