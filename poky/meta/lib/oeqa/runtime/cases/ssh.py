from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID

class SSHTest(OERuntimeTestCase):

    @OETestID(224)
    @OETestDepends(['ping.PingTest.test_ping'])
    def test_ssh(self):
        (status, output) = self.target.run('uname -a')
        self.assertEqual(status, 0, msg='SSH Test failed: %s' % output)
        (status, output) = self.target.run('cat /etc/masterimage')
        msg = "This isn't the right image  - /etc/masterimage " \
              "shouldn't be here %s" % output
        self.assertEqual(status, 1, msg=msg)
