from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.data import skipIfNotInDataVar
from oeqa.core.decorator.depends import OETestDepends
from time import sleep


class FvpDevicesTest(OERuntimeTestCase):
    def run_cmd(self, cmd, check=True, retry=3):
        """
        A wrapper around self.target.run, which:
          * Fails the test on command failure by default
          * Allows the "run" behavior to be overridden in sub-classes
          * Has a retry mechanism when SSH returns 255
        """
        status = 255
        # The loop is retrying the self.target.run() which uses SSH only when
        # the SSH return code is 255, which might be an issue with
        # "Connection refused" because the port isn't open yet
        while status == 255 and retry > 0:
            (status, output) = self.target.run(cmd)
            retry -= 1
            # In case the status is 255, delay the next retry to give time to
            # the system to settle
            if status == 255:
                sleep(30)

        if status and check:
            self.fail("Command '%s' returned non-zero exit "
                      "status %d:\n%s" % (cmd, status, output))

        return (status, output)

    def check_devices(self, cls, min_count, search_drivers):
        # Find all the devices of the specified class
        cmd = f'find "/sys/class/{cls}" -type l -maxdepth 1'
        _, output = self.run_cmd(cmd)

        devices = output.split()
        self.assertGreaterEqual(len(devices),
                                min_count,
                                msg='Device count is lower than expected')

        # Assert that at least one of the devices uses at least one of the
        # drivers
        drivers = set()
        for device in devices:
            cmd = f'basename "$(readlink "{device}/device/driver")"'
            _, output = self.run_cmd(cmd)
            drivers.update(output.split())

        self.assertTrue(drivers & set(search_drivers),
                      msg='No device uses either of the drivers: ' +
                        str(search_drivers))

    def check_rng(self, hw_random, dev):
        cmd = f'cat {hw_random} | grep {dev}'
        self.run_cmd(cmd)

    def set_cpu(self, cpu_num, flag):
        # Issue echo command
        self.run_cmd(
            f'echo "{flag}" > "/sys/devices/system/cpu/cpu{cpu_num}/online"',
            check = False,
        )
        _, output = self.run_cmd(
            f'cat "/sys/devices/system/cpu/cpu{cpu_num}/online"'
        )

        return output == flag

    def enable_cpu(self, cpu_num):
        return self.set_cpu(cpu_num, "1")

    def disable_cpu(self, cpu_num):
        return self.set_cpu(cpu_num, "0")

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @skipIfNotInDataVar('TEST_FVP_DEVICES', 'cpu_hotplug',
                        'cpu_hotplug not included in BSP tests')
    def test_cpu_hotplug(self):
        _, cpus = self.run_cmd('find /sys/firmware/devicetree/base/cpus/'
                               ' -name "cpu@*" -maxdepth 1 | wc -l')

        try:
            count_cpus = int(cpus)
        except ValueError:
            self.fail(f"Expected number of CPUs, but found this:\n{cpus}")

        self.num_cpus = int(self.td.get('TEST_CPU_HOTPLUG_NUM_CPUS',
                                        count_cpus))
        try:
            # Test that all cores are online
            _, cpus = self.run_cmd('grep -c "processor" /proc/cpuinfo')
            self.assertEqual(int(cpus), self.num_cpus)
            # Don't try to disable here the only cpu present in the system.
            if self.num_cpus > 1:
                # Test that we can stop each core individually
                for i in range(self.num_cpus):
                    self.assertTrue(self.disable_cpu(i))
                    self.assertTrue(self.enable_cpu(i))

            # Test that we cannot disable all cores
            for i in range(self.num_cpus - 1):
                self.assertTrue(self.disable_cpu(i))
            # Disabling last core should trigger an error
            self.assertFalse(self.disable_cpu(self.num_cpus - 1))
        finally:
            # Ensure all CPUs are re-enabled
            for i in range(self.num_cpus):
                self.enable_cpu(i)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @skipIfNotInDataVar('TEST_FVP_DEVICES', 'rtc',
                        'rtc device not included in BSP tests')
    def test_rtc(self):
        self.check_devices("rtc", 1, ["rtc-pl031"])
        self.run_cmd('hwclock')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @skipIfNotInDataVar('TEST_FVP_DEVICES', 'watchdog',
                        'watchdog device not included in BSP tests')
    def test_watchdog(self):
        self.check_devices("watchdog", 1, ["sp805-wdt", "sbsa-gwdt"])

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @skipIfNotInDataVar('TEST_FVP_DEVICES', 'networking',
                        'networking device not included in BSP tests')
    def test_networking(self):
        self.check_devices("net", 2, ["virtio_net", "vif"])

        # Check that outbound network connections work
        self.run_cmd('wget -O /dev/null "https://www.arm.com"')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @skipIfNotInDataVar('TEST_FVP_DEVICES', 'virtiorng',
                        'virtiorng device not included in BSP tests')
    def test_virtiorng(self):
        self.check_rng('/sys/devices/virtual/misc/hw_random/rng_available',
                       'virtio_rng.0')
        self.check_rng('/sys/devices/virtual/misc/hw_random/rng_current',
                       'virtio_rng.0')

        self.run_cmd('hexdump -n 32 /dev/hwrng')
