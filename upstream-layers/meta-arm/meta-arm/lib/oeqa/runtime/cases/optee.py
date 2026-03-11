#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.oetimeout import OETimeout

class OpteeTestSuite(OERuntimeTestCase):
    """
    Run OP-TEE tests (xtest).
    """
    @OETimeout(2700)
    @OEHasPackage(['optee-test'])
    def test_opteetest_xtest(self):
        # clear storage before executing tests
        cmd = "xtest --clear-storage || true"
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
        cmd = "xtest"
        status, output = self.target.run(cmd, timeout=1200)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
