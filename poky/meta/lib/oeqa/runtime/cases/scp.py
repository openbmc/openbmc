import os
from tempfile import mkstemp

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class ScpTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        cls.tmp_fd, cls.tmp_path = mkstemp()
        with os.fdopen(cls.tmp_fd, 'w') as f:
            f.seek(2 ** 22 -1)
            f.write(os.linesep)

    @classmethod
    def tearDownClass(cls):
        os.remove(cls.tmp_path)

    @OETestID(220)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['openssh-scp', 'dropbear'])
    def test_scp_file(self):
        dst = '/tmp/test_scp_file'

        (status, output) = self.target.copyTo(self.tmp_path, dst)
        msg = 'File could not be copied. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        (status, output) = self.target.run('ls -la %s' % dst)
        self.assertEqual(status, 0, msg = 'SCP test failed')

        self.target.run('rm %s' % dst)
