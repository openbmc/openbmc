#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.runtime.decorator.package import OEHasPackage
from oeqa.core.decorator.oetimeout import OETimeout
from oeqa.core.decorator.data import skipIfQemu
from oeqa.core.decorator.data import skipIfNotQemu

class OpteeTestSuite(OERuntimeTestCase):
    """
    Run OP-TEE tests (xtest).
    """
    @skipIfQemu()
    @OETimeout(2700)
    @OEHasPackage(['optee-test'])
    def test_opteetest_xtest_all(self):
        # clear storage before executing tests
        cmd = "xtest --clear-storage || true"
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
        cmd = "xtest"
        status, output = self.target.run(cmd, timeout=1200)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

    @skipIfNotQemu()
    @OETimeout(2700)
    @OEHasPackage(['optee-test'])
    def test_opteetest_xtest_regression(self):
        # clear storage before executing tests
        cmd = "xtest --clear-storage || true"
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
        cmd = "xtest -t regression"
        status, output = self.target.run(cmd, timeout=1200)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
