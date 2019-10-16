# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class CheckSecTest(OERuntimeTestCase):

    @OEHasPackage(['checksec'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_checksec_help(self):
        status, output = self.target.run('checksec --help ')
        msg = ('checksec  command does not work as expected. '
                'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['checksec.CheckSecTest.test_checksec_help'])
    def test_checksec_xml(self):
        status, output = self.target.run('checksec --format xml --proc-all')
        msg = ('checksec xml failed. Output: %s' % output)
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['checksec.CheckSecTest.test_checksec_xml'])
    @OEHasPackage(['binutils'])
    def test_checksec_fortify(self):
        status, output = self.target.run('checksec --fortify-proc 1')
        match = re.search('FORTIFY_SOURCE support:', output)
        if not match:
            msg = ('checksec : fortify-proc failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)
