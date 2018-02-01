from subprocess import Popen, PIPE

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.oetimeout import OETimeout

class PingTest(OERuntimeTestCase):

    @OETimeout(30)
    @OETestID(964)
    def test_ping(self):
        output = ''
        count = 0
        while count < 5:
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
