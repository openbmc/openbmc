from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfFeature
from oeqa.runtime.decorator.package import OEHasPackage

import re

class RTCTest(OERuntimeTestCase):

    def setUp(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Stopping systemd-timesyncd daemon')
            self.target.run('systemctl disable --now --runtime systemd-timesyncd')

    def tearDown(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Starting systemd-timesyncd daemon')
            self.target.run('systemctl enable --now --runtime systemd-timesyncd')

    @skipIfFeature('read-only-rootfs',
                   'Test does not work with read-only-rootfs in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['coreutils', 'busybox'])
    def test_rtc(self):
        (status, output) = self.target.run('hwclock -r')
        self.assertEqual(status, 0, msg='Failed to get RTC time, output: %s' % output)

        (status, current_datetime) = self.target.run('date +"%m%d%H%M%Y"')
        self.assertEqual(status, 0, msg='Failed to get system current date & time, output: %s' % current_datetime)

        example_datetime = '062309452008'
        (status, output) = self.target.run('date %s ; hwclock -w ; hwclock -r' % example_datetime)
        check_hwclock = re.search('2008-06-23 09:45:..', output)
        self.assertTrue(check_hwclock, msg='The RTC time was not set correctly, output: %s' % output)

        (status, output) = self.target.run('date %s' % current_datetime)
        self.assertEqual(status, 0, msg='Failed to reset system date & time, output: %s' % output)

        (status, output) = self.target.run('hwclock -w')
        self.assertEqual(status, 0, msg='Failed to reset RTC time, output: %s' % output)
