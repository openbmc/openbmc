import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID

class DateTest(OERuntimeTestCase):

    def setUp(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Stopping systemd-timesyncd daemon')
            self.target.run('systemctl stop systemd-timesyncd')

    def tearDown(self):
        if self.tc.td.get('VIRTUAL-RUNTIME_init_manager') == 'systemd':
            self.logger.debug('Starting systemd-timesyncd daemon')
            self.target.run('systemctl start systemd-timesyncd')

    @OETestID(211)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_date(self):
        (status, output) = self.target.run('date +"%Y-%m-%d %T"')
        msg = 'Failed to get initial date, output: %s' % output
        self.assertEqual(status, 0, msg=msg)
        oldDate = output

        sampleDate = '"2016-08-09 10:00:00"'
        (status, output) = self.target.run("date -s %s" % sampleDate)
        self.assertEqual(status, 0, msg='Date set failed, output: %s' % output)

        (status, output) = self.target.run("date -R")
        p = re.match('Tue, 09 Aug 2016 10:00:.. \+0000', output)
        msg = 'The date was not set correctly, output: %s' % output
        self.assertTrue(p, msg=msg)

        (status, output) = self.target.run('date -s "%s"' % oldDate)
        msg = 'Failed to reset date, output: %s' % output
        self.assertEqual(status, 0, msg=msg)
