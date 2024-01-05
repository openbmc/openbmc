#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import sys
import signal
import time
import glob
import subprocess
from collections import defaultdict

from .ssh import OESSHTarget
from oeqa.utils.qemurunner import QemuRunner

supported_fstypes = ['ext3', 'ext4', 'cpio.gz', 'wic']

class OEQemuTarget(OESSHTarget):
    def __init__(self, logger, server_ip, timeout=300, user='root',
            port=None, machine='', rootfs='', kernel='', kvm=False, slirp=False,
            dump_dir='', display='', bootlog='',
            tmpdir='', dir_image='', boottime=60, serial_ports=2,
            boot_patterns = defaultdict(str), ovmf=False, tmpfsdir=None, **kwargs):

        super(OEQemuTarget, self).__init__(logger, None, server_ip, timeout,
                user, port)

        self.server_ip = server_ip
        self.server_port = 0
        self.machine = machine
        self.rootfs = rootfs
        self.kernel = kernel
        self.kvm = kvm
        self.ovmf = ovmf
        self.use_slirp = slirp
        self.boot_patterns = boot_patterns
        self.dump_dir = dump_dir
        self.bootlog = bootlog

        self.runner = QemuRunner(machine=machine, rootfs=rootfs, tmpdir=tmpdir,
                                 deploy_dir_image=dir_image, display=display,
                                 logfile=bootlog, boottime=boottime,
                                 use_kvm=kvm, use_slirp=slirp, dump_dir=dump_dir, logger=logger,
                                 serial_ports=serial_ports, boot_patterns = boot_patterns, 
                                 use_ovmf=ovmf, tmpfsdir=tmpfsdir)

    def start(self, params=None, extra_bootparams=None, runqemuparams=''):
        if self.use_slirp and not self.server_ip:
            self.logger.error("Could not start qemu with slirp without server ip - provide 'TEST_SERVER_IP'")
            raise RuntimeError("FAILED to start qemu - check the task log and the boot log")
        if self.runner.start(params, extra_bootparams=extra_bootparams, runqemuparams=runqemuparams):
            self.ip = self.runner.ip
            if self.use_slirp:
                target_ip_port = self.runner.ip.split(':')
                if len(target_ip_port) == 2:
                    target_ip = target_ip_port[0]
                    port = target_ip_port[1]
                    self.ip = target_ip
                    self.ssh = self.ssh + ['-p', port]
                    self.scp = self.scp + ['-P', port]
                else:
                    self.logger.error("Could not get host machine port to connect qemu with slirp, ssh will not be "
                                      "able to connect to qemu with slirp")
            if self.runner.server_ip:
                self.server_ip = self.runner.server_ip
        else:
            self.stop()
            # Display the first 20 lines of top and
            # last 20 lines of the bootlog when the
            # target is not being booted up.
            topfile = glob.glob(self.dump_dir + "/*_qemu/host_*_top")
            msg = "\n\n===== start: snippet =====\n\n"
            for f in topfile:
                msg += "file: %s\n\n" % f
                with open(f) as tf:
                    for x in range(20):
                        msg += next(tf)
            msg += "\n\n===== end: snippet =====\n\n"
            blcmd = ["tail", "-20", self.bootlog]
            msg += "===== start: snippet =====\n\n"
            try:
                out = subprocess.check_output(blcmd, stderr=subprocess.STDOUT, timeout=1).decode('utf-8')
                msg += "file: %s\n\n" % self.bootlog
                msg += out
            except (subprocess.CalledProcessError, subprocess.TimeoutExpired, FileNotFoundError) as err:
                msg += "Error running command: %s\n%s\n" % (blcmd, err)
            msg += "\n\n===== end: snippet =====\n"

            raise RuntimeError("FAILED to start qemu - check the task log and the boot log %s" % (msg))

    def stop(self):
        self.runner.stop()
