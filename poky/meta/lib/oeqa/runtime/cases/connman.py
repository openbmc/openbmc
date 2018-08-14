from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class ConnmanTest(OERuntimeTestCase):

    def service_status(self, service):
        if 'systemd' in self.tc.td['DISTRO_FEATURES']:
            (_, output) = self.target.run('systemctl status -l %s' % service)
            return output
        else:
            return "Unable to get status or logs for %s" % service

    @OETestID(961)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["connman"])
    def test_connmand_help(self):
        (status, output) = self.target.run('/usr/sbin/connmand --help')
        msg = 'Failed to get connman help. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestID(221)
    @OETestDepends(['connman.ConnmanTest.test_connmand_help'])
    def test_connmand_running(self):
        cmd = '%s | grep [c]onnmand' % self.tc.target_cmds['ps']
        (status, output) = self.target.run(cmd)
        if status != 0:
            self.logger.info(self.service_status("connman"))
            self.fail("No connmand process running")
