#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# This module is used by testimage.bbclass for setting up and controlling a target machine.

import os
import subprocess
import bb
import logging
from oeqa.utils.sshcontrol import SSHControl
from oeqa.utils.qemurunner import QemuRunner
from oeqa.utils.qemutinyrunner import QemuTinyRunner
from oeqa.utils.dump import TargetDumper
from oeqa.utils.dump import MonitorDumper
from abc import ABCMeta, abstractmethod

class BaseTarget(object, metaclass=ABCMeta):

    supported_image_fstypes = []

    def __init__(self, d, logger):
        self.connection = None
        self.ip = None
        self.server_ip = None
        self.datetime = d.getVar('DATETIME')
        self.testdir = d.getVar("TEST_LOG_DIR")
        self.pn = d.getVar("PN")
        self.logger = logger

    @abstractmethod
    def deploy(self):

        self.sshlog = os.path.join(self.testdir, "ssh_target_log.%s" % self.datetime)
        sshloglink = os.path.join(self.testdir, "ssh_target_log")
        if os.path.islink(sshloglink):
            os.unlink(sshloglink)
        os.symlink(self.sshlog, sshloglink)
        self.logger.info("SSH log file: %s" % self.sshlog)

    @abstractmethod
    def start(self, params=None, ssh=True, extra_bootparams=None):
        pass

    @abstractmethod
    def stop(self):
        pass

    @classmethod
    def get_extra_files(self):
        return None

    @classmethod
    def match_image_fstype(self, d, image_fstypes=None):
        if not image_fstypes:
            image_fstypes = d.getVar('IMAGE_FSTYPES').split(' ')
        possible_image_fstypes = [fstype for fstype in self.supported_image_fstypes if fstype in image_fstypes]
        if possible_image_fstypes:
            return possible_image_fstypes[0]
        else:
            return None

    def get_image_fstype(self, d):
        image_fstype = self.match_image_fstype(d)
        if image_fstype:
            return image_fstype
        else:
            bb.fatal("IMAGE_FSTYPES should contain a Target Controller supported image fstype: %s " % ', '.join(map(str, self.supported_image_fstypes)))

    def restart(self, params=None):
        self.stop()
        self.start(params)

    def run(self, cmd, timeout=None):
        return self.connection.run(cmd, timeout)

    def copy_to(self, localpath, remotepath):
        return self.connection.copy_to(localpath, remotepath)

    def copy_from(self, remotepath, localpath):
        return self.connection.copy_from(remotepath, localpath)



