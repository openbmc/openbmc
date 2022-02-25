#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class SSHTest(OERuntimeTestCase):

    @OETestDepends(['ping.PingTest.test_ping'])
    @OEHasPackage(['dropbear', 'openssh-sshd'])
    def test_ssh(self):
        (status, output) = self.target.run('uname -a')
        self.assertEqual(status, 0, msg='SSH Test failed: %s' % output)
        (status, output) = self.target.run('cat /etc/controllerimage')
        msg = "This isn't the right image  - /etc/controllerimage " \
              "shouldn't be here %s" % output
        self.assertEqual(status, 1, msg=msg)
