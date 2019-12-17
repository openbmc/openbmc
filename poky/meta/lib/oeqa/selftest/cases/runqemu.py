#
# Copyright (c) 2017 Wind River Systems, Inc.
#
# SPDX-License-Identifier: MIT
#

import re
import tempfile
import time
import oe.types
from oeqa.core.decorator import OETestTag
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_var, runCmd

class RunqemuTests(OESelftestTestCase):
    """Runqemu test class"""

    image_is_ready = False
    deploy_dir_image = ''

    def setUpLocal(self):
        super(RunqemuTests, self).setUpLocal()
        self.recipe = 'core-image-minimal'
        self.machine =  'qemux86-64'
        self.fstypes = "ext4 iso hddimg wic.vmdk wic.qcow2 wic.vdi"
        self.cmd_common = "runqemu nographic"

        kvm = oe.types.qemu_use_kvm(get_bb_var('QEMU_USE_KVM'), 'x86_64')
        if kvm:
            self.cmd_common += " kvm"

        self.write_config(
"""
MACHINE = "%s"
IMAGE_FSTYPES = "%s"
# 10 means 1 second
SYSLINUX_TIMEOUT = "10"
"""
% (self.machine, self.fstypes)
        )

        if not RunqemuTests.image_is_ready:
            RunqemuTests.deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
            bitbake(self.recipe)
            RunqemuTests.image_is_ready = True

    def test_boot_machine(self):
        """Test runqemu machine"""
        cmd = "%s %s" % (self.cmd_common, self.machine)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(qemu.runner.logged, "Failed: %s, %s" % (cmd, f.read()))

    def test_boot_machine_ext4(self):
        """Test runqemu machine ext4"""
        cmd = "%s %s ext4" % (self.cmd_common, self.machine)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn('rootfs.ext4', f.read(), "Failed: %s" % cmd)

    def test_boot_machine_iso(self):
        """Test runqemu machine iso"""
        cmd = "%s %s iso" % (self.cmd_common, self.machine)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn('media=cdrom', f.read(), "Failed: %s" % cmd)

    def test_boot_recipe_image(self):
        """Test runqemu recipe-image"""
        cmd = "%s %s" % (self.cmd_common, self.recipe)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(qemu.runner.logged, "Failed: %s, %s" % (cmd, f.read()))


    def test_boot_recipe_image_vmdk(self):
        """Test runqemu recipe-image vmdk"""
        cmd = "%s %s wic.vmdk" % (self.cmd_common, self.recipe)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn('format=vmdk', f.read(), "Failed: %s" % cmd)

    def test_boot_recipe_image_vdi(self):
        """Test runqemu recipe-image vdi"""
        cmd = "%s %s wic.vdi" % (self.cmd_common, self.recipe)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn('format=vdi', f.read(), "Failed: %s" % cmd)

    def test_boot_deploy(self):
        """Test runqemu deploy_dir_image"""
        cmd = "%s %s" % (self.cmd_common, self.deploy_dir_image)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(qemu.runner.logged, "Failed: %s, %s" % (cmd, f.read()))


    def test_boot_deploy_hddimg(self):
        """Test runqemu deploy_dir_image hddimg"""
        cmd = "%s %s hddimg" % (self.cmd_common, self.deploy_dir_image)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(re.search('file=.*.hddimg', f.read()), "Failed: %s, %s" % (cmd, f.read()))

    def test_boot_machine_slirp(self):
        """Test runqemu machine slirp"""
        cmd = "%s slirp %s" % (self.cmd_common, self.machine)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn(' -netdev user', f.read(), "Failed: %s" % cmd)

    def test_boot_machine_slirp_qcow2(self):
        """Test runqemu machine slirp qcow2"""
        cmd = "%s slirp wic.qcow2 %s" % (self.cmd_common, self.machine)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertIn('format=qcow2', f.read(), "Failed: %s" % cmd)

    def test_boot_qemu_boot(self):
        """Test runqemu /path/to/image.qemuboot.conf"""
        qemuboot_conf = "%s-%s.qemuboot.conf" % (self.recipe, self.machine)
        qemuboot_conf = os.path.join(self.deploy_dir_image, qemuboot_conf)
        if not os.path.exists(qemuboot_conf):
            self.skipTest("%s not found" % qemuboot_conf)
        cmd = "%s %s" % (self.cmd_common, qemuboot_conf)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(qemu.runner.logged, "Failed: %s, %s" % (cmd, f.read()))

    def test_boot_rootfs(self):
        """Test runqemu /path/to/rootfs.ext4"""
        rootfs = "%s-%s.ext4" % (self.recipe, self.machine)
        rootfs = os.path.join(self.deploy_dir_image, rootfs)
        if not os.path.exists(rootfs):
            self.skipTest("%s not found" % rootfs)
        cmd = "%s %s" % (self.cmd_common, rootfs)
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            with open(qemu.qemurunnerlog) as f:
                self.assertTrue(qemu.runner.logged, "Failed: %s, %s" % (cmd, f.read()))


