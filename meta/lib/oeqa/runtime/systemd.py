import unittest
import re
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("systemd"):
            skipModule("target doesn't have systemd in DISTRO_FEATURES")
    if "systemd" != oeRuntimeTest.tc.d.getVar("VIRTUAL-RUNTIME_init_manager", True):
            skipModule("systemd is not the init manager for this image")


class SystemdTest(oeRuntimeTest):

    def systemctl(self, action = '', target = '', expected = 0, verbose = False):
        command = 'systemctl %s %s' % (action, target)
        status, output = self.target.run(command)
        message = '\n'.join([command, output])
        if status != expected and verbose:
            message += self.target.run('systemctl status --full %s' % target)[1]
        self.assertEqual(status, expected, message)
        return output


class SystemdBasicTests(SystemdTest):

    @skipUnlessPassed('test_ssh')
    def test_systemd_basic(self):
        self.systemctl('--version')

    @testcase(551)
    @skipUnlessPassed('test_system_basic')
    def test_systemd_list(self):
        self.systemctl('list-unit-files')

    def settle(self):
        """
        Block until systemd has finished activating any units being activated,
        or until two minutes has elapsed.

        Returns a tuple, either (True, '') if all units have finished
        activating, or (False, message string) if there are still units
        activating (generally, failing units that restart).
        """
        import time
        endtime = time.time() + (60 * 2)
        while True:
            status, output = self.target.run('systemctl --state=activating')
            if "0 loaded units listed" in output:
                return (True, '')
            if time.time() >= endtime:
                return (False, output)
            time.sleep(10)

    @testcase(550)
    @skipUnlessPassed('test_systemd_basic')
    def test_systemd_failed(self):
        settled, output = self.settle()
        self.assertTrue(settled, msg="Timed out waiting for systemd to settle:\n" + output)

        output = self.systemctl('list-units', '--failed')
        match = re.search("0 loaded units listed", output)
        if not match:
            output += self.systemctl('status --full --failed')
        self.assertTrue(match, msg="Some systemd units failed:\n%s" % output)


class SystemdServiceTests(SystemdTest):

    def check_for_avahi(self):
        if not self.hasPackage('avahi-daemon'):
            raise unittest.SkipTest("Testcase dependency not met: need avahi-daemon installed on target")

    @skipUnlessPassed('test_systemd_basic')
    def test_systemd_status(self):
        self.check_for_avahi()
        self.systemctl('status --full', 'avahi-daemon.service')

    @testcase(695)
    @skipUnlessPassed('test_systemd_status')
    def test_systemd_stop_start(self):
        self.check_for_avahi()
        self.systemctl('stop', 'avahi-daemon.service')
        self.systemctl('is-active', 'avahi-daemon.service', expected=3, verbose=True)
        self.systemctl('start','avahi-daemon.service')
        self.systemctl('is-active', 'avahi-daemon.service', verbose=True)

    @testcase(696)
    @skipUnlessPassed('test_systemd_basic')
    def test_systemd_disable_enable(self):
        self.check_for_avahi()
        self.systemctl('disable', 'avahi-daemon.service')
        self.systemctl('is-enabled', 'avahi-daemon.service', expected=1)
        self.systemctl('enable', 'avahi-daemon.service')
        self.systemctl('is-enabled', 'avahi-daemon.service')

class SystemdJournalTests(SystemdTest):
    @skipUnlessPassed('test_ssh')
    def test_systemd_journal(self):
        (status, output) = self.target.run('journalctl')
        self.assertEqual(status, 0, output)
