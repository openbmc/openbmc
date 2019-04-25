# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import sys
import signal
import time

from .ssh import OESSHTarget
from oeqa.utils.qemurunner import QemuRunner

supported_fstypes = ['ext3', 'ext4', 'cpio.gz', 'wic']

class OEQemuTarget(OESSHTarget):
    def __init__(self, logger, server_ip, timeout=300, user='root',
            port=None, machine='', rootfs='', kernel='', kvm=False, slirp=False,
            dump_dir='', dump_host_cmds='', display='', bootlog='',
            tmpdir='', dir_image='', boottime=60, **kwargs):

        super(OEQemuTarget, self).__init__(logger, None, server_ip, timeout,
                user, port)

        self.server_ip = server_ip
        self.machine = machine
        self.rootfs = rootfs
        self.kernel = kernel
        self.kvm = kvm
        self.use_slirp = slirp

        self.runner = QemuRunner(machine=machine, rootfs=rootfs, tmpdir=tmpdir,
                                 deploy_dir_image=dir_image, display=display,
                                 logfile=bootlog, boottime=boottime,
                                 use_kvm=kvm, use_slirp=slirp, dump_dir=dump_dir,
                                 dump_host_cmds=dump_host_cmds, logger=logger)

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
            raise RuntimeError("FAILED to start qemu - check the task log and the boot log")

    def stop(self):
        self.runner.stop()
