#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class XorgTest(OERuntimeTestCase):

    @skipIfNotFeature('x11-base',
                      'Test requires x11 to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['xserver-nodm-init'])
    def test_xorg_running(self):
        cmd ='%s | grep -v xinit | grep [X]org' % self.tc.target_cmds['ps']
        status, output = self.target.run(cmd)
        msg = ('Xorg does not appear to be running %s' %
              self.target.run(self.tc.target_cmds['ps'])[1])
        self.assertEqual(status, 0, msg=msg)
