#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import collections
import os

from shutil import rmtree
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends

common_errors = [
    "(WW) warning, (EE) error, (NI) not implemented, (??) unknown.",
    "dma timeout",
    "can\'t add hid device:",
    "usbhid: probe of ",
    "_OSC failed (AE_ERROR)",
    "_OSC failed (AE_SUPPORT)",
    "AE_ALREADY_EXISTS",
    "ACPI _OSC request failed (AE_SUPPORT)",
    "can\'t disable ASPM",
    "Failed to load module \"vesa\"",
    "Failed to load module vesa",
    "Failed to load module \"modesetting\"",
    "Failed to load module modesetting",
    "Failed to load module \"glx\"",
    "Failed to load module \"fbdev\"",
    "Failed to load module fbdev",
    "Failed to load module glx",
    "[drm] Cannot find any crtc or sizes",
    "_OSC failed (AE_NOT_FOUND); disabling ASPM",
    "Open ACPI failed (/var/run/acpid.socket) (No such file or directory)",
    "NX (Execute Disable) protection cannot be enabled: non-PAE kernel!",
    "hd.: possibly failed opcode",
    'NETLINK INITIALIZATION FAILED',
    'kernel: Cannot find map file',
    'omap_hwmod: debugss: _wait_target_disable failed',
    'VGA arbiter: cannot open kernel arbiter, no multi-card support',
    'Failed to find URL:http://ipv4.connman.net/online/status.html',
    'Online check failed for',
    'netlink init failed',
    'Fast TSC calibration',
    "BAR 0-9",
    "Failed to load module \"ati\"",
    "controller can't do DEVSLP, turning off",
    "stmmac_dvr_probe: warning: cannot get CSR clock",
    "error: couldn\'t mount because of unsupported optional features",
    "GPT: Use GNU Parted to correct GPT errors",
    "Cannot set xattr user.Librepo.DownloadInProgress",
    "Failed to read /var/lib/nfs/statd/state: Success",
    "error retry time-out =",
    "logind: cannot setup systemd-logind helper (-61), using legacy fallback",
    "Failed to rename network interface",
    "Failed to process device, ignoring: Device or resource busy",
    "Cannot find a map file",
    "[rdrand]: Initialization Failed",
    "[rndr  ]: Initialization Failed",
    "[pulseaudio] authkey.c: Failed to open cookie file",
    "[pulseaudio] authkey.c: Failed to load authentication key",
    "was skipped because of a failed condition check",
    "was skipped because all trigger condition checks failed",
    "xf86OpenConsole: Switching VT failed",
    "Failed to read LoaderConfigTimeoutOneShot variable, ignoring: Operation not supported",
    "Failed to read LoaderEntryOneShot variable, ignoring: Operation not supported",
    "invalid BAR (can't size)",
    ]

x86_common = [
    '[drm:psb_do_init] *ERROR* Debug is',
    'wrong ELF class',
    'Could not enable PowerButton event',
    'probe of LNXPWRBN:00 failed with error -22',
    'pmd_set_huge: Cannot satisfy',
    'failed to setup card detect gpio',
    'amd_nb: Cannot enumerate AMD northbridges',
    'failed to retrieve link info, disabling eDP',
    'Direct firmware load for iwlwifi',
    'Direct firmware load for regulatory.db',
    'failed to load regulatory.db',
] + common_errors

qemux86_common = [
    'wrong ELF class',
    "fail to add MMCONFIG information, can't access extended PCI configuration space under this bridge.",
    "can't claim BAR ",
    'amd_nb: Cannot enumerate AMD northbridges',
    'tsc: HPET/PMTIMER calibration failed',
    "modeset(0): Failed to initialize the DRI2 extension",
    "glamor initialization failed",
    "blk_update_request: I/O error, dev fd0, sector 0 op 0x0:(READ)",
    "floppy: error",
    'failed to IDENTIFY (I/O error, err_mask=0x4)',
] + common_errors

