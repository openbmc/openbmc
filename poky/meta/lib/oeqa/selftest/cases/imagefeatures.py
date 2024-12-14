#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu
from oeqa.utils.sshcontrol import SSHControl
import glob
import os
import json

class ImageFeatures(OESelftestTestCase):

    test_user = 'tester'
    root_user = 'root'

    @OETestTag("runqemu")
    def test_non_root_user_can_connect_via_ssh_without_password(self):
        """
        Summary: Check if non root user can connect via ssh without password
        Expected: 1. Connection to the image via ssh using root user without providing a password should be allowed.
                  2. Connection to the image via ssh using tester user without providing a password should be allowed.
        Product: oe-core
        Author: Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        features = 'EXTRA_IMAGE_FEATURES = "ssh-server-openssh empty-root-password allow-empty-password allow-root-login"\n'
        features += 'INHERIT += "extrausers"\n'
        features += 'EXTRA_USERS_PARAMS = "useradd -p \'\' {}; usermod -s /bin/sh {};"'.format(self.test_user, self.test_user)
        self.write_config(features)

        # Build a core-image-minimal
        bitbake('core-image-minimal')

        with runqemu("core-image-minimal") as qemu:
            # Attempt to ssh with each user into qemu with empty password
            for user in [self.root_user, self.test_user]:
                ssh = SSHControl(ip=qemu.ip, logfile=qemu.sshlog, user=user)
                status, output = ssh.run("true")
                self.assertEqual(status, 0, 'ssh to user %s failed with %s' % (user, output))

    @OETestTag("runqemu")
    def test_all_users_can_connect_via_ssh_without_password(self):
        """
        Summary:     Check if all users can connect via ssh without password
        Expected: 1. Connection to the image via ssh using root user without providing a password should NOT be allowed.
                  2. Connection to the image via ssh using tester user without providing a password should be allowed.
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        features = 'EXTRA_IMAGE_FEATURES = "ssh-server-openssh allow-empty-password allow-root-login"\n'
        features += 'INHERIT += "extrausers"\n'
        features += 'EXTRA_USERS_PARAMS = "useradd -p \'\' {}; usermod -s /bin/sh {};"'.format(self.test_user, self.test_user)
        self.write_config(features)

        # Build a core-image-minimal
        bitbake('core-image-minimal')

        with runqemu("core-image-minimal") as qemu:
            # Attempt to ssh with each user into qemu with empty password
            for user in [self.root_user, self.test_user]:
                ssh = SSHControl(ip=qemu.ip, logfile=qemu.sshlog, user=user)
                status, output = ssh.run("true")
                if user == 'root':
                    self.assertNotEqual(status, 0, 'ssh to user root was allowed when it should not have been')
                else:
                    self.assertEqual(status, 0, 'ssh to user tester failed with %s' % output)


    def test_wayland_support_in_image(self):
        """
        Summary:     Check Wayland support in image
        Expected:    1. Wayland image can be build
                     2. Wayland feature can be installed
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        distro_features = get_bb_var('DISTRO_FEATURES')
        if not ('opengl' in distro_features and 'wayland' in distro_features):
            self.skipTest('neither opengl nor wayland present on DISTRO_FEATURES so core-image-weston cannot be built')

        # Build a core-image-weston
        bitbake('core-image-weston')

    def test_bmap(self):
        """
        Summary:     Check bmap support
        Expected:    1. core-image-minimal can be build with bmap support
                     2. core-image-minimal is sparse
        Product:     oe-core
        Author:      Ed Bartosh <ed.bartosh@linux.intel.com>
        """

        features = 'IMAGE_FSTYPES += " ext4 ext4.bmap ext4.bmap.gz"'
        self.write_config(features)

        image = 'core-image-minimal'
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)

        image_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.ext4" % bb_vars['IMAGE_LINK_NAME'])
        bmap_path = "%s.bmap" % image_path
        gzip_path = "%s.gz" % bmap_path

        # check if result image, bmap and bmap.gz files are in deploy directory
        self.assertTrue(os.path.exists(image_path))
        self.assertTrue(os.path.exists(bmap_path))
        self.assertTrue(os.path.exists(gzip_path))

        # check if result image is sparse
        image_stat = os.stat(image_path)
        self.assertGreater(image_stat.st_size, image_stat.st_blocks * 512)

        # check if the resulting gzip is valid, --force is needed in case gzip_path is a symlink
        self.assertTrue(runCmd('gzip --test --force %s' % gzip_path))

    def test_hypervisor_fmts(self):
        """
        Summary:     Check various hypervisor formats
        Expected:    1. core-image-minimal can be built with vmdk, vdi and
                        qcow2 support.
                     2. qemu-img says each image has the expected format
        Product:     oe-core
        Author:      Tom Rini <trini@konsulko.com>
        """

        img_types = [ 'vmdk', 'vdi', 'qcow2' ]
        features = ""
        for itype in img_types:
            features += 'IMAGE_FSTYPES += "ext4.%s"\n' % itype
        self.write_config(features)

        image = 'core-image-minimal'
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)

        for itype in img_types:
            image_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.ext4.%s" %
                                      (bb_vars['IMAGE_LINK_NAME'], itype))

            # check if result image file is in deploy directory
            self.assertTrue(os.path.exists(image_path))

            # check if result image is vmdk
            sysroot = get_bb_var('STAGING_DIR_NATIVE', 'core-image-minimal')
            result = runCmd('qemu-img info --output json %s' % image_path,
                            native_sysroot=sysroot)
            try:
                data = json.loads(result.output)
                self.assertEqual(data.get('format'), itype,
                                 msg="Unexpected format in '%s'" % (result.output))
            except json.decoder.JSONDecodeError:
                self.fail("Could not parse '%ss'" % result.output)

    def test_long_chain_conversion(self):
        """
        Summary:     Check for chaining many CONVERSION_CMDs together
        Expected:    1. core-image-minimal can be built with
                        ext4.bmap.gz.bz2.zst.xz.u-boot and also create a
                        sha256sum
                     2. The above image has a valid sha256sum
        Product:     oe-core
        Author:      Tom Rini <trini@konsulko.com>
        """

        conv = "ext4.bmap.gz.bz2.zst.xz.u-boot"
        features = 'IMAGE_FSTYPES += "%s %s.sha256sum"' % (conv, conv)
        self.write_config(features)

        image = 'core-image-minimal'
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)
        image_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.%s" %
                                  (bb_vars['IMAGE_LINK_NAME'], conv))

        # check if resulting image is in the deploy directory
        self.assertTrue(os.path.exists(image_path))
        self.assertTrue(os.path.exists(image_path + ".sha256sum"))

        # check if the resulting sha256sum agrees
        self.assertTrue(runCmd('cd %s;sha256sum -c %s.%s.sha256sum' %
                               (bb_vars['DEPLOY_DIR_IMAGE'], bb_vars['IMAGE_LINK_NAME'], conv)))

    def test_image_fstypes(self):
        """
        Summary:     Check if image of supported image fstypes can be built
        Expected:    core-image-minimal can be built for various image types
        Product:     oe-core
        Author:      Ed Bartosh <ed.bartosh@linux.intel.com>
        """
        image = 'core-image-minimal'

        all_image_types = set(get_bb_var("IMAGE_TYPES", image).split())
        skip_image_types = set(('container', 'elf', 'f2fs', 'tar.zst', 'wic.zst', 'squashfs-lzo', 'vfat'))
        img_types = all_image_types - skip_image_types

        config = """
