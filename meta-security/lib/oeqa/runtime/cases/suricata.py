# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SuricataTest(OERuntimeTestCase):

    @OEHasPackage(['suricata'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_suricata_help(self):
        status, output = self.target.run('suricata --help')
        msg = ('suricata command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 1, msg = msg)

    @OETestDepends(['suricata.SuricataTest.test_suricata_help'])
    def test_suricata_unittest(self):
        status, output = self.target.run('suricata -u')
        match = re.search('FAILED: 0 ', output)
        if not match:
            msg = ('suricata unittest had an unexpected failure. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)
