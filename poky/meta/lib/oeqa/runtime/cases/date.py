#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class DateTest(OERuntimeTestCase):

    def setUp(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Stopping systemd-timesyncd daemon')
            self.target.run('systemctl disable --now --runtime systemd-timesyncd')

    def tearDown(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Starting systemd-timesyncd daemon')
            self.target.run('systemctl enable --now --runtime systemd-timesyncd')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['coreutils', 'busybox'])
    def test_date(self):
        (status, output) = self.target.run('date +"%Y-%m-%d %T"')
        msg = 'Failed to get initial date, output: %s' % output
        self.assertEqual(status, 0, msg=msg)
        oldDate = output

        sampleTimestamp = 1488800000
        (status, output) = self.target.run("date -s @%d" % sampleTimestamp)
        self.assertEqual(status, 0, msg='Date set failed, output: %s' % output)

        (status, output) = self.target.run('date +"%s"')
        msg = 'The date was not set correctly, output: %s' % output
        self.assertTrue(int(output) - sampleTimestamp < 300, msg=msg)

        (status, output) = self.target.run('date -s "%s"' % oldDate)
        msg = 'Failed to reset date, output: %s' % output
        self.assertEqual(status, 0, msg=msg)
