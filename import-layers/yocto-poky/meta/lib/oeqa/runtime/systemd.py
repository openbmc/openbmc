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

    #TODO: use pyjournalctl instead
    def journalctl(self, args='',l_match_units=[]):
        """
        Request for the journalctl output to the current target system

        Arguments:
        -args, an optional argument pass through argument
        -l_match_units, an optional list of units to filter the output
        Returns:
        -string output of the journalctl command
        Raises:
        -AssertionError, on remote commands that fail
        -ValueError, on a journalctl call with filtering by l_match_units that
        returned no entries
        """
        query_units=""
        if len(l_match_units):
            query_units = ['_SYSTEMD_UNIT='+unit for unit in l_match_units]
            query_units = " ".join(query_units)
        command = 'journalctl %s %s' %(args, query_units)
        status, output = self.target.run(command)
        if status:
            raise AssertionError("Command '%s' returned non-zero exit \
                    code %d:\n%s" % (command, status, output))
        if len(output) == 1 and "-- No entries --" in output:
            raise ValueError("List of units to match: %s, returned no entries"
                    % l_match_units)
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

    @skipUnlessPassed('test_systemd_basic')
    def test_systemd_boot_time(self, systemd_TimeoutStartSec=90):
        """
        Get the target boot time from journalctl and log it

        Arguments:
        -systemd_TimeoutStartSec, an optional argument containing systemd's
        unit start timeout to compare against
        """

        # the expression chain that uniquely identifies the time boot message
        expr_items=["Startup finished","kernel", "userspace","\.$"]
        try:
            output = self.journalctl(args="-o cat --reverse")
        except AssertionError:
            self.fail("Error occurred while calling journalctl")
        if not len(output):
            self.fail("Error, unable to get startup time from systemd journal")

        # check for the regular expression items that match the startup time
        for line in output.split('\n'):
            check_match = "".join(re.findall(".*".join(expr_items), line))
            if check_match: break
        # put the startup time in the test log
        if check_match:
            print "%s" % check_match
        else:
            self.skipTest("Error at obtaining the boot time from journalctl")
        boot_time_sec = 0

        # get the numeric values from the string and convert them to seconds
        # same data will be placed in list and string for manipulation
        l_boot_time = check_match.split(" ")[-2:]
        s_boot_time = " ".join(l_boot_time)
        try:
            # Obtain the minutes it took to boot
            if l_boot_time[0].endswith('min') and l_boot_time[0][0].isdigit():
                boot_time_min = s_boot_time.split("min")[0]
                # convert to seconds and accumulate it
                boot_time_sec += int(boot_time_min) * 60
            # Obtain the seconds it took to boot and accumulate
            boot_time_sec += float(l_boot_time[1].split("s")[0])
        except ValueError:
            self.skipTest("Error when parsing time from boot string")
        #Assert the target boot time against systemd's unit start timeout
        if boot_time_sec > systemd_TimeoutStartSec:
            print "Target boot time %s exceeds systemd's TimeoutStartSec %s"\
                    %(boot_time_sec, systemd_TimeoutStartSec)
