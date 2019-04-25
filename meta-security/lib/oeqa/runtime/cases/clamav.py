# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class ClamavTest(OERuntimeTestCase):

    @OEHasPackage(['clamav'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_freshclam_help(self):
        status, output = self.target.run('freshclam --help ')
        msg = ('freshclam --hlep  command does not work as expected. ', 
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['clamav.ClamavTest.test_freshclam_help'])
    def test_freshclam_download(self):
        status, output = self.target.run('freshclam --show-progress')
        match = re.search('Database updated', output)
        #match = re.search('main.cvd is up to date', output)
        if not match:
            msg = ('freshclam : DB dowbload failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)

    @OETestDepends(['clamav.ClamavTest.test_freshclam_download'])
    def test_freshclam_check_mirrors(self):
        status, output = self.target.run('freshclam --list-mirrors')
        match = re.search('Failures: 0', output)
        if not match:
            msg = ('freshclam --list-mirrors: failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)

