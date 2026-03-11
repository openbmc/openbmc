# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re
import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SamhainTest(OERuntimeTestCase):

    @OEHasPackage(['samhain-standalone'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_samhain_help(self):
        machine = self.td.get('MACHINE', '')
        status, output = self.target.run('echo "127.0.0.1 %s.localdomain  %s" >> /etc/hosts' % (machine, machine))
        msg = ("samhain can't append hosts. "
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

        status, output = self.target.run('samhain --help')
        msg = ('samhain command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['samhain.SamhainTest.test_samhain_help'])
    def test_samhain_init_db(self):
        status, output = self.target.run('samhain -t init')
        match = re.search('FAILED: 0 ', output)
        if not match:
            msg = ('samhain database init had an unexpected failure. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['samhain.SamhainTest.test_samhain_init_db'])
    def test_samhain_db_check(self):
        status, output = self.target.run('samhain -t check')
        match = re.search('FAILED: 0 ', output)
        if not match:
            msg = ('samhain errors found in db. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)