IMAGE_FSTYPES += "%s"
WKS_FILE = "wictestdisk.wks"
MKUBIFS_ARGS ?= "-m 2048 -e 129024 -c 2047"
UBINIZE_ARGS ?= "-m 2048 -p 128KiB -s 512"
MULTIUBI_BUILD += "mtd_2_128"
MKUBIFS_ARGS_mtd_2_128 ?= "-m 2048 -e 129024 -c 2047"
UBINIZE_ARGS_mtd_2_128 ?= "-m 2048 -p 128KiB -s 512"
MULTIUBI_BUILD += "mtd_4_256"
MKUBIFS_ARGS_mtd_4_256 ?= "-m 4096 -e 253952 -c 4096"
UBINIZE_ARGS_mtd_4_256 ?= "-m 4096 -p 256KiB"
""" % ' '.join(img_types)
        self.write_config(config)

        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME', 'MULTIUBI_BUILD'], image)

        for itype in img_types:
            if itype == 'multiubi':
                # For multiubi build we need to manage MULTIUBI_BUILD entry to append
                # specific name to IMAGE_LINK_NAME
                for vname in bb_vars['MULTIUBI_BUILD'].split():
                    image_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s_%s.ubifs" % (bb_vars['IMAGE_LINK_NAME'], vname))
                    # check if result image is in deploy directory
                    self.assertTrue(os.path.exists(image_path),
                                    "%s image %s doesn't exist" % (itype, image_path))
            else:
                image_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.%s" % (bb_vars['IMAGE_LINK_NAME'], itype))
                # check if result image is in deploy directory
                self.assertTrue(os.path.exists(image_path),
                                "%s image %s doesn't exist" % (itype, image_path))

    def test_useradd_static(self):
        config = """
