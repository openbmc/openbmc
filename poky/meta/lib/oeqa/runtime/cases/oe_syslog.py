#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage
import time

class SyslogTest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(["busybox-syslog", "sysklogd", "rsyslog", "syslog-ng"])
    def test_syslog_running(self):
        status, output = self.target.run(self.tc.target_cmds['ps'])
        msg = "Failed to execute %s" % self.tc.target_cmds['ps']
        self.assertEqual(status, 0, msg=msg)
        msg = "No syslog daemon process; %s output:\n%s" % (self.tc.target_cmds['ps'], output)
        hasdaemon = "syslogd" in output or "syslog-ng" in output or "svlogd" in output
        self.assertTrue(hasdaemon, msg=msg)

class SyslogTestConfig(OERuntimeTestCase):

    def verif_not_running(self, pids):
        for pid in pids:
            status, err_output = self.target.run('kill -0 %s' %pid)
            if not status:
                self.logger.debug("previous %s is still running" %pid)
                return 1

    def verify_running(self, names):
        pids = []
        for name in names:
            status, pid = self.target.run('pidof %s' %name)
            if status:
                self.logger.debug("%s is not running" %name)
                return 1, pids
            pids.append(pid)
        return 0, pids


    def restart_sanity(self, names, restart_cmd, pidchange=True):
        status, original_pids = self.verify_running(names)
        if status:
            return False

        status, output = self.target.run(restart_cmd)

        msg = ('Could not restart %s service. Status and output: %s and %s' % (names, status, output))
        self.assertEqual(status, 0, msg)

        if not pidchange:
            return True

        # Always check for an error, most likely a race between shutting down and starting up
        timeout = time.time() + 30

        restarted = False
        status = ""
        while time.time() < timeout:
            # Verify the previous ones are no longer running
            status = self.verif_not_running(original_pids)
            if status:
                status = "Original syslog processes still running"
                continue

            status, pids = self.verify_running(names)
            if status:
                status = "New syslog processes not running"
                continue

            # Everything is fine now, so exit to continue the test
            restarted = True
            break

        msg = ('%s didn\'t appear to restart: %s' % (names, status))
        self.assertTrue(restarted, msg)

        return True

    @OETestDepends(['oe_syslog.SyslogTest.test_syslog_running'])
    def test_syslog_logger(self):
        status, output = self.target.run('logger foobar')
        msg = "Can't log into syslog. Output: %s " % output
        self.assertEqual(status, 0, msg=msg)

        # There is no way to flush the logger to disk in all cases
        time.sleep(1)

        status, output = self.target.run('grep foobar /var/log/messages')
        if status != 0:
            if self.tc.td.get("VIRTUAL-RUNTIME_init_manager") == "systemd":
                status, output = self.target.run('journalctl -o cat | grep foobar')
            else:
                status, output = self.target.run('logread | grep foobar')
        msg = ('Test log string not found in /var/log/messages or logread.'
               ' Output: %s ' % output)
        self.assertEqual(status, 0, msg=msg)


    @OETestDepends(['oe_syslog.SyslogTest.test_syslog_running'])
    def test_syslog_restart(self):
        if self.restart_sanity(['systemd-journald'], 'systemctl restart syslog.service', pidchange=False):
            pass
        elif self.restart_sanity(['rsyslogd'], '/etc/init.d/rsyslog restart'):
            pass
        elif self.restart_sanity(['syslogd', 'klogd'], '/etc/init.d/syslog restart'):
            pass
        else:
            self.logger.info("No syslog found to restart, ignoring")


    @OETestDepends(['oe_syslog.SyslogTestConfig.test_syslog_logger'])
    @OEHasPackage(["busybox-syslog"])
    @skipIfDataVar('VIRTUAL-RUNTIME_init_manager', 'systemd',
                   'Not appropiate for systemd image')
    def test_syslog_startup_config(self):
        cmd = 'echo "LOGFILE=/var/log/test" >> /etc/syslog-startup.conf'
        self.target.run(cmd)

        self.test_syslog_restart()

        cmd = 'logger foobar'
        status, output = self.target.run(cmd)
        msg = 'Logger command failed, %s. Output: %s ' % (status, output)
        self.assertEqual(status, 0, msg=msg)

        cmd = 'cat /var/log/test'
        status, output = self.target.run(cmd)
        if "foobar" not in output or status:
            self.fail("'foobar' not found in logfile, status %s, contents %s" % (status, output))

        cmd = "sed -i 's#LOGFILE=/var/log/test##' /etc/syslog-startup.conf"
        self.target.run(cmd)
        self.test_syslog_restart()
