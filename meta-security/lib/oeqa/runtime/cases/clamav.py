# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re
from tempfile import mkstemp

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class ClamavTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        cls.tmp_fd, cls.tmp_path = mkstemp()
        with os.fdopen(cls.tmp_fd, 'w') as f:
            # use gooled public dns
            f.write("nameserver 8.8.8.8")
            f.write(os.linesep)
            f.write("nameserver 8.8.4.4")
            f.write(os.linesep)
            f.write("nameserver 127.0.0.1")
            f.write(os.linesep)

    @classmethod
    def tearDownClass(cls):
        os.remove(cls.tmp_path)

    @OEHasPackage(['clamav'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_freshclam_help(self):
        status, output = self.target.run('freshclam --help ')
        msg = ('freshclam --hlep  command does not work as expected. ', 
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['clamav.ClamavTest.test_freshclam_help'])
    @OEHasPackage(['openssh-scp', 'dropbear'])
    def test_ping_clamav_net(self):
        dst = '/etc/resolv.conf'
        self.tc.target.run('rm -f %s' % dst)
        (status, output) = self.tc.target.copyTo(self.tmp_path, dst)
        msg = 'File could not be copied. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('ping -c 1 database.clamav.net')
        msg = ('ping database.clamav.net failed: output is:\n%s' % output)
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['clamav.ClamavTest.test_ping_clamav_net'])
    def test_freshclam_download(self):
        status, output = self.target.run('freshclam --show-progress')
        match = re.search('Database updated', output)
        #match = re.search('main.cvd is up to date', output)
        if not match:
            msg = ('freshclam : DB dowbload failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)

    @OETestDepends(['clamav.ClamavTest.test_ping_clamav_net'])
    def test_freshclam_check_mirrors(self):
        status, output = self.target.run('freshclam --list-mirrors')
        match = re.search('Failures: 0', output)
        if not match:
            msg = ('freshclam --list-mirrors: failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)