class QemuTarget(BaseTarget):

    supported_image_fstypes = ['ext3', 'ext4', 'cpio.gz', 'wic']

    def __init__(self, d, logger, image_fstype=None, boot_patterns=None):

        import oe.types

        super(QemuTarget, self).__init__(d, logger)

        self.rootfs = ''
        self.kernel = ''
        self.image_fstype = ''

        if d.getVar('FIND_ROOTFS') == '1':
            self.image_fstype = image_fstype or self.get_image_fstype(d)
            self.rootfs = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"),  d.getVar("IMAGE_LINK_NAME") + '.' + self.image_fstype)
            self.kernel = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"), d.getVar("KERNEL_IMAGETYPE", False) + '-' + d.getVar('MACHINE', False) + '.bin')
        self.qemulog = os.path.join(self.testdir, "qemu_boot_log.%s" % self.datetime)
        dump_monitor_cmds = d.getVar("testimage_dump_monitor")
        dump_dir = d.getVar("TESTIMAGE_DUMP_DIR")
        if not dump_dir:
            dump_dir = os.path.join(d.getVar('LOG_DIR'), 'runtime-hostdump')
        use_kvm = oe.types.qemu_use_kvm(d.getVar('QEMU_USE_KVM'), d.getVar('TARGET_ARCH'))

        # Log QemuRunner log output to a file
        import oe.path
        bb.utils.mkdirhier(self.testdir)
        self.qemurunnerlog = os.path.join(self.testdir, 'qemurunner_log.%s' % self.datetime)
        self.loggerhandler = logging.FileHandler(self.qemurunnerlog)
        self.loggerhandler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
        self.logger.addHandler(self.loggerhandler)
        oe.path.symlink(os.path.basename(self.qemurunnerlog), os.path.join(self.testdir, 'qemurunner_log'), force=True)

        if d.getVar("DISTRO") == "poky-tiny":
            self.runner = QemuTinyRunner(machine=d.getVar("MACHINE"),
                            rootfs=self.rootfs,
                            tmpdir = d.getVar("TMPDIR"),
                            deploy_dir_image = d.getVar("DEPLOY_DIR_IMAGE"),
                            display = d.getVar("BB_ORIGENV", False).getVar("DISPLAY"),
                            logfile = self.qemulog,
                            kernel = self.kernel,
                            boottime = int(d.getVar("TEST_QEMUBOOT_TIMEOUT")),
                            tmpfsdir = d.getVar("RUNQEMU_TMPFS_DIR"),
                            logger = logger)
        else:
            self.runner = QemuRunner(machine=d.getVar("MACHINE"),
                            rootfs=self.rootfs,
                            tmpdir = d.getVar("TMPDIR"),
                            deploy_dir_image = d.getVar("DEPLOY_DIR_IMAGE"),
                            display = d.getVar("BB_ORIGENV", False).getVar("DISPLAY"),
                            logfile = self.qemulog,
                            boottime = int(d.getVar("TEST_QEMUBOOT_TIMEOUT")),
                            use_kvm = use_kvm,
                            dump_dir = dump_dir,
                            logger = logger,
                            tmpfsdir = d.getVar("RUNQEMU_TMPFS_DIR"),
                            serial_ports = len(d.getVar("SERIAL_CONSOLES").split()),
                            boot_patterns = boot_patterns)

        self.monitor_dumper = MonitorDumper(dump_monitor_cmds, dump_dir, self.runner)
        if (self.monitor_dumper):
            self.monitor_dumper.create_dir("qmp")

    def deploy(self):
        bb.utils.mkdirhier(self.testdir)

        qemuloglink = os.path.join(self.testdir, "qemu_boot_log")
        if os.path.islink(qemuloglink):
            os.unlink(qemuloglink)
        os.symlink(self.qemulog, qemuloglink)

        self.logger.info("rootfs file: %s" % self.rootfs)
        self.logger.info("Qemu log file: %s" % self.qemulog)
        super(QemuTarget, self).deploy()

    def start(self, params=None, ssh=True, extra_bootparams='', runqemuparams='', launch_cmd='', discard_writes=True):
        if launch_cmd:
            start = self.runner.launch(get_ip=ssh, launch_cmd=launch_cmd, qemuparams=params)
        else:
            start = self.runner.start(params, get_ip=ssh, extra_bootparams=extra_bootparams, runqemuparams=runqemuparams, discard_writes=discard_writes)

        if start:
            if ssh:
                self.ip = self.runner.ip
                self.server_ip = self.runner.server_ip
                self.connection = SSHControl(ip=self.ip, logfile=self.sshlog)
        else:
            self.stop()
            if os.path.exists(self.qemulog):
                with open(self.qemulog, 'r') as f:
                    bb.error("Qemu log output from %s:\n%s" % (self.qemulog, f.read()))
            raise RuntimeError("%s - FAILED to start qemu - check the task log and the boot log" % self.pn)

    def check(self):
        return self.runner.is_alive()

    def stop(self):
        try:
            self.runner.stop()
        except:
            pass
        self.logger.removeHandler(self.loggerhandler)
        self.loggerhandler.close()
        self.connection = None
        self.ip = None
        self.server_ip = None

    def restart(self, params=None):
        if self.runner.restart(params):
            self.ip = self.runner.ip
            self.server_ip = self.runner.server_ip
            self.connection = SSHControl(ip=self.ip, logfile=self.sshlog)
        else:
            raise RuntimeError("%s - FAILED to re-start qemu - check the task log and the boot log" % self.pn)

    def run_serial(self, command, timeout=60):
        return self.runner.run_serial(command, timeout=timeout)


class SimpleRemoteTarget(BaseTarget):

    def __init__(self, d):
        super(SimpleRemoteTarget, self).__init__(d)
        addr = d.getVar("TEST_TARGET_IP") or bb.fatal('Please set TEST_TARGET_IP with the IP address of the machine you want to run the tests on.')
        self.ip = addr.split(":")[0]
        try:
            self.port = addr.split(":")[1]
        except IndexError:
            self.port = None
        self.logger.info("Target IP: %s" % self.ip)
        self.server_ip = d.getVar("TEST_SERVER_IP")
        if not self.server_ip:
            try:
                self.server_ip = subprocess.check_output(['ip', 'route', 'get', self.ip ]).split("\n")[0].split()[-1]
            except Exception as e:
                bb.fatal("Failed to determine the host IP address (alternatively you can set TEST_SERVER_IP with the IP address of this machine): %s" % e)
        self.logger.info("Server IP: %s" % self.server_ip)

    def deploy(self):
        super(SimpleRemoteTarget, self).deploy()

    def start(self, params=None, ssh=True, extra_bootparams=None):
        if ssh:
            self.connection = SSHControl(self.ip, logfile=self.sshlog, port=self.port)

    def stop(self):
        self.connection = None
        self.ip = None
        self.server_ip = None
