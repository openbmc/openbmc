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
            port=None, machine='', rootfs='', kernel='', kvm=False,
            dump_dir='', dump_host_cmds='', display='', bootlog='',
            tmpdir='', dir_image='', boottime=60, **kwargs):

        super(OEQemuTarget, self).__init__(logger, None, server_ip, timeout,
                user, port)

        self.server_ip = server_ip
        self.machine = machine
        self.rootfs = rootfs
        self.kernel = kernel
        self.kvm = kvm

        self.runner = QemuRunner(machine=machine, rootfs=rootfs, tmpdir=tmpdir,
                                 deploy_dir_image=dir_image, display=display,
                                 logfile=bootlog, boottime=boottime,
                                 use_kvm=kvm, dump_dir=dump_dir,
                                 dump_host_cmds=dump_host_cmds, logger=logger)

    def start(self, params=None, extra_bootparams=None):
        if self.runner.start(params, extra_bootparams=extra_bootparams):
            self.ip = self.runner.ip
            self.server_ip = self.runner.server_ip
        else:
            self.stop()
            raise RuntimeError("FAILED to start qemu - check the task log and the boot log")

    def stop(self):
        self.runner.stop()
