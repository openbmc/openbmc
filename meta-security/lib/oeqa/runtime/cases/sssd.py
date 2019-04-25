# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SSSDTest(OERuntimeTestCase):

    @OEHasPackage(['sssd'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_sssd_help(self):
        status, output = self.target.run('sssctl --help')
        msg = ('sssctl command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 1, msg = msg)

    @OETestDepends(['sssd.SSSDTest.test_sssd_help'])
    def test_sssd_sssctl_conf_perms_chk(self):
        status, output = self.target.run('sssctl domain-status')
        match = re.search('ConfDB initialization has failed', output)
        if match:
            msg = ('sssctl domain-status failed, check sssd.conf perms. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['sssd.SSSDTest.test_sssd_sssctl_conf_perms_chk'])
    def test_sssd_sssctl_deamon(self):
        status, output = self.target.run('sssctl domain-status')
        match = re.search('No domains configured, fatal error!', output)
        if match:
            msg = ('sssctl domain-status failed, sssd.conf not setup correctly. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

