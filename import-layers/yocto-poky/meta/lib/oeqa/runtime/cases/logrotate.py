# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=289 testcase
# Note that the image under test must have logrotate installed

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class LogrotateTest(OERuntimeTestCase):

    @classmethod
    def tearDownClass(cls):
        cls.tc.target.run('rm -rf $HOME/logrotate_dir')

    @OETestID(1544)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['logrotate'])
    def test_1_logrotate_setup(self):
        status, output = self.target.run('mkdir $HOME/logrotate_dir')
        msg = 'Could not create logrotate_dir. Output: %s' % output
        self.assertEqual(status, 0, msg = msg)

        cmd = ('sed -i "s#wtmp {#wtmp {\\n    olddir $HOME/logrotate_dir#"'
               ' /etc/logrotate.conf')
        status, output = self.target.run(cmd)
        msg = ('Could not write to logrotate.conf file. Status and output: '
               ' %s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestID(1542)
    @OETestDepends(['logrotate.LogrotateTest.test_1_logrotate_setup'])
    def test_2_logrotate(self):
        status, output = self.target.run('logrotate -f /etc/logrotate.conf')
        msg = ('logrotate service could not be reloaded. Status and output: '
                '%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

        _, output = self.target.run('ls -la $HOME/logrotate_dir/ | wc -l')
        msg = ('new logfile could not be created. List of files within log '
               'directory: %s' % (
                self.target.run('ls -la $HOME/logrotate_dir')[1]))
        self.assertTrue(int(output)>=3, msg = msg)