USERADDEXTENSION = "useradd-staticids"
USERADD_ERROR_DYNAMIC = "skip"
USERADD_UID_TABLES += "files/static-passwd"
USERADD_GID_TABLES += "files/static-group"
"""
        self.write_config(config)
        bitbake("core-image-base")

    def test_no_busybox_base_utils(self):
        config = """
# Enable wayland
DISTRO_FEATURES:append = " pam opengl wayland"

# Switch to systemd
INIT_MANAGER = "systemd"

# Replace busybox
PREFERRED_PROVIDER_virtual/base-utils = "packagegroup-core-base-utils"
VIRTUAL-RUNTIME_base-utils = "packagegroup-core-base-utils"
VIRTUAL-RUNTIME_base-utils-hwclock = "util-linux-hwclock"
VIRTUAL-RUNTIME_base-utils-syslog = ""

# Skip busybox
SKIP_RECIPE[busybox] = "Don't build this"
"""
        self.write_config(config)

        bitbake("--graphviz core-image-weston")

    def test_image_gen_debugfs(self):
        """
        Summary:     Check debugfs generation
        Expected:    1. core-image-minimal can be build with IMAGE_GEN_DEBUGFS variable set
                     2. debug filesystem is created when variable set
                     3. debug symbols available
        Product:     oe-core
        Author:      Humberto Ibarra <humberto.ibarra.lopez@intel.com>
                     Yeoh Ee Peng <ee.peng.yeoh@intel.com>
        """
      
        image = 'core-image-minimal'
        image_fstypes_debugfs = 'tar.bz2'
        features = 'IMAGE_GEN_DEBUGFS = "1"\n'
        features += 'IMAGE_FSTYPES_DEBUGFS = "%s"\n' % image_fstypes_debugfs
        self.write_config(features)

        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)

        dbg_tar_file = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s-dbg.%s" % (bb_vars['IMAGE_LINK_NAME'], image_fstypes_debugfs))
        self.assertTrue(os.path.exists(dbg_tar_file), 'debug filesystem not generated at %s' % dbg_tar_file)
        result = runCmd('cd %s; tar xvf %s' % (bb_vars['DEPLOY_DIR_IMAGE'], dbg_tar_file))
        self.assertEqual(result.status, 0, msg='Failed to extract %s: %s' % (dbg_tar_file, result.output))
        result = runCmd('find %s -name %s' % (bb_vars['DEPLOY_DIR_IMAGE'], "udevadm"))
        self.assertTrue("udevadm" in result.output, msg='Failed to find udevadm: %s' % result.output)
        dbg_symbols_targets = result.output.splitlines()
        self.assertTrue(dbg_symbols_targets, msg='Failed to split udevadm: %s' % dbg_symbols_targets)
        for t in dbg_symbols_targets:
            result = runCmd('objdump --syms %s | grep debug' % t)
            self.assertTrue("debug" in result.output, msg='Failed to find debug symbol: %s' % result.output)

    def test_empty_image(self):
        """Test creation of image with no packages"""
        image = 'test-empty-image'
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)
        manifest = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.manifest" % bb_vars['IMAGE_LINK_NAME'])
        self.assertTrue(os.path.exists(manifest))

        with open(manifest, "r") as f:
                self.assertEqual(len(f.read().strip()),0)

    def test_mandb(self):
        """
        Test that an image containing manpages has working man and apropos commands.
        """
        config = """
DISTRO_FEATURES:append = " api-documentation"
CORE_IMAGE_EXTRA_INSTALL = "man-pages"
"""
        self.write_config(config)
        bitbake("core-image-minimal")

        with runqemu('core-image-minimal', ssh=False, runqemuparams='nographic') as qemu:
            # This manpage is provided by man-pages
            status, output = qemu.run_serial("apropos 8859")
            self.assertEqual(status, 1, 'Failed to run apropos: %s' % (output))
            self.assertIn("iso_8859_15", output)

            # This manpage is provided by man-pages
            status, output = qemu.run_serial("man --pager=cat intro")
            self.assertEqual(status, 1, 'Failed to run man: %s' % (output))
            self.assertIn("introduction to user commands", output)
