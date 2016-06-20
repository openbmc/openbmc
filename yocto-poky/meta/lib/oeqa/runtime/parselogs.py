import os
import unittest
import subprocess
from oeqa.oetest import oeRuntimeTest
from oeqa.utils.decorators import *

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
    ]

video_related = [
    "uvesafb",
]

x86_common = [
    '[drm:psb_do_init] *ERROR* Debug is',
    'wrong ELF class',
    'Could not enable PowerButton event',
    'probe of LNXPWRBN:00 failed with error -22',
    'pmd_set_huge: Cannot satisfy',
] + common_errors

qemux86_common = [
    'wrong ELF class',
    "fail to add MMCONFIG information, can't access extended PCI configuration space under this bridge.",
    "can't claim BAR ",
] + common_errors

ignore_errors = { 
    'default' : common_errors,
    'qemux86' : [
        'Failed to access perfctr msr (MSR',
        ] + qemux86_common,
    'qemux86-64' : qemux86_common,
    'qemumips' : [
        'Failed to load module "glx"',
        'pci 0000:00:00.0: [Firmware Bug]: reg 0x..: invalid BAR (can\'t size)',
        ] + common_errors,
    'qemumips64' : [
        'pci 0000:00:00.0: [Firmware Bug]: reg 0x..: invalid BAR (can\'t size)',
         ] + common_errors,
    'qemuppc' : [
        'PCI 0000:00 Cannot reserve Legacy IO [io  0x0000-0x0fff]',
        'host side 80-wire cable detection failed, limiting max speed',
        'mode "640x480" test failed',
        'Failed to load module "glx"',
        ] + common_errors,
    'qemuarm' : [
        'mmci-pl18x: probe of fpga:05 failed with error -22',
        'mmci-pl18x: probe of fpga:0b failed with error -22',
        'Failed to load module "glx"'
        ] + common_errors,
    'qemuarm64' : [
        'Fatal server error:',
        '(EE) Server terminated with error (1). Closing log file.',
        'dmi: Firmware registration failed.',
        ] + common_errors,
    'emenlow' : [
        '[Firmware Bug]: ACPI: No _BQC method, cannot determine initial brightness',
        '(EE) Failed to load module "psb"',
        '(EE) Failed to load module psb',
        '(EE) Failed to load module "psbdrv"',
        '(EE) Failed to load module psbdrv',
        '(EE) open /dev/fb0: No such file or directory',
        '(EE) AIGLX: reverting to software rendering',
        ] + x86_common,
    'intel-core2-32' : [
        'ACPI: No _BQC method, cannot determine initial brightness',
        '[Firmware Bug]: ACPI: No _BQC method, cannot determine initial brightness',
        '(EE) Failed to load module "psb"',
        '(EE) Failed to load module psb',
        '(EE) Failed to load module "psbdrv"',
        '(EE) Failed to load module psbdrv',
        '(EE) open /dev/fb0: No such file or directory',
        '(EE) AIGLX: reverting to software rendering',
        ] + x86_common,
    'intel-corei7-64' : x86_common,
    'crownbay' : x86_common,
    'genericx86' : x86_common,
    'genericx86-64' : x86_common,
    'edgerouter' : [
        'Fatal server error:',
        ] + common_errors,
    'jasperforest' : [
        'Activated service \'org.bluez\' failed:',
        'Unable to find NFC netlink family',
        ] + common_errors,
}

log_locations = ["/var/log/","/var/log/dmesg", "/tmp/dmesg_output.log"]

class ParseLogsTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        self.errors = errors

        # When systemd is enabled we need to notice errors on
        # circular dependencies in units.
        if self.hasFeature("systemd"):
            self.errors.extend([
                'Found ordering cycle on',
                'Breaking ordering cycle by deleting job',
                'deleted to break ordering cycle',
                'Ordering cycle found, skipping',
                ])

        self.ignore_errors = ignore_errors
        self.log_locations = log_locations
        self.msg = ""
        (is_lsb, location) = oeRuntimeTest.tc.target.run("which LSB_Test.sh")
        if is_lsb == 0:
            for machine in self.ignore_errors:
                self.ignore_errors[machine] = self.ignore_errors[machine] + video_related

    def getMachine(self):
        return oeRuntimeTest.tc.d.getVar("MACHINE", True)

    #get some information on the CPU of the machine to display at the beginning of the output. This info might be useful in some cases.
    def getHardwareInfo(self):
        hwi = ""
        (status, cpu_name) = self.target.run("cat /proc/cpuinfo | grep \"model name\" | head -n1 | awk 'BEGIN{FS=\":\"}{print $2}'")
        (status, cpu_physical_cores) = self.target.run("cat /proc/cpuinfo | grep \"cpu cores\" | head -n1 | awk {'print $4'}")
        (status, cpu_logical_cores) = self.target.run("cat /proc/cpuinfo | grep \"processor\" | wc -l")
        (status, cpu_arch) = self.target.run("uname -m")
        hwi += "Machine information: \n"
        hwi += "*******************************\n"
        hwi += "Machine name: "+self.getMachine()+"\n"
        hwi += "CPU: "+str(cpu_name)+"\n"
        hwi += "Arch: "+str(cpu_arch)+"\n"
        hwi += "Physical cores: "+str(cpu_physical_cores)+"\n"
        hwi += "Logical cores: "+str(cpu_logical_cores)+"\n"
        hwi += "*******************************\n"
        return hwi

    #go through the log locations provided and if it's a folder create a list with all the .log files in it, if it's a file just add 
    #it to that list
    def getLogList(self, log_locations):
        logs = []
        for location in log_locations:
            (status, output) = self.target.run("test -f "+str(location))
            if (status == 0):
                logs.append(str(location))
            else:
                (status, output) = self.target.run("test -d "+str(location))
                if (status == 0):
                    (status, output) = self.target.run("find "+str(location)+"/*.log -maxdepth 1 -type f")
                    if (status == 0):
                        output = output.splitlines()
                        for logfile in output:
                            logs.append(os.path.join(location,str(logfile)))
        return logs

    #copy the log files to be parsed locally
    def transfer_logs(self, log_list):
        target_logs = 'target_logs'
        if not os.path.exists(target_logs):
            os.makedirs(target_logs)
        for f in log_list:
            self.target.copy_from(f, target_logs)

    #get the local list of logs
    def get_local_log_list(self, log_locations):
        self.transfer_logs(self.getLogList(log_locations))
        logs = [ os.path.join('target_logs',f) for f in os.listdir('target_logs') if os.path.isfile(os.path.join('target_logs',f)) ]
        return logs

    #build the grep command to be used with filters and exclusions
    def build_grepcmd(self, errors, ignore_errors, log):
        grepcmd = "grep "
        grepcmd +="-Ei \""
        for error in errors:
            grepcmd += error+"|"
        grepcmd = grepcmd[:-1]
        grepcmd += "\" "+str(log)+" | grep -Eiv \'"
        try:
            errorlist = ignore_errors[self.getMachine()]
        except KeyError:
            self.msg += "No ignore list found for this machine, using default\n"
            errorlist = ignore_errors['default']
        for ignore_error in errorlist:
            ignore_error = ignore_error.replace("(", "\(")
            ignore_error = ignore_error.replace(")", "\)")
            ignore_error = ignore_error.replace("'", ".")
            ignore_error = ignore_error.replace("?", "\?")
            ignore_error = ignore_error.replace("[", "\[")
            ignore_error = ignore_error.replace("]", "\]")
            ignore_error = ignore_error.replace("*", "\*")
            ignore_error = ignore_error.replace("0-9", "[0-9]")
            grepcmd += ignore_error+"|"
        grepcmd = grepcmd[:-1]
        grepcmd += "\'"
        return grepcmd

    #grep only the errors so that their context could be collected. Default context is 10 lines before and after the error itself
    def parse_logs(self, errors, ignore_errors, logs, lines_before = 10, lines_after = 10):
        results = {}
        rez = []
        grep_output = ''
        for log in logs:
            result = None
            thegrep = self.build_grepcmd(errors, ignore_errors, log)
            try:
                result = subprocess.check_output(thegrep, shell=True)
            except:
                pass
            if (result is not None):
                results[log.replace('target_logs/','')] = {}
                rez = result.splitlines()
                for xrez in rez:
                    try:
                        grep_output = subprocess.check_output(['grep', '-F', xrez, '-B', str(lines_before), '-A', str(lines_after), log])
                    except:
                        pass
                    results[log.replace('target_logs/','')][xrez]=grep_output
        return results

    #get the output of dmesg and write it in a file. This file is added to log_locations.
    def write_dmesg(self):
        (status, dmesg) = self.target.run("dmesg > /tmp/dmesg_output.log")

    @testcase(1059)
    @skipUnlessPassed('test_ssh')
    def test_parselogs(self):
        self.write_dmesg()
        log_list = self.get_local_log_list(self.log_locations)
        result = self.parse_logs(self.errors, self.ignore_errors, log_list)
        print self.getHardwareInfo()
        errcount = 0
        for log in result:
            self.msg += "Log: "+log+"\n"
            self.msg += "-----------------------\n"
            for error in result[log]:
                errcount += 1
                self.msg += "Central error: "+str(error)+"\n"
                self.msg +=  "***********************\n"
                self.msg +=  result[str(log)][str(error)]+"\n"
                self.msg +=  "***********************\n"
        self.msg += "%s errors found in logs." % errcount
        self.assertEqual(errcount, 0, msg=self.msg)
