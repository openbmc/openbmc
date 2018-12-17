from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
from oeqa.core.decorator.oeid import OETestID
from oeqa.utils.sshcontrol import SSHControl
import os
import json

class ImageFeatures(OESelftestTestCase):

    test_user = 'tester'
    root_user = 'root'

    @OETestID(1107)
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

    @OETestID(1115)
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


    @OETestID(1116)
    def test_clutter_image_can_be_built(self):
        """
        Summary:     Check if clutter image can be built
        Expected:    1. core-image-clutter can be built
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        # Build a core-image-clutter
        bitbake('core-image-clutter')

    @OETestID(1117)
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

    @OETestID(1497)
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

        image_name = 'core-image-minimal'
        bitbake(image_name)

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        link_name = get_bb_var('IMAGE_LINK_NAME', image_name)
        image_path = os.path.join(deploy_dir_image, "%s.ext4" % link_name)
        bmap_path = "%s.bmap" % image_path
        gzip_path = "%s.gz" % bmap_path

        # check if result image, bmap and bmap.gz files are in deploy directory
        self.assertTrue(os.path.exists(image_path))
        self.assertTrue(os.path.exists(bmap_path))
        self.assertTrue(os.path.exists(gzip_path))

        # check if result image is sparse
        image_stat = os.stat(image_path)
        self.assertTrue(image_stat.st_size > image_stat.st_blocks * 512)

        # check if the resulting gzip is valid
        self.assertTrue(runCmd('gzip -t %s' % gzip_path))

    @OETestID(1903)
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
            features += 'IMAGE_FSTYPES += "wic.%s"\n' % itype
        self.write_config(features)

        image_name = 'core-image-minimal'
        bitbake(image_name)

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        link_name = get_bb_var('IMAGE_LINK_NAME', image_name)
        for itype in img_types:
            image_path = os.path.join(deploy_dir_image, "%s.wic.%s" %
                                      (link_name, itype))

            # check if result image file is in deploy directory
            self.assertTrue(os.path.exists(image_path))

            # check if result image is vmdk
            sysroot = get_bb_var('STAGING_DIR_NATIVE', 'core-image-minimal')
            result = runCmd('qemu-img info --output json %s' % image_path,
                            native_sysroot=sysroot)
            self.assertTrue(json.loads(result.output).get('format') == itype)

    @OETestID(1905)
    def test_long_chain_conversion(self):
        """
        Summary:     Check for chaining many CONVERSION_CMDs together
        Expected:    1. core-image-minimal can be built with
                        ext4.bmap.gz.bz2.lzo.xz.u-boot and also create a
                        sha256sum
                     2. The above image has a valid sha256sum
        Product:     oe-core
        Author:      Tom Rini <trini@konsulko.com>
        """

        conv = "ext4.bmap.gz.bz2.lzo.xz.u-boot"
        features = 'IMAGE_FSTYPES += "%s %s.sha256sum"' % (conv, conv)
        self.write_config(features)

        image_name = 'core-image-minimal'
        bitbake(image_name)

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        link_name = get_bb_var('IMAGE_LINK_NAME', image_name)
        image_path = os.path.join(deploy_dir_image, "%s.%s" %
                                  (link_name, conv))

        # check if resulting image is in the deploy directory
        self.assertTrue(os.path.exists(image_path))
        self.assertTrue(os.path.exists(image_path + ".sha256sum"))

        # check if the resulting sha256sum agrees
        self.assertTrue(runCmd('cd %s;sha256sum -c %s.%s.sha256sum' %
                               (deploy_dir_image, link_name, conv)))

    @OETestID(1904)
    def test_image_fstypes(self):
        """
        Summary:     Check if image of supported image fstypes can be built
        Expected:    core-image-minimal can be built for various image types
        Product:     oe-core
        Author:      Ed Bartosh <ed.bartosh@linux.intel.com>
        """
        image_name = 'core-image-minimal'

        img_types = [itype for itype in get_bb_var("IMAGE_TYPES", image_name).split() \
                         if itype not in ('container', 'elf', 'f2fs', 'multiubi')]

        config = 'IMAGE_FSTYPES += "%s"\n'\
                 'MKUBIFS_ARGS ?= "-m 2048 -e 129024 -c 2047"\n'\
                 'UBINIZE_ARGS ?= "-m 2048 -p 128KiB -s 512"' % ' '.join(img_types)

        self.write_config(config)

        bitbake(image_name)

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        link_name = get_bb_var('IMAGE_LINK_NAME', image_name)
        for itype in img_types:
            image_path = os.path.join(deploy_dir_image, "%s.%s" % (link_name, itype))
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
