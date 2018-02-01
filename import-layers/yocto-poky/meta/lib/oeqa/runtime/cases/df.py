from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID

class DfTest(OERuntimeTestCase):

    @OETestID(234)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_df(self):
        cmd = "df / | sed -n '2p' | awk '{print $4}'"
        (status,output) = self.target.run(cmd)
        msg = 'Not enough space on image. Current size is %s' % output
        self.assertTrue(int(output)>5120, msg=msg)
