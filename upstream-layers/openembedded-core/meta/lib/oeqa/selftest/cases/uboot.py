# Qemu-based u-boot bootloader integration testing
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import textwrap

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_var, get_bb_vars, runCmd
from oeqa.core.decorator.data import skipIfNotArch, skipIfNotBuildArch
from oeqa.core.decorator import OETestTag

uboot_boot_patterns = {
        'search_reached_prompt': "stop autoboot",
        'search_login_succeeded': "=>",
        'search_cmd_finished': "=>"
        }


class UBootTest(OESelftestTestCase):

    @skipIfNotArch(['arm', 'aarch64'])
    @OETestTag("runqemu")
    def test_boot_uboot(self):
        """
        Tests building u-boot and booting it with QEMU
        """

        self.write_config("""
QB_DEFAULT_BIOS = "u-boot.bin"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot"
QEMU_USE_KVM = "False"
""")
        bitbake("virtual/bootloader core-image-minimal")

        with runqemu('core-image-minimal', ssh=False, runqemuparams='nographic',
                     boot_patterns=uboot_boot_patterns) as qemu:

            # test if u-boot console works
            cmd = "version"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(status, 1, msg=output)
            self.assertTrue("U-Boot" in output, msg=output)

    @skipIfNotArch(['aarch64'])
    @skipIfNotBuildArch(['aarch64'])
    @OETestTag("runqemu")
    def test_boot_uboot_kvm_to_full_target(self):
        """
        Tests building u-boot and booting it with QEMU and KVM.
        Requires working KVM on build host. See "kvm-ok" output.
        """

        runCmd("kvm-ok")

        image = "core-image-minimal"
        vars = get_bb_vars(['HOST_ARCH', 'BUILD_ARCH'], image)
        host_arch = vars['HOST_ARCH']
        build_arch = vars['BUILD_ARCH']

        self.assertEqual(host_arch, build_arch, 'HOST_ARCH %s and BUILD_ARCH %s must match for KVM' % (host_arch, build_arch))

        self.write_config("""
QEMU_USE_KVM = "1"

# Using u-boot in EFI mode, need ESP partition for grub/systemd-boot/kernel etc
IMAGE_FSTYPES:pn-core-image-minimal:append = " wic"

# easiest to follow genericarm64 setup with wks file, initrd and EFI loader
INITRAMFS_IMAGE = "core-image-initramfs-boot"
EFI_PROVIDER = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd-boot", "grub-efi", d)}"
WKS_FILE = "genericarm64.wks.in"

# use wic image with ESP for u-boot, not ext4
QB_DEFAULT_FSTYPE = "wic"

PREFERRED_PROVIDER_virtual/bootloader = "u-boot"
QB_DEFAULT_BIOS = "u-boot.bin"

# let u-boot or EFI loader load kernel from ESP
QB_DEFAULT_KERNEL = "none"

# virt pci, not scsi because support not in u-boot to find ESP
QB_DRIVE_TYPE = "/dev/vd"
""")
        bitbake("virtual/bootloader %s" % image)

        runqemu_params = get_bb_var('TEST_RUNQEMUPARAMS', image) or ""
        with runqemu(image, ssh=False, runqemuparams='nographic kvm %s' % runqemu_params) as qemu:

            # boot to target and login worked, should have been fast with kvm
            cmd = "dmesg"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(status, 1, msg=output)
            # Machine is qemu
            self.assertTrue("Machine model: linux,dummy-virt" in output, msg=output)
            # with KVM enabled
            self.assertTrue("KVM: hypervisor services detected" in output, msg=output)

    def test_uboot_initial_env_binary(self):
        """
        Tests building the initial U-Boot environment in binary format with
        the U-Boot mkimage tool.
        We assume that the uboot-mkenvimage tool generates a correct binary.
        """

        self.write_config(textwrap.dedent("""
            UBOOT_INITIAL_ENV_BINARY = "1"
            UBOOT_INITIAL_ENV_BINARY_SIZE = "0x4000"
            UBOOT_INITIAL_ENV_BINARY_REDUND = "1"
        """))

        bitbake("u-boot")

        bb_vars = get_bb_vars(["DEPLOYDIR", "UBOOT_INITIAL_ENV"], "u-boot")

        uboot_initial_env_binary_path = os.path.realpath(os.path.join(
            bb_vars["DEPLOYDIR"], "%s.bin" % bb_vars["UBOOT_INITIAL_ENV"]
        ))

        self.assertExists(uboot_initial_env_binary_path)

    def test_uboot_config_initial_env_binary(self):
        """
        Tests building the initial U-Boot environment in binary format with
        the U-Boot mkimage tool for a U-Boot config.
        We assume that the uboot-mkenvimage tool generates a correct binary.
        """

        uboot_machine = get_bb_var("UBOOT_MACHINE", "u-boot")

        self.write_config(textwrap.dedent(f"""
            UBOOT_CONFIG = "test"
            UBOOT_CONFIG[test] := "{uboot_machine}"
            UBOOT_MACHINE = ""
            UBOOT_INITIAL_ENV_BINARY = "1"
            UBOOT_INITIAL_ENV_BINARY_SIZE = "0x4000"
            UBOOT_INITIAL_ENV_BINARY_REDUND = "1"
        """))

        bitbake("u-boot")

        bb_vars = get_bb_vars(["DEPLOYDIR", "UBOOT_INITIAL_ENV"], "u-boot")

        uboot_initial_env_binary_path = os.path.realpath(os.path.join(
            bb_vars["DEPLOYDIR"], "%s-test.bin" % bb_vars["UBOOT_INITIAL_ENV"]
        ))

        self.assertExists(uboot_initial_env_binary_path)


