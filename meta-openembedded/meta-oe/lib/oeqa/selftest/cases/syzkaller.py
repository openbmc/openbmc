#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
from oeqa.utils.network import get_free_port

class TestSyzkaller(OESelftestTestCase):
    def setUpSyzkallerConfig(self, os_arch, qemu_postfix):
        syz_target_sysroot = get_bb_var('PKGD', 'syzkaller')
        syz_target = os.path.join(syz_target_sysroot, 'usr')

        qemu_native_bin = os.path.join(self.syz_native_sysroot, 'usr/bin/qemu-system-' + qemu_postfix)
        kernel_cmdline = "ip=dhcp rootfs=/dev/sda dummy_hcd.num=%s" % (self.dummy_hcd_num)
        kernel_objdir = self.deploy_dir_image
        port = get_free_port()

        if not os.path.exists(self.syz_workdir):
            os.mkdir(self.syz_workdir)

        with open(self.syz_cfg, 'w') as f:
            f.write(
"""
{
	"target": "%s",
	"http": "127.0.0.1:%s",
	"workdir": "%s",
	"kernel_obj": "%s",
	"kernel_src": "%s",
	"image": "%s",
	"syzkaller": "%s",
	"type": "qemu",
	"reproduce" : false,
	"sandbox": "none",
	"vm": {
		"count": %s,
		"kernel": "%s",
		"cmdline": "%s",
		"cpu": %s,
		"mem": %s,
		"qemu": "%s",
		"qemu_args": "-device virtio-scsi-pci,id=scsi -device scsi-hd,drive=rootfs -enable-kvm -cpu host,migratable=off",
		"image_device": "drive index=0,id=rootfs,if=none,media=disk,file="
	}
}
"""
% (os_arch, port, self.syz_workdir, kernel_objdir, self.kernel_src,
   self.rootfs, syz_target, self.syz_qemu_vms, self.kernel, kernel_cmdline,
   self.syz_qemu_cpus, self.syz_qemu_mem, qemu_native_bin))

    def test_syzkallerFuzzingQemux86_64(self):
        self.image = 'core-image-minimal'
        self.machine = 'qemux86-64'
        self.fstype = "ext4"

        self.write_config(
"""
MACHINE = "%s"
IMAGE_FSTYPES = "%s"
KERNEL_IMAGETYPES += "vmlinux"
EXTRA_IMAGE_FEATURES += " ssh-server-openssh"
IMAGE_ROOTFS_EXTRA_SPACE = "512000"
KERNEL_EXTRA_FEATURES += " \
    cfg/debug/syzkaller/debug-syzkaller.scc \
"
IMAGE_INSTALL:append = " syzkaller"
"""
% (self.machine, self.fstype))

        build_vars = ['TOPDIR', 'DEPLOY_DIR_IMAGE', 'STAGING_KERNEL_DIR']
        syz_fuzz_vars = ['SYZ_WORKDIR', 'SYZ_FUZZTIME', 'SYZ_QEMU_MEM', 'SYZ_QEMU_CPUS', 'SYZ_QEMU_VM_COUNT']
        syz_aux_vars = ['SYZ_DUMMY_HCD_NUM']

        needed_vars = build_vars + syz_fuzz_vars + syz_aux_vars
        bb_vars = get_bb_vars(needed_vars)

        for var in syz_fuzz_vars:
                if not bb_vars[var]:
                    self.skipTest(
"""
%s variable not set.
Please configure %s fuzzing parameters to run this test.

Example local.conf config:
SYZ_WORKDIR="<path>"  # syzkaller workdir location (must be persistent across os-selftest runs)
SYZ_FUZZTIME="30"     # fuzzing time in minutes
SYZ_QEMU_VM_COUNT="1" # number of qemu VMs to be used for fuzzing
SYZ_QEMU_MEM="2048"'  # memory used by each qemu VM
SYZ_QEMU_CPUS="2"'    # number of cpus used by each qemu VM
"""
% (var, ', '.join(syz_fuzz_vars)))

        self.topdir = bb_vars['TOPDIR']
        self.deploy_dir_image = bb_vars['DEPLOY_DIR_IMAGE']
        self.kernel_src = bb_vars['STAGING_KERNEL_DIR']

        """
        SYZ_WORKDIR must be set to an absolute path where syzkaller will store
        the corpus database, config, runtime and crash data generated during
        fuzzing. It must be persistent between oe-selftest runs, so the fuzzer
        does not start over again on each run.
        """
        self.syz_workdir = bb_vars['SYZ_WORKDIR']
        self.syz_fuzztime = int(bb_vars['SYZ_FUZZTIME']) * 60
        self.syz_qemu_mem = int(bb_vars['SYZ_QEMU_MEM'])
        self.syz_qemu_cpus = int(bb_vars['SYZ_QEMU_CPUS'])
        self.syz_qemu_vms = int(bb_vars['SYZ_QEMU_VM_COUNT'])
        self.dummy_hcd_num = int(bb_vars['SYZ_DUMMY_HCD_NUM'] or 8)

        self.syz_cfg = os.path.join(self.syz_workdir, 'syzkaller.cfg')
        self.kernel = os.path.join(self.deploy_dir_image, 'bzImage')
        self.rootfs = os.path.join(self.deploy_dir_image, '%s-%s.%s' % (self.image, self.machine, self.fstype))

        self.syz_native_sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'syzkaller-native')

        self.setUpSyzkallerConfig("linux/amd64", "x86_64")

        bitbake(self.image)
        bitbake('syzkaller')
        bitbake('syzkaller-native -c addto_recipe_sysroot')

        cmd = "syz-manager -config %s" % self.syz_cfg
        runCmd(cmd, native_sysroot = self.syz_native_sysroot, timeout=self.syz_fuzztime, output_log=self.logger, ignore_status=True, shell=True)
