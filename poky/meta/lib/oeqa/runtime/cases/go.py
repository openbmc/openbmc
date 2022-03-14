#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class GoHelloworldTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['go-helloworld'])
    def test_gohelloworld(self):
        cmd = "go-helloworld"
        status, output = self.target.run(cmd)
        msg = 'Exit status was not 0. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        msg = 'Incorrect output: %s' % output
        self.assertEqual(output, "Hello, Go examples!", msg=msg)
