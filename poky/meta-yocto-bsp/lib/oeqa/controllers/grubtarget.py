# Copyright (C) 2014 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module adds support to testimage.bbclass to deploy images and run
# tests on a Generic PC that boots using grub bootloader. The device must
# be set up as per README.hardware and the master image should be deployed
# onto the harddisk so that it boots into it by default.For booting into the
# image under test we interact with grub over serial, so for the
# Generic PC you will need an additional serial cable and device under test
# needs to have a serial interface. The separate ext3
# partition that will contain the image to be tested must be labelled
# "testrootfs" so that the deployment code below can find it.

import os
import bb
import time
import subprocess
import sys
import pexpect

from oeqa.controllers.controllerimage import ControllerImageHardwareTarget

class GrubTarget(ControllerImageHardwareTarget):

    def __init__(self, d):
        super(GrubTarget, self).__init__(d)
        self.deploy_cmds = [
                'mount -L boot /boot',
                'mkdir -p /mnt/testrootfs',
                'mount -L testrootfs /mnt/testrootfs',
                'cp ~/test-kernel /boot',
                'rm -rf /mnt/testrootfs/*',
                'tar xvf ~/test-rootfs.%s -C /mnt/testrootfs' % self.image_fstype,
                ]

        if not self.serialcontrol_cmd:
            bb.fatal("This TEST_TARGET needs a TEST_SERIALCONTROL_CMD defined in local.conf.")


    def _deploy(self):
        # make sure these aren't mounted
        self.controller.run("umount /boot; umount /mnt/testrootfs;")
        self.controller.ignore_status = False
        # Kernel files may not be in the image, so copy them just in case
        self.controller.copy_to(self.rootfs, "~/test-rootfs." + self.image_fstype)
        self.controller.copy_to(self.kernel, "~/test-kernel")
        for cmd in self.deploy_cmds:
            self.controller.run(cmd)

    def _start(self, params=None):
        self.power_cycle(self.controller)
        try:
            serialconn = pexpect.spawn(self.serialcontrol_cmd, env=self.origenv, logfile=sys.stdout)
            serialconn.expect("GNU GRUB  version 2.00")
            serialconn.expect("Linux")
            serialconn.sendline("x")
            serialconn.expect("login:", timeout=120)
            serialconn.close()
        except pexpect.ExceptionPexpect as e:
            bb.fatal('Serial interaction failed: %s' % str(e))

    def _wait_until_booted(self):
        try:
            serialconn = pexpect.spawn(self.serialcontrol_cmd, env=self.origenv, logfile=sys.stdout)
            serialconn.expect("login:", timeout=120)
            serialconn.close()
        except pexpect.ExceptionPexpect as e:
            bb.fatal('Serial interaction failed: %s' % str(e))

