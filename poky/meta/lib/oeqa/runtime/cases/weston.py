#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class WestonTest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['weston'])
    def test_weston_running(self):
        cmd ='%s | grep [w]eston-desktop-shell' % self.tc.target_cmds['ps']
        status, output = self.target.run(cmd)
        msg = ('Weston does not appear to be running %s' %
              self.target.run(self.tc.target_cmds['ps'])[1])
        self.assertEqual(status, 0, msg=msg)