class UBootConfigTest(OESelftestTestCase):

    def test_uboot_config_extract(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations into the support variables.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_BINARY = "defBinary"
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2 test3"
            UBOOT_CONFIG[test1] = "machine1"
            UBOOT_CONFIG[test2] = "machine2"
            UBOOT_CONFIG[test3] = "machine3"
            UBOOT_CONFIG_IMAGE_FSTYPES[test2] = "image_fstype2"
            UBOOT_CONFIG_IMAGE_FSTYPES[test3] = "image_fstype3"
            UBOOT_CONFIG_BINARY[test3] = "binary3"
            UBOOT_CONFIG_MAKE_OPTS[test1] = "OPT=1"
            UBOOT_CONFIG_MAKE_OPTS[test3] = "OPT=3 FOO=2"
            UBOOT_CONFIG_FRAGMENTS[test1] = "fragment1a fragment1b"
            UBOOT_CONFIG_FRAGMENTS[test2] = "fragment2"
        """))

        bitbake("-e u-boot")

        bb_vars = get_bb_vars(["UBOOT_MACHINE", "IMAGE_FSTYPES", "UBOOT_CONFIG_BINARY", "UBOOT_CONFIG_MAKE_OPTS", "UBOOT_CONFIG_FRAGMENTS"], "u-boot")

        self.assertEqual(bb_vars["UBOOT_MACHINE"], " machine1 machine2 machine3")
        self.assertTrue(bb_vars["IMAGE_FSTYPES"].endswith(" image_fstype2 image_fstype3"))
        self.assertEqual(bb_vars["UBOOT_CONFIG_BINARY"], "defBinary ? defBinary ? binary3 ? ")
        self.assertEqual(bb_vars["UBOOT_CONFIG_MAKE_OPTS"], "OPT=1 ?  ? OPT=3 FOO=2 ? ")
        self.assertEqual(bb_vars["UBOOT_CONFIG_FRAGMENTS"], "fragment1a fragment1b ? fragment2 ?  ? ")

    def test_uboot_config_extract_legacy(self):
        """
        Tests the legacy comma-separated portion of the uboot-config.bbclass
        python function that extracts all of the config variations into the
        support variables.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_BINARY = "defBinary"
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2 test3"
            UBOOT_CONFIG[test1] = "machine1"
            UBOOT_CONFIG[test2] = "machine2,image_fstype2"
            UBOOT_CONFIG[test3] = "machine3,image_fstype3,binary3"
        """))

        bitbake("-e u-boot")

        bb_vars = get_bb_vars(["UBOOT_MACHINE", "IMAGE_FSTYPES", "UBOOT_CONFIG_BINARY", "UBOOT_CONFIG_MAKE_OPTS", "UBOOT_CONFIG_FRAGMENTS"], "u-boot")

        self.assertEqual(bb_vars["UBOOT_MACHINE"], " machine1 machine2 machine3")
        self.assertTrue(bb_vars["IMAGE_FSTYPES"].endswith(" image_fstype2 image_fstype3"))
        self.assertEqual(bb_vars["UBOOT_CONFIG_BINARY"], "defBinary ? defBinary ? binary3 ? ")
        self.assertEqual(bb_vars["UBOOT_CONFIG_MAKE_OPTS"], " ?  ?  ? ")
        self.assertEqual(bb_vars["UBOOT_CONFIG_FRAGMENTS"], " ?  ?  ? ")

    def test_uboot_config_extract_error_missing_config(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on having a missing
        flag in UBOOT_CONFIG.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_BINARY = "defBinary"
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2 test3"
            UBOOT_CONFIG[test1] = "machine1"
            UBOOT_CONFIG[test2] = "machine2"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: The selected UBOOT_CONFIG key test3 has no match in dict_keys(['test1', 'test2']).", s)

    def test_uboot_config_extract_error_nothing_set(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on not having 
        UBOOT_MACHINE or UBOOT_CONFIG set.
        """

        machine = get_bb_var("MACHINE", "u-boot")

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = ""
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: Either UBOOT_MACHINE or UBOOT_CONFIG must be set in the %s machine configuration." % machine, s)

    def test_uboot_config_extract_error_set_both_config_and_machine(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on setting both 
        UBOOT_MACHINE or UBOOT_CONFIG.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = "machine"
            UBOOT_CONFIG = "test1 test2"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: You cannot use UBOOT_MACHINE and UBOOT_CONFIG at the same time.", s)

    def test_uboot_config_extract_error_set_uboot_config_image_fstypes(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on setting internal
        variable UBOOT_CONFIG_IMAGE_FSTYPES.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2"
            UBOOT_CONFIG_IMAGE_FSTYPES = "fstype"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: You cannot use UBOOT_CONFIG_IMAGE_FSTYPES as a variable, you can only set flags.", s)

    def test_uboot_config_extract_error_set_uboot_config_binary(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on setting internal
        variable UBOOT_CONFIG_BINARY.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2"
            UBOOT_CONFIG_BINARY = "binary"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: You cannot use UBOOT_CONFIG_BINARY as a variable, you can only set flags.", s)

    def test_uboot_config_extract_error_set_uboot_config_make_opts(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on setting internal
        variable UBOOT_CONFIG_MAKE_OPTS.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2"
            UBOOT_CONFIG_MAKE_OPTS = "OPT=1"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: You cannot use UBOOT_CONFIG_MAKE_OPTS as a variable, you can only set flags.", s)

    def test_uboot_config_extract_error_set_uboot_config_fragments(self):
        """
        Tests the uboot-config.bbclass python function that extracts all of
        the config variations to make sure it errors on setting internal
        variable UBOOT_CONFIG_FRAGMENTS.
        """

        self.write_config(textwrap.dedent(f"""
            UBOOT_MACHINE = ""
            UBOOT_CONFIG = "test1 test2"
            UBOOT_CONFIG_FRAGMENTS = "fragment"
        """))

        with self.assertRaises(AssertionError) as cm:
            bitbake("-e u-boot")

        e = cm.exception
        s = str(e)

        self.assertIn("ERROR: Nothing PROVIDES 'u-boot'", s)
        self.assertIn("u-boot was skipped: You cannot use UBOOT_CONFIG_FRAGMENTS as a variable, you can only set flags.", s)
