# Copyright (C) 2014 Intel Corporation
#
# SPDX-License-Identifier: MIT
#
# This module adds support to testimage.bbclass to deploy images and run
# tests using a "master image" - this is a "known good" image that is
# installed onto the device as part of initial setup and will be booted into
# with no interaction; we can then use it to deploy the image to be tested
# to a second partition before running the tests.
#
# For an example master image, see core-image-testmaster
# (meta/recipes-extended/images/core-image-testmaster.bb)

import os
import bb
import traceback
import time
import subprocess

import oeqa.targetcontrol
import oeqa.utils.sshcontrol as sshcontrol
import oeqa.utils.commands as commands
from oeqa.utils import CommandError

from abc import ABCMeta, abstractmethod

class MasterImageHardwareTarget(oeqa.targetcontrol.BaseTarget, metaclass=ABCMeta):

    supported_image_fstypes = ['tar.gz', 'tar.bz2']

    def __init__(self, d):
        super(MasterImageHardwareTarget, self).__init__(d)

        # target ip
        addr = d.getVar("TEST_TARGET_IP") or bb.fatal('Please set TEST_TARGET_IP with the IP address of the machine you want to run the tests on.')
        self.ip = addr.split(":")[0]
        try:
            self.port = addr.split(":")[1]
        except IndexError:
            self.port = None
        bb.note("Target IP: %s" % self.ip)
        self.server_ip = d.getVar("TEST_SERVER_IP")
        if not self.server_ip:
            try:
                self.server_ip = subprocess.check_output(['ip', 'route', 'get', self.ip ]).split("\n")[0].split()[-1]
            except Exception as e:
                bb.fatal("Failed to determine the host IP address (alternatively you can set TEST_SERVER_IP with the IP address of this machine): %s" % e)
        bb.note("Server IP: %s" % self.server_ip)

        # test rootfs + kernel
        self.image_fstype = self.get_image_fstype(d)
        self.rootfs = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"), d.getVar("IMAGE_LINK_NAME") + '.' + self.image_fstype)
        self.kernel = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"), d.getVar("KERNEL_IMAGETYPE", False) + '-' + d.getVar('MACHINE', False) + '.bin')
        if not os.path.isfile(self.rootfs):
            # we could've checked that IMAGE_FSTYPES contains tar.gz but the config for running testimage might not be
            # the same as the config with which the image was build, ie
            # you bitbake core-image-sato with IMAGE_FSTYPES += "tar.gz"
            # and your autobuilder overwrites the config, adds the test bits and runs bitbake core-image-sato -c testimage
            bb.fatal("No rootfs found. Did you build the image ?\nIf yes, did you build it with IMAGE_FSTYPES += \"tar.gz\" ? \
                      \nExpected path: %s" % self.rootfs)
        if not os.path.isfile(self.kernel):
            bb.fatal("No kernel found. Expected path: %s" % self.kernel)

        # master ssh connection
        self.master = None
        # if the user knows what they are doing, then by all means...
        self.user_cmds = d.getVar("TEST_DEPLOY_CMDS")
        self.deploy_cmds = None

        # this is the name of the command that controls the power for a board
        # e.g: TEST_POWERCONTROL_CMD = "/home/user/myscripts/powercontrol.py ${MACHINE} what-ever-other-args-the-script-wants"
        # the command should take as the last argument "off" and "on" and "cycle" (off, on)
        self.powercontrol_cmd = d.getVar("TEST_POWERCONTROL_CMD") or None
        self.powercontrol_args = d.getVar("TEST_POWERCONTROL_EXTRA_ARGS", False) or ""

        self.serialcontrol_cmd = d.getVar("TEST_SERIALCONTROL_CMD") or None
        self.serialcontrol_args = d.getVar("TEST_SERIALCONTROL_EXTRA_ARGS", False) or ""

        self.origenv = os.environ
        if self.powercontrol_cmd or self.serialcontrol_cmd:
            # the external script for controlling power might use ssh
            # ssh + keys means we need the original user env
            bborigenv = d.getVar("BB_ORIGENV", False) or {}
            for key in bborigenv:
                val = bborigenv.getVar(key)
                if val is not None:
                    self.origenv[key] = str(val)

        if self.powercontrol_cmd:
            if self.powercontrol_args:
                self.powercontrol_cmd = "%s %s" % (self.powercontrol_cmd, self.powercontrol_args)
        if self.serialcontrol_cmd:
            if self.serialcontrol_args:
                self.serialcontrol_cmd = "%s %s" % (self.serialcontrol_cmd, self.serialcontrol_args)

    def power_ctl(self, msg):
        if self.powercontrol_cmd:
            cmd = "%s %s" % (self.powercontrol_cmd, msg)
            try:
                commands.runCmd(cmd, assert_error=False, start_new_session=True, env=self.origenv)
            except CommandError as e:
                bb.fatal(str(e))

    def power_cycle(self, conn):
        if self.powercontrol_cmd:
            # be nice, don't just cut power
            conn.run("shutdown -h now")
            time.sleep(10)
            self.power_ctl("cycle")
        else:
            status, output = conn.run("sync; { sleep 1; reboot; } > /dev/null &")
            if status != 0:
                bb.error("Failed rebooting target and no power control command defined. You need to manually reset the device.\n%s" % output)

    def _wait_until_booted(self):
        ''' Waits until the target device has booted (if we have just power cycled it) '''
        # Subclasses with better methods of determining boot can override this
        time.sleep(120)

    def deploy(self):
        # base class just sets the ssh log file for us
        super(MasterImageHardwareTarget, self).deploy()
        self.master = sshcontrol.SSHControl(ip=self.ip, logfile=self.sshlog, timeout=600, port=self.port)
        status, output = self.master.run("cat /etc/masterimage")
        if status != 0:
            # We're not booted into the master image, so try rebooting
            bb.plain("%s - booting into the master image" % self.pn)
            self.power_ctl("cycle")
            self._wait_until_booted()

        bb.plain("%s - deploying image on target" % self.pn)
        status, output = self.master.run("cat /etc/masterimage")
        if status != 0:
            bb.fatal("No ssh connectivity or target isn't running a master image.\n%s" % output)
        if self.user_cmds:
            self.deploy_cmds = self.user_cmds.split("\n")
        try:
            self._deploy()
        except Exception as e:
            bb.fatal("Failed deploying test image: %s" % e)

    @abstractmethod
    def _deploy(self):
        pass

    def start(self, extra_bootparams=None):
        bb.plain("%s - boot test image on target" % self.pn)
        self._start()
        # set the ssh object for the target/test image
        self.connection = sshcontrol.SSHControl(self.ip, logfile=self.sshlog, port=self.port)
        bb.plain("%s - start running tests" % self.pn)

    @abstractmethod
    def _start(self):
        pass

    def stop(self):
        bb.plain("%s - reboot/powercycle target" % self.pn)
        self.power_cycle(self.master)


