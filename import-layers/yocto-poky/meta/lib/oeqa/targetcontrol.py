# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module is used by testimage.bbclass for setting up and controlling a target machine.

import os
import shutil
import subprocess
import bb
import traceback
import sys
import logging
from oeqa.utils.sshcontrol import SSHControl
from oeqa.utils.qemurunner import QemuRunner
from oeqa.utils.qemutinyrunner import QemuTinyRunner
from oeqa.utils.dump import TargetDumper
from oeqa.controllers.testtargetloader import TestTargetLoader
from abc import ABCMeta, abstractmethod

def get_target_controller(d):
    testtarget = d.getVar("TEST_TARGET", True)
    # old, simple names
    if testtarget == "qemu":
        return QemuTarget(d)
    elif testtarget == "simpleremote":
        return SimpleRemoteTarget(d)
    else:
        # use the class name
        try:
            # is it a core class defined here?
            controller = getattr(sys.modules[__name__], testtarget)
        except AttributeError:
            # nope, perhaps a layer defined one
            try:
                bbpath = d.getVar("BBPATH", True).split(':')
                testtargetloader = TestTargetLoader()
                controller = testtargetloader.get_controller_module(testtarget, bbpath)
            except ImportError as e:
                bb.fatal("Failed to import {0} from available controller modules:\n{1}".format(testtarget,traceback.format_exc()))
            except AttributeError as e:
                bb.fatal("Invalid TEST_TARGET - " + str(e))
        return controller(d)


