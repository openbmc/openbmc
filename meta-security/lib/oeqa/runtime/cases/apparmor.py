# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class ApparmorTest(OERuntimeTestCase):

    @OEHasPackage(['apparmor'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_apparmor_help(self):
        status, output = self.target.run('aa-status --help')
        msg = ('apparmor command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['apparmor.ApparmorTest.test_apparmor_help'])
    def test_apparmor_aa_status(self):
        status, output = self.target.run('aa-status')
        match = re.search('apparmor module is loaded.', output)
        if not match:
            msg = ('aa-status  failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['apparmor.ApparmorTest.test_apparmor_aa_status'])
    def test_apparmor_aa_complain(self):
        status, output = self.target.run('aa-complain /etc/apparmor.d/*')
        match = re.search('apparmor module is loaded.', output)
        if not match:
            msg = ('aa-complain  failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['apparmor.ApparmorTest.test_apparmor_aa_complain'])
    def test_apparmor_aa_enforce(self):
        status, output = self.target.run('aa-enforce /etc/apparmor.d/*')
        match = re.search('apparmor module is loaded.', output)
        if not match:
            msg = ('aa-enforce  failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