class SystemdbootTarget(MasterImageHardwareTarget):

    def __init__(self, d):
        super(SystemdbootTarget, self).__init__(d)
        # this the value we need to set in the LoaderEntryOneShot EFI variable
        # so the system boots the 'test' bootloader label and not the default
        # The first four bytes are EFI bits, and the rest is an utf-16le string
        # (EFI vars values need to be utf-16)
        # $ echo -en "test\0" | iconv -f ascii -t utf-16le | hexdump -C
        # 00000000  74 00 65 00 73 00 74 00  00 00                    |t.e.s.t...|
        self.efivarvalue = r'\x07\x00\x00\x00\x74\x00\x65\x00\x73\x00\x74\x00\x00\x00'
        self.deploy_cmds = [
                'mount -L boot /boot',
                'mkdir -p /mnt/testrootfs',
                'mount -L testrootfs /mnt/testrootfs',
                'modprobe efivarfs',
                'mount -t efivarfs efivarfs /sys/firmware/efi/efivars',
                'cp ~/test-kernel /boot',
                'rm -rf /mnt/testrootfs/*',
                'tar xvf ~/test-rootfs.%s -C /mnt/testrootfs' % self.image_fstype,
                'printf "%s" > /sys/firmware/efi/efivars/LoaderEntryOneShot-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f' % self.efivarvalue
                ]

    def _deploy(self):
        # make sure these aren't mounted
        self.master.run("umount /boot; umount /mnt/testrootfs; umount /sys/firmware/efi/efivars;")
        # from now on, every deploy cmd should return 0
        # else an exception will be thrown by sshcontrol
        self.master.ignore_status = False
        self.master.copy_to(self.rootfs, "~/test-rootfs." + self.image_fstype)
        self.master.copy_to(self.kernel, "~/test-kernel")
        for cmd in self.deploy_cmds:
            self.master.run(cmd)

    def _start(self, params=None):
        self.power_cycle(self.master)
        # there are better ways than a timeout but this should work for now
        time.sleep(120)
