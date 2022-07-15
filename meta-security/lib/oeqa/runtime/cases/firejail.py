# Copyright (C) 2022 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class FirejailTest(OERuntimeTestCase):

    @OEHasPackage(['firejail'])
    @OEHasPackage(['libseccomp'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_firejail_basic(self):
        status, output = self.target.run('firejail --help')
        msg = ('Firejail --help command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)
