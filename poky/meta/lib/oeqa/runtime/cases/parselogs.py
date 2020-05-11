#
# SPDX-License-Identifier: MIT
#

import os

from subprocess import check_output
from shutil import rmtree
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage

#in the future these lists could be moved outside of module
errors = ["error", "cannot", "can\'t", "failed"]

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
    "[drm] Cannot find any crtc or sizes - going 1024x768",
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
    "[pulseaudio] authkey.c: Failed to open cookie file",
    "[pulseaudio] authkey.c: Failed to load authentication key",
    ]

video_related = [
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
] + common_errors

ignore_errors = {
    'default' : common_errors,
    'qemux86' : [
        'Failed to access perfctr msr (MSR',
        'pci 0000:00:00.0: [Firmware Bug]: reg 0x..: invalid BAR (can\'t size)',
        ] + qemux86_common,
    'qemux86-64' : qemux86_common,
    'qemumips' : [
        'Failed to load module "glx"',
        'pci 0000:00:00.0: [Firmware Bug]: reg 0x..: invalid BAR (can\'t size)',
        'cacheinfo: Failed to find cpu0 device node',
        ] + common_errors,
    'qemumips64' : [
        'pci 0000:00:00.0: [Firmware Bug]: reg 0x..: invalid BAR (can\'t size)',
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
    'qemuarm' : [
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
    'edgerouter' : [
        'not creating \'/sys/firmware/fdt\'',
        'Failed to find cpu0 device node',
        'Fatal server error:',
        'Server terminated with error',
        ] + common_errors,
    'beaglebone-yocto' : [
        'Direct firmware load for regulatory.db',
        'failed to load regulatory.db',
        'l4_wkup_cm',
        'Failed to load module "glx"',
        'Failed to make EGL context current',
        'glamor initialization failed',
        ] + common_errors,
}

log_locations = ["/var/log/","/var/log/dmesg", "/tmp/dmesg_output.log"]

class ParseLogsTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        cls.errors = errors

        # When systemd is enabled we need to notice errors on
        # circular dependencies in units.
        if 'systemd' in cls.td.get('DISTRO_FEATURES', ''):
            cls.errors.extend([
                'Found ordering cycle on',
                'Breaking ordering cycle by deleting job',
                'deleted to break ordering cycle',
                'Ordering cycle found, skipping',
                ])

        cls.ignore_errors = ignore_errors
        cls.log_locations = log_locations
        cls.msg = ''
        is_lsb, _ = cls.tc.target.run("which LSB_Test.sh")
        if is_lsb == 0:
            for machine in cls.ignore_errors:
                cls.ignore_errors[machine] = cls.ignore_errors[machine] \
                                             + video_related

    def getMachine(self):
        return self.td.get('MACHINE', '')

    def getWorkdir(self):
        return self.td.get('WORKDIR', '')

    # Get some information on the CPU of the machine to display at the
    # beginning of the output. This info might be useful in some cases.
    def getHardwareInfo(self):
        hwi = ""
        cmd = ('cat /proc/cpuinfo | grep "model name" | head -n1 | '
               " awk 'BEGIN{FS=\":\"}{print $2}'")
        _, cpu_name = self.target.run(cmd)

        cmd = ('cat /proc/cpuinfo | grep "cpu cores" | head -n1 | '
               "awk {'print $4'}")
        _, cpu_physical_cores = self.target.run(cmd)

        cmd = 'cat /proc/cpuinfo | grep "processor" | wc -l'
        _, cpu_logical_cores = self.target.run(cmd)

        _, cpu_arch = self.target.run('uname -m')

        hwi += 'Machine information: \n'
        hwi += '*******************************\n'
        hwi += 'Machine name: ' + self.getMachine() + '\n'
        hwi += 'CPU: ' + str(cpu_name) + '\n'
        hwi += 'Arch: ' + str(cpu_arch)+ '\n'
        hwi += 'Physical cores: ' + str(cpu_physical_cores) + '\n'
        hwi += 'Logical cores: ' + str(cpu_logical_cores) + '\n'
        hwi += '*******************************\n'

        return hwi

    # Go through the log locations provided and if it's a folder
    # create a list with all the .log files in it, if it's a file
    # just add it to that list.
    def getLogList(self, log_locations):
        logs = []
        for location in log_locations:
            status, _ = self.target.run('test -f ' + str(location))
            if status == 0:
                logs.append(str(location))
            else:
                status, _ = self.target.run('test -d ' + str(location))
                if status == 0:
                    cmd = 'find ' + str(location) + '/*.log -maxdepth 1 -type f'
                    status, output = self.target.run(cmd)
                    if status == 0:
                        output = output.splitlines()
                        for logfile in output:
                            logs.append(os.path.join(location, str(logfile)))
        return logs

    # Copy the log files to be parsed locally
    def transfer_logs(self, log_list):
        workdir = self.getWorkdir()
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

    # Build the grep command to be used with filters and exclusions
    def build_grepcmd(self, errors, ignore_errors, log):
        grepcmd = 'grep '
        grepcmd += '-Ei "'
        for error in errors:
            grepcmd += '\<' + error + '\>' + '|'
        grepcmd = grepcmd[:-1]
        grepcmd += '" ' + str(log) + " | grep -Eiv \'"

        try:
            errorlist = ignore_errors[self.getMachine()]
        except KeyError:
            self.msg += 'No ignore list found for this machine, using default\n'
            errorlist = ignore_errors['default']

        for ignore_error in errorlist:
            ignore_error = ignore_error.replace('(', '\(')
            ignore_error = ignore_error.replace(')', '\)')
            ignore_error = ignore_error.replace("'", '.')
            ignore_error = ignore_error.replace('?', '\?')
            ignore_error = ignore_error.replace('[', '\[')
            ignore_error = ignore_error.replace(']', '\]')
            ignore_error = ignore_error.replace('*', '\*')
            ignore_error = ignore_error.replace('0-9', '[0-9]')
            grepcmd += ignore_error + '|'
        grepcmd = grepcmd[:-1]
        grepcmd += "\'"

        return grepcmd

    # Grep only the errors so that their context could be collected.
    # Default context is 10 lines before and after the error itself
    def parse_logs(self, errors, ignore_errors, logs,
                   lines_before = 10, lines_after = 10):
        results = {}
        rez = []
        grep_output = ''

        for log in logs:
            result = None
            thegrep = self.build_grepcmd(errors, ignore_errors, log)

            try:
                result = check_output(thegrep, shell=True).decode('utf-8')
            except:
                pass

            if result is not None:
                results[log] = {}
                rez = result.splitlines()

                for xrez in rez:
                    try:
                        cmd = ['grep', '-F', xrez, '-B', str(lines_before)]
                        cmd += ['-A', str(lines_after), log]
                        grep_output = check_output(cmd).decode('utf-8')
                    except:
                        pass
                    results[log][xrez]=grep_output

        return results

    # Get the output of dmesg and write it in a file.
    # This file is added to log_locations.
    def write_dmesg(self):
        (status, dmesg) = self.target.run('dmesg > /tmp/dmesg_output.log')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_parselogs(self):
        self.write_dmesg()
        log_list = self.get_local_log_list(self.log_locations)
        result = self.parse_logs(self.errors, self.ignore_errors, log_list)
        print(self.getHardwareInfo())
        errcount = 0
        for log in result:
            self.msg += 'Log: ' + log + '\n'
            self.msg += '-----------------------\n'
            for error in result[log]:
                errcount += 1
                self.msg += 'Central error: ' + str(error) + '\n'
                self.msg +=  '***********************\n'
                self.msg +=  result[str(log)][str(error)] + '\n'
                self.msg +=  '***********************\n'
        self.msg += '%s errors found in logs.' % errcount
        self.assertEqual(errcount, 0, msg=self.msg)