# This test was designed as a separate class to test that shutdown
# command will shutdown qemu as expected on each qemu architecture
# based on the MACHINE configuration inside the config file
# (eg. local.conf).
#
# This was different compared to RunqemuTests, where RunqemuTests was
# dedicated for MACHINE=qemux86-64 where it test that qemux86-64 will
# bootup various filesystem types, including live image(iso and hddimg)
# where live image was not supported on all qemu architecture.
@OETestTag("machine")
class QemuTest(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(QemuTest, cls).setUpClass()
        cls.recipe = 'core-image-minimal'
        cls.machine =  get_bb_var('MACHINE')
        cls.deploy_dir_image =  get_bb_var('DEPLOY_DIR_IMAGE')
        cls.cmd_common = "runqemu nographic"
        cls.qemuboot_conf = "%s-%s.qemuboot.conf" % (cls.recipe, cls.machine)
        cls.qemuboot_conf = os.path.join(cls.deploy_dir_image, cls.qemuboot_conf)
        bitbake(cls.recipe)

    def _start_qemu_shutdown_check_if_shutdown_succeeded(self, qemu, timeout):
        qemu.run_serial("shutdown -h now")
        # Stop thread will stop the LoggingThread instance used for logging
        # qemu through serial console, stop thread will prevent this code
        # from facing exception (Console connection closed unexpectedly)
        # when qemu was shutdown by the above shutdown command
        qemu.runner.stop_thread()
        time_track = 0
        try:
            while True:
                is_alive = qemu.check()
                if not is_alive:
                    return True
                if time_track > timeout:
                    return False
                time.sleep(1)
                time_track += 1
        except SystemExit:
            return True

    def test_qemu_can_shutdown(self):
        self.assertExists(self.qemuboot_conf)
        cmd = "%s %s" % (self.cmd_common, self.qemuboot_conf)
        shutdown_timeout = 120
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            qemu_shutdown_succeeded = self._start_qemu_shutdown_check_if_shutdown_succeeded(qemu, shutdown_timeout)
            self.assertTrue(qemu_shutdown_succeeded, 'Failed: %s does not shutdown within timeout(%s)' % (self.machine, shutdown_timeout))

    # Need to have portmap/rpcbind running to allow this test to work and
    # current autobuilder setup does not have this.
    def disabled_test_qemu_can_boot_nfs_and_shutdown(self):
        self.assertExists(self.qemuboot_conf)
        bitbake('meta-ide-support')
        rootfs_tar = "%s-%s.tar.bz2" % (self.recipe, self.machine)
        rootfs_tar = os.path.join(self.deploy_dir_image, rootfs_tar)
        self.assertExists(rootfs_tar)
        tmpdir = tempfile.mkdtemp(prefix='qemu_nfs')
        tmpdir_nfs = os.path.join(tmpdir, 'nfs')
        cmd_extract_nfs = 'runqemu-extract-sdk %s %s' % (rootfs_tar, tmpdir_nfs)
        result = runCmd(cmd_extract_nfs)
        self.assertEqual(0, result.status, "runqemu-extract-sdk didn't run as expected. %s" % result.output)
        cmd = "%s nfs %s %s" % (self.cmd_common, self.qemuboot_conf, tmpdir_nfs)
        shutdown_timeout = 120
        with runqemu(self.recipe, ssh=False, launch_cmd=cmd) as qemu:
            qemu_shutdown_succeeded = self._start_qemu_shutdown_check_if_shutdown_succeeded(qemu, shutdown_timeout)
            self.assertTrue(qemu_shutdown_succeeded, 'Failed: %s does not shutdown within timeout(%s)' % (self.machine, shutdown_timeout))
        runCmd('rm -rf %s' % tmpdir)
