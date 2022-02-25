# Copyright (C) 2014 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module adds support to testimage.bbclass to deploy images and run
# tests on a BeagleBone (original "white" or Black models). The device must
# be set up as per README.hardware and the master image should be deployed
# onto the card so that it boots into it by default. For booting into the
# image under test we interact with u-boot over serial, so for the
# BeagleBone Black you will need an additional TTL serial cable since a
# serial interface isn't automatically provided over the USB connection as
# it is on the original BeagleBone ("white") version. The separate ext3
# partition that will contain the image to be tested must be labelled
# "testrootfs" so that the deployment code below can find it.
#
# NOTE: for the BeagleBone "white" (original version) you may need to use
# a script which handles the serial device disappearing on power down, such
# as scripts/contrib/serdevtry in OE-Core.

import os
import bb
import time
import subprocess
import sys
import pexpect

from oeqa.controllers.controllerimage import ControllerImageHardwareTarget


class BeagleBoneTarget(ControllerImageHardwareTarget):

    dtbs = {'uImage-am335x-bone.dtb': 'am335x-bone.dtb', 'uImage-am335x-boneblack.dtb': 'am335x-boneblack.dtb'}

    @classmethod
    def get_extra_files(self):
        return list(self.dtbs.keys())

    def __init__(self, d):
        super(BeagleBoneTarget, self).__init__(d)

        self.image_fstype = self.get_image_fstype(d)
        self.deploy_cmds = [
                'mkdir -p /mnt/testrootfs',
                'mount -L testrootfs /mnt/testrootfs',
                'rm -rf /mnt/testrootfs/*',
                'tar xvf ~/test-rootfs.%s -C /mnt/testrootfs' % self.image_fstype,
                '[ -e /mnt/testrootfs/boot/uImage ] || [ -L /mnt/testrootfs/boot/uImage ] || cp ~/test-kernel /mnt/testrootfs/boot/uImage',
                ]

        for _, dtbfn in self.dtbs.iteritems():
            # Kernel and dtb files may not be in the image, so copy them if not
            self.deploy_cmds.append('[ -e /mnt/testrootfs/boot/{0} ] || cp ~/{0} /mnt/testrootfs/boot/'.format(dtbfn))

        if not self.serialcontrol_cmd:
            bb.fatal("This TEST_TARGET needs a TEST_SERIALCONTROL_CMD defined in local.conf.")


    def _deploy(self):
        self.controller.run("umount /boot; umount /mnt/testrootfs;")
        self.controller.ignore_status = False
        # Kernel and dtb files may not be in the image, so copy them just in case
        self.controller.copy_to(self.kernel, "~/test-kernel")
        kernelpath = os.path.dirname(self.kernel)
        for dtborig, dtbfn in self.dtbs.iteritems():
            dtbfile = os.path.join(kernelpath, dtborig)
            if os.path.exists(dtbfile):
                self.controller.copy_to(dtbfile, "~/%s" % dtbfn)
        self.controller.copy_to(self.rootfs, "~/test-rootfs.%s" % self.image_fstype)
        for cmd in self.deploy_cmds:
            self.controller.run(cmd)

    def _start(self, params=None):
        self.power_cycle(self.controller)
        try:
            serialconn = pexpect.spawn(self.serialcontrol_cmd, env=self.origenv, logfile=sys.stdout)
            # We'd wait for "U-Boot" here but sometimes we connect too late on BeagleBone white to see it
            serialconn.expect("NAND:")
            serialconn.expect("MMC:")
            serialconn.sendline("a")
            serialconn.expect("U-Boot#")
            serialconn.sendline("setenv bootpart 0:3")
            serialconn.expect("U-Boot#")
            serialconn.sendline("setenv mmcroot /dev/mmcblk0p3 ro")
            serialconn.expect("U-Boot#")
            serialconn.sendline("boot")
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
