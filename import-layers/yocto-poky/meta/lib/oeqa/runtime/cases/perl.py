import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class PerlTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        src = os.path.join(cls.tc.files_dir, 'test.pl')
        dst = '/tmp/test.pl'
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDownClass(cls):
        dst = '/tmp/test.pl'
        cls.tc.target.run('rm %s' % dst)

    @OETestID(1141)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['perl'])
    def test_perl_exists(self):
        status, output = self.target.run('which perl')
        msg = 'Perl binary not in PATH or not on target.'
        self.assertEqual(status, 0, msg=msg)

    @OETestID(208)
    @OETestDepends(['perl.PerlTest.test_perl_exists'])
    def test_perl_works(self):
        status, output = self.target.run('perl /tmp/test.pl')
        msg = 'Exit status was not 0. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        msg = 'Incorrect output: %s' % output
        self.assertEqual(output, "the value of a is 0.01", msg=msg)
