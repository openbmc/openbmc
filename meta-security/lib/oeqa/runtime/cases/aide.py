# Copyright (C) 2022 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class AideTest(OERuntimeTestCase):

    @OEHasPackage(['aide'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_aide_help(self):
        status, output = self.target.run('aide --help')
        msg = ('Aide help command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['aide.AideTest.test_aide_help'])
    def test_aide_dbinit(self):
        status, output = self.target.run('aide --init')
        match = re.search('Number of entries:', output)
        if not match:
            msg = ('Aide db init failed: output is:\n%s' % output)
            self.assertEqual(status, 0, msg = msg)