ignore_errors = {
    'default' : common_errors,
    'qemux86' : [
        'Failed to access perfctr msr (MSR',
        ] + qemux86_common,
    'qemux86-64' : qemux86_common,
    'qemumips' : [
        'Failed to load module "glx"',
        'cacheinfo: Failed to find cpu0 device node',
        ] + common_errors,
    'qemumips64' : [
        'cacheinfo: Failed to find cpu0 device node',
         ] + common_errors,
    'qemuppc' : [
        'PCI 0000:00 Cannot reserve Legacy IO [io  0x0000-0x0fff]',
        'host side 80-wire cable detection failed, limiting max speed',
        'mode "640x480" test failed',
        'Failed to load module "glx"',
        'can\'t handle BAR above 4GB',
        'Cannot reserve Legacy IO',
        ] + common_errors,
    'qemuppc64' : [
        'vio vio: uevent: failed to send synthetic uevent',
        'synth uevent: /devices/vio: failed to send uevent',
        'PCI 0000:00 Cannot reserve Legacy IO [io  0x10000-0x10fff]',
        ] + common_errors,
    'qemuarmv5' : [
        'mmci-pl18x: probe of fpga:05 failed with error -22',
        'mmci-pl18x: probe of fpga:0b failed with error -22',
        'Failed to load module "glx"',
        'OF: amba_device_add() failed (-19) for /amba/smc@10100000',
        'OF: amba_device_add() failed (-19) for /amba/mpmc@10110000',
        'OF: amba_device_add() failed (-19) for /amba/sctl@101e0000',
        'OF: amba_device_add() failed (-19) for /amba/watchdog@101e1000',
        'OF: amba_device_add() failed (-19) for /amba/sci@101f0000',
        'OF: amba_device_add() failed (-19) for /amba/spi@101f4000',
        'OF: amba_device_add() failed (-19) for /amba/ssp@101f4000',
        'OF: amba_device_add() failed (-19) for /amba/fpga/sci@a000',
        'Failed to initialize \'/amba/timer@101e3000\': -22',
        'jitterentropy: Initialization failed with host not compliant with requirements: 2',
        'clcd-pl11x: probe of 10120000.display failed with error -2',
        'arm-charlcd 10008000.lcd: error -ENXIO: IRQ index 0 not found'
        ] + common_errors,
    'qemuarm64' : [
        'Fatal server error:',
        '(EE) Server terminated with error (1). Closing log file.',
        'dmi: Firmware registration failed.',
        'irq: type mismatch, failed to map hwirq-27 for /intc',
        'logind: failed to get session seat',
        ] + common_errors,
    'intel-core2-32' : [
        'ACPI: No _BQC method, cannot determine initial brightness',
        '[Firmware Bug]: ACPI: No _BQC method, cannot determine initial brightness',
        '(EE) Failed to load module "psb"',
        '(EE) Failed to load module psb',
        '(EE) Failed to load module "psbdrv"',
        '(EE) Failed to load module psbdrv',
        '(EE) open /dev/fb0: No such file or directory',
        '(EE) AIGLX: reverting to software rendering',
        'dmi: Firmware registration failed.',
        'ioremap error for 0x78',
        ] + x86_common,
    'intel-corei7-64' : [
        'can\'t set Max Payload Size to 256',
        'intel_punit_ipc: can\'t request region for resource',
        '[drm] parse error at position 4 in video mode \'efifb\'',
        'ACPI Error: Could not enable RealTimeClock event',
        'ACPI Warning: Could not enable fixed event - RealTimeClock',
        'hci_intel INT33E1:00: Unable to retrieve gpio',
        'hci_intel: probe of INT33E1:00 failed',
        'can\'t derive routing for PCI INT A',
        'failed to read out thermal zone',
        'Bluetooth: hci0: Setting Intel event mask failed',
        'ttyS2 - failed to request DMA',
        'Bluetooth: hci0: Failed to send firmware data (-38)',
        'atkbd serio0: Failed to enable keyboard on isa0060/serio0',
        ] + x86_common,
    'genericx86' : x86_common,
    'genericx86-64' : [
        'Direct firmware load for i915',
        'Failed to load firmware i915',
        'Failed to fetch GuC',
        'Failed to initialize GuC',
        'Failed to load DMC firmware',
        'The driver is built-in, so to load the firmware you need to',
        ] + x86_common,
    'beaglebone-yocto' : [
        'Direct firmware load for regulatory.db',
        'failed to load regulatory.db',
        'l4_wkup_cm',
        'Failed to load module "glx"',
        'Failed to make EGL context current',
        'glamor initialization failed',
        ] + common_errors,
}

