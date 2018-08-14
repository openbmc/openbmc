from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature

class LddTest(OERuntimeTestCase):

    @OETestID(962)
    @skipIfNotFeature('tools-sdk',
                      'Test requires tools-sdk to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_ldd_exists(self):
        status, output = self.target.run('which ldd')
        msg = 'ldd does not exist in PATH: which ldd: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestID(239)
    @OETestDepends(['ldd.LddTest.test_ldd_exists'])
    def test_ldd_rtldlist_check(self):
        cmd = ('for i in $(which ldd | xargs cat | grep "^RTLDLIST"| '
              'cut -d\'=\' -f2|tr -d \'"\'); '
              'do test -f $i && echo $i && break; done')
        status, output = self.target.run(cmd)
        msg = "ldd path not correct or RTLDLIST files don't exist."
        self.assertEqual(status, 0, msg=msg)
