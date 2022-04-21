#
# SPDX-License-Identifier: MIT
#

from subprocess import Popen, PIPE

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.oetimeout import OETimeout
from oeqa.core.exception import OEQATimeoutError

class PingTest(OERuntimeTestCase):

    @OETimeout(30)
    def test_ping(self):
        output = ''
        count = 0
        try:
            while count < 5:
                cmd = 'ping -c 1 %s' % self.target.ip
                proc = Popen(cmd, shell=True, stdout=PIPE)
                output += proc.communicate()[0].decode('utf-8')
                if proc.poll() == 0:
                    count += 1
                else:
                    count = 0
        except OEQATimeoutError:
            self.fail("Ping timeout error for address %s, count %s, output: %s" % (self.target.ip, count, output))
        msg = ('Expected 5 consecutive, got %d.\n'
               'ping output is:\n%s' % (count,output))
        self.assertEqual(count, 5, msg = msg)