class ParseLogsTest(OERuntimeTestCase):

    # Which log files should be collected
    log_locations = ["/var/log/", "/var/log/dmesg", "/tmp/dmesg_output.log"]

    # The keywords that identify error messages in the log files
    errors = ["error", "cannot", "can't", "failed"]

    @classmethod
    def setUpClass(cls):
        # When systemd is enabled we need to notice errors on
        # circular dependencies in units.
        if 'systemd' in cls.td.get('DISTRO_FEATURES'):
            cls.errors.extend([
                'Found ordering cycle on',
                'Breaking ordering cycle by deleting job',
                'deleted to break ordering cycle',
                'Ordering cycle found, skipping',
                ])

        cls.errors = [s.casefold() for s in cls.errors]

        try:
            cls.ignore_errors = [s.casefold() for s in ignore_errors[cls.td.get('MACHINE')]]
        except KeyError:
            cls.logger.info('No ignore list found for this machine, using default')
            cls.ignore_errors = [s.casefold() for s in ignore_errors['default']]

    # Go through the log locations provided and if it's a folder
    # create a list with all the .log files in it, if it's a file
    # just add it to that list.
    def getLogList(self, log_locations):
        logs = []
        for location in log_locations:
            status, _ = self.target.run('test -f %s' % location)
            if status == 0:
                logs.append(location)
            else:
                status, _ = self.target.run('test -d %s' % location)
                if status == 0:
                    cmd = 'find %s -name \\*.log -maxdepth 1 -type f' % location
                    status, output = self.target.run(cmd)
                    if status == 0:
                        output = output.splitlines()
                        for logfile in output:
                            logs.append(os.path.join(location, logfile))
        return logs

    # Copy the log files to be parsed locally
    def transfer_logs(self, log_list):
        workdir = self.td.get('WORKDIR')
        self.target_logs = workdir + '/' + 'target_logs'
        target_logs = self.target_logs
        if os.path.exists(target_logs):
            rmtree(self.target_logs)
        os.makedirs(target_logs)
        for f in log_list:
            self.target.copyFrom(str(f), target_logs)

    # Get the local list of logs
    def get_local_log_list(self, log_locations):
        self.transfer_logs(self.getLogList(log_locations))
        list_dir = os.listdir(self.target_logs)
        dir_files = [os.path.join(self.target_logs, f) for f in list_dir]
        logs = [f for f in dir_files if os.path.isfile(f)]
        return logs

    def get_context(self, lines, index, before=6, after=3):
        """
        Given a set of lines and the index of the line that is important, return
        a number of lines surrounding that line.
        """
        last = len(lines)

        start = index - before
        end = index + after + 1

        if start < 0:
            end -= start
            start = 0
        if end > last:
            start -= end - last
            end = last

        return lines[start:end]

    def test_get_context(self):
        """
        A test case for the test case.
        """
        lines = list(range(0,10))
        self.assertEqual(self.get_context(lines, 0, 2, 1), [0, 1, 2, 3])
        self.assertEqual(self.get_context(lines, 5, 2, 1), [3, 4, 5, 6])
        self.assertEqual(self.get_context(lines, 9, 2, 1), [6, 7, 8, 9])

    def parse_logs(self, logs, lines_before=10, lines_after=10):
        """
        Search the log files @logs looking for error lines (marked by
        @self.errors), ignoring anything listed in @self.ignore_errors.

        Returns a dictionary of log filenames to a dictionary of error lines to
        the error context (controlled by @lines_before and @lines_after).
        """
        results = collections.defaultdict(dict)

        for log in logs:
            with open(log) as f:
                lines = f.readlines()

            for i, line in enumerate(lines):
                line = line.strip()
                line_lower = line.casefold()

                if any(keyword in line_lower for keyword in self.errors):
                    if not any(ignore in line_lower for ignore in self.ignore_errors):
                        results[log][line] = "".join(self.get_context(lines, i, lines_before, lines_after))

        return results

    # Get the output of dmesg and write it in a file.
    # This file is added to log_locations.
    def write_dmesg(self):
        (status, dmesg) = self.target.run('dmesg > /tmp/dmesg_output.log')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_parselogs(self):
        self.write_dmesg()
        log_list = self.get_local_log_list(self.log_locations)
        result = self.parse_logs(log_list)

        errcount = 0
        self.msg = ""
        for log in result:
            self.msg += 'Log: ' + log + '\n'
            self.msg += '-----------------------\n'
            for error in result[log]:
                errcount += 1
                self.msg += 'Central error: ' + error + '\n'
                self.msg +=  '***********************\n'
                self.msg +=  result[log][error] + '\n'
                self.msg +=  '***********************\n'
        self.msg += '%s errors found in logs.' % errcount
        self.assertEqual(errcount, 0, msg=self.msg)
