# Copyright (C) 2014 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module adds support to testimage.bbclass to deploy images and run
# tests on a Ubiquiti Networks EdgeRouter Lite. The device must be set up
# to boot into the master image already - the easiest way to do that is as
# follows:
#
# 1. Take out the internal USB drive and plug it into your PC
# 2. Repartition the USB drive so that you have three partitions in this
#    order:
#      1: vfat, labelled "boot" (it will need to be formatted with mkfs.vfat
#         for this to be possible, since FAT partitions formatted under
#         DOS/Windows will only support uppercase labels)
#      2: ext3 (for master image) labelled "testmaster"
#      3: ext3 (for image under test) labelled "testrootfs"
# 3. Copy the kernel to be used by the master image to the FAT partition
#    (it should be named "vmlinux.64" with the factory u-boot configuration)
# 4. Install the master image onto the "testmaster" ext3 partition. If
#    you do this by just extracting the contents of an image onto the
#    partition, you will also likely need to create the master image marker
#    file /etc/masterimage within this partition so that we can tell when
#    we're booted into it that it is the master image.
# 5. Put the USB drive back into the device, and ensure the console port
#    and first ethernet port are connected before powering on
#
# TEST_SERIALCONTROL_CMD will need to be set in local.conf so that we can
# interact with u-boot over the serial console port.

import os
import bb
import time
import subprocess
import sys
import pexpect

import oeqa.utils.sshcontrol as sshcontrol
from oeqa.controllers.masterimage import MasterImageHardwareTarget


class EdgeRouterTarget(MasterImageHardwareTarget):

    def __init__(self, d):
        super(EdgeRouterTarget, self).__init__(d)

	self.image_fstype = self.get_image_fstype(d)
        self.deploy_cmds = [
                'mount -L boot /boot',
                'mkdir -p /mnt/testrootfs',
                'mount -L testrootfs /mnt/testrootfs',
                'cp ~/test-kernel /boot',
                'rm -rf /mnt/testrootfs/*',
                'tar xvf ~/test-rootfs.%s -C /mnt/testrootfs' % self.image_fstype
                ]
        if not self.serialcontrol_cmd:
            bb.fatal("This TEST_TARGET needs a TEST_SERIALCONTROL_CMD defined in local.conf.")


    def _deploy(self):
        self.master.run("umount /mnt/testrootfs;")
        self.master.ignore_status = False
        self.master.copy_to(self.kernel, "~/test-kernel")
        self.master.copy_to(self.rootfs, "~/test-rootfs.%s" % self.image_fstype)
        for cmd in self.deploy_cmds:
            self.master.run(cmd)

    def _start(self, params=None):
        self.power_cycle(self.master)
        try:
            serialconn = pexpect.spawn(self.serialcontrol_cmd, env=self.origenv, logfile=sys.stdout)
            serialconn.expect("U-Boot")
            serialconn.sendline("a")
            serialconn.expect("Octeon ubnt_e100#")
            serialconn.sendline("fatload usb 0:1 $loadaddr test-kernel")
            serialconn.expect(" bytes read")
            serialconn.expect("Octeon ubnt_e100#")
            serialconn.sendline("bootoctlinux $loadaddr coremask=0x3 root=/dev/sda3 rw rootwait mtdparts=phys_mapped_flash:512k(boot0),512k(boot1),64k@3072k(eeprom)")
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