class BaseTarget(object):

    __metaclass__ = ABCMeta

    supported_image_fstypes = []

    def __init__(self, d):
        self.connection = None
        self.ip = None
        self.server_ip = None
        self.datetime = d.getVar('DATETIME', True)
        self.testdir = d.getVar("TEST_LOG_DIR", True)
        self.pn = d.getVar("PN", True)

    @abstractmethod
    def deploy(self):

        self.sshlog = os.path.join(self.testdir, "ssh_target_log.%s" % self.datetime)
        sshloglink = os.path.join(self.testdir, "ssh_target_log")
        if os.path.islink(sshloglink):
            os.unlink(sshloglink)
        os.symlink(self.sshlog, sshloglink)
        bb.note("SSH log file: %s" %  self.sshlog)

    @abstractmethod
    def start(self, params=None, ssh=True):
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
            image_fstypes = d.getVar('IMAGE_FSTYPES', True).split(' ')
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

    def __init__(self, d):

        super(QemuTarget, self).__init__(d)

        self.image_fstype = self.get_image_fstype(d)
        self.qemulog = os.path.join(self.testdir, "qemu_boot_log.%s" % self.datetime)
        self.origrootfs = os.path.join(d.getVar("DEPLOY_DIR_IMAGE", True),  d.getVar("IMAGE_LINK_NAME", True) + '.' + self.image_fstype)
        self.rootfs = os.path.join(self.testdir, d.getVar("IMAGE_LINK_NAME", True) + '-testimage.' + self.image_fstype)
        self.kernel = os.path.join(d.getVar("DEPLOY_DIR_IMAGE", True), d.getVar("KERNEL_IMAGETYPE", False) + '-' + d.getVar('MACHINE', False) + '.bin')
        dump_target_cmds = d.getVar("testimage_dump_target", True)
        dump_host_cmds = d.getVar("testimage_dump_host", True)
        dump_dir = d.getVar("TESTIMAGE_DUMP_DIR", True)

        # Log QemuRunner log output to a file
        import oe.path
        bb.utils.mkdirhier(self.testdir)
        self.qemurunnerlog = os.path.join(self.testdir, 'qemurunner_log.%s' % self.datetime)
        logger = logging.getLogger('BitBake.QemuRunner')
        loggerhandler = logging.FileHandler(self.qemurunnerlog)
        loggerhandler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
        logger.addHandler(loggerhandler)
        oe.path.symlink(os.path.basename(self.qemurunnerlog), os.path.join(self.testdir, 'qemurunner_log'), force=True)

        if d.getVar("DISTRO", True) == "poky-tiny":
            self.runner = QemuTinyRunner(machine=d.getVar("MACHINE", True),
                            rootfs=self.rootfs,
                            tmpdir = d.getVar("TMPDIR", True),
                            deploy_dir_image = d.getVar("DEPLOY_DIR_IMAGE", True),
                            display = d.getVar("BB_ORIGENV", False).getVar("DISPLAY", True),
                            logfile = self.qemulog,
                            kernel = self.kernel,
                            boottime = int(d.getVar("TEST_QEMUBOOT_TIMEOUT", True)))
        else:
            self.runner = QemuRunner(machine=d.getVar("MACHINE", True),
                            rootfs=self.rootfs,
                            tmpdir = d.getVar("TMPDIR", True),
                            deploy_dir_image = d.getVar("DEPLOY_DIR_IMAGE", True),
                            display = d.getVar("BB_ORIGENV", False).getVar("DISPLAY", True),
                            logfile = self.qemulog,
                            boottime = int(d.getVar("TEST_QEMUBOOT_TIMEOUT", True)),
                            dump_dir = dump_dir,
                            dump_host_cmds = d.getVar("testimage_dump_host", True))

        self.target_dumper = TargetDumper(dump_target_cmds, dump_dir, self.runner)

    def deploy(self):
        try:
            bb.utils.mkdirhier(self.testdir)
            shutil.copyfile(self.origrootfs, self.rootfs)
        except Exception as e:
            bb.fatal("Error copying rootfs: %s" % e)

        qemuloglink = os.path.join(self.testdir, "qemu_boot_log")
        if os.path.islink(qemuloglink):
            os.unlink(qemuloglink)
        os.symlink(self.qemulog, qemuloglink)

        bb.note("rootfs file: %s" %  self.rootfs)
        bb.note("Qemu log file: %s" % self.qemulog)
        super(QemuTarget, self).deploy()

    def start(self, params=None, ssh=True):
        if self.runner.start(params, get_ip=ssh):
            if ssh:
                self.ip = self.runner.ip
                self.server_ip = self.runner.server_ip
                self.connection = SSHControl(ip=self.ip, logfile=self.sshlog)
        else:
            self.stop()
            if os.path.exists(self.qemulog):
                with open(self.qemulog, 'r') as f:
                    bb.error("Qemu log output from %s:\n%s" % (self.qemulog, f.read()))
            raise bb.build.FuncFailed("%s - FAILED to start qemu - check the task log and the boot log" % self.pn)

    def check(self):
        return self.runner.is_alive()

    def stop(self):
        self.runner.stop()
        self.connection = None
        self.ip = None
        self.server_ip = None

    def restart(self, params=None):
        if self.runner.restart(params):
            self.ip = self.runner.ip
            self.server_ip = self.runner.server_ip
            self.connection = SSHControl(ip=self.ip, logfile=self.sshlog)
        else:
            raise bb.build.FuncFailed("%s - FAILED to re-start qemu - check the task log and the boot log" % self.pn)

    def run_serial(self, command):
        return self.runner.run_serial(command)


class SimpleRemoteTarget(BaseTarget):

    def __init__(self, d):
        super(SimpleRemoteTarget, self).__init__(d)
        addr = d.getVar("TEST_TARGET_IP", True) or bb.fatal('Please set TEST_TARGET_IP with the IP address of the machine you want to run the tests on.')
        self.ip = addr.split(":")[0]
        try:
            self.port = addr.split(":")[1]
        except IndexError:
            self.port = None
        bb.note("Target IP: %s" % self.ip)
        self.server_ip = d.getVar("TEST_SERVER_IP", True)
        if not self.server_ip:
            try:
                self.server_ip = subprocess.check_output(['ip', 'route', 'get', self.ip ]).split("\n")[0].split()[-1]
            except Exception as e:
                bb.fatal("Failed to determine the host IP address (alternatively you can set TEST_SERVER_IP with the IP address of this machine): %s" % e)
        bb.note("Server IP: %s" % self.server_ip)

    def deploy(self):
        super(SimpleRemoteTarget, self).deploy()

    def start(self, params=None, ssh=True):
        if ssh:
            self.connection = SSHControl(self.ip, logfile=self.sshlog, port=self.port)

    def stop(self):
        self.connection = None
        self.ip = None
        self.server_ip = None
