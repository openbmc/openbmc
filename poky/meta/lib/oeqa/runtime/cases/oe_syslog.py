from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage

class SyslogTest(OERuntimeTestCase):

    @OETestID(201)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["busybox-syslog", "sysklogd", "rsyslog", "syslog-ng"])
    def test_syslog_running(self):
        status, output = self.target.run(self.tc.target_cmds['ps'])
        msg = "Failed to execute %s" % self.tc.target_cmds['ps']
        self.assertEqual(status, 0, msg=msg)
        msg = "No syslog daemon process; %s output:\n%s" % (self.tc.target_cmds['ps'], output)
        hasdaemon = "syslogd" in output or "syslog-ng" in output
        self.assertTrue(hasdaemon, msg=msg)

class SyslogTestConfig(OERuntimeTestCase):

    @OETestID(1149)
    @OETestDepends(['oe_syslog.SyslogTest.test_syslog_running'])
    def test_syslog_logger(self):
        status, output = self.target.run('logger foobar')
        msg = "Can't log into syslog. Output: %s " % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('grep foobar /var/log/messages')
        if status != 0:
            if self.tc.td.get("VIRTUAL-RUNTIME_init_manager") == "systemd":
                status, output = self.target.run('journalctl -o cat | grep foobar')
            else:
                status, output = self.target.run('logread | grep foobar')
        msg = ('Test log string not found in /var/log/messages or logread.'
               ' Output: %s ' % output)
        self.assertEqual(status, 0, msg=msg)

    @OETestID(1150)
    @OETestDepends(['oe_syslog.SyslogTest.test_syslog_running'])
    def test_syslog_restart(self):
        if "systemd" != self.tc.td.get("VIRTUAL-RUNTIME_init_manager", ""):
            (_, _) = self.target.run('/etc/init.d/syslog restart')
        else:
            (_, _) = self.target.run('systemctl restart syslog.service')


    @OETestID(202)
    @OETestDepends(['oe_syslog.SyslogTestConfig.test_syslog_logger'])
    @OEHasPackage(["busybox-syslog"])
    @skipIfDataVar('VIRTUAL-RUNTIME_init_manager', 'systemd',
                   'Not appropiate for systemd image')
    def test_syslog_startup_config(self):
        cmd = 'echo "LOGFILE=/var/log/test" >> /etc/syslog-startup.conf'
        self.target.run(cmd)
        status, output = self.target.run('/etc/init.d/syslog restart')
        msg = ('Could not restart syslog service. Status and output:'
               ' %s and %s' % (status,output))
        self.assertEqual(status, 0, msg)

        cmd = 'logger foobar && grep foobar /var/log/test'
        status,output = self.target.run(cmd)
        msg = 'Test log string not found. Output: %s ' % output
        self.assertEqual(status, 0, msg=msg)

        cmd = "sed -i 's#LOGFILE=/var/log/test##' /etc/syslog-startup.conf"
        self.target.run(cmd)
        self.target.run('/etc/init.d/syslog restart')
