#
# SPDX-License-Identifier: MIT
#

from subprocess import Popen, PIPE
import time

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oetimeout import OETimeout
from oeqa.core.decorator.data import skipIfQemu

class BootTest(OERuntimeTestCase):

    @OETimeout(120)
    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_reboot(self):
        output = ''
        count = 0
        (status, output) = self.target.run('reboot -h')
        while count < 5:
            time.sleep(5)
            cmd = 'ping -c 1 %s' % self.target.ip
            proc = Popen(cmd, shell=True, stdout=PIPE)
            output += proc.communicate()[0].decode('utf-8')
            if proc.poll() == 0:
                count += 1
            else:
                count = 0
        msg = ('Expected 5 consecutive, got %d.\n'
               'ping output is:\n%s' % (count,output))
        self.assertEqual(count, 5, msg = msg)
