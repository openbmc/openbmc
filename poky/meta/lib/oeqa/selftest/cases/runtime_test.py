from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu
from oeqa.utils.sshcontrol import SSHControl
from oeqa.core.decorator.oeid import OETestID
import os
import re
import tempfile
import shutil

class TestExport(OESelftestTestCase):

    @classmethod
    def tearDownClass(cls):
        runCmd("rm -rf /tmp/sdk")
        super(TestExport, cls).tearDownClass()

    @OETestID(1499)
    def test_testexport_basic(self):
        """
        Summary: Check basic testexport functionality with only ping test enabled.
        Expected: 1. testexport directory must be created.
                  2. runexported.py must run without any error/exception.
                  3. ping test must succeed.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        features = 'INHERIT += "testexport"\n'
        # These aren't the actual IP addresses but testexport class needs something defined
        features += 'TEST_SERVER_IP = "192.168.7.1"\n'
        features += 'TEST_TARGET_IP = "192.168.7.1"\n'
        features += 'TEST_SUITES = "ping"\n'
        self.write_config(features)

        # Build tesexport for core-image-minimal
        bitbake('core-image-minimal')
        bitbake('-c testexport core-image-minimal')

        testexport_dir = get_bb_var('TEST_EXPORT_DIR', 'core-image-minimal')

        # Verify if TEST_EXPORT_DIR was created
        isdir = os.path.isdir(testexport_dir)
        self.assertEqual(True, isdir, 'Failed to create testexport dir: %s' % testexport_dir)

        with runqemu('core-image-minimal') as qemu:
            # Attempt to run runexported.py to perform ping test
            test_path = os.path.join(testexport_dir, "oe-test")
            data_file = os.path.join(testexport_dir, 'data', 'testdata.json')
            manifest = os.path.join(testexport_dir, 'data', 'manifest')
            cmd = ("%s runtime --test-data-file %s --packages-manifest %s "
                   "--target-ip %s --server-ip %s --quiet"
                  % (test_path, data_file, manifest, qemu.ip, qemu.server_ip))
            result = runCmd(cmd)
            # Verify ping test was succesful
            self.assertEqual(0, result.status, 'oe-test runtime returned a non 0 status')

    @OETestID(1641)
    def test_testexport_sdk(self):
        """
        Summary: Check sdk functionality for testexport.
        Expected: 1. testexport directory must be created.
                  2. SDK tarball must exists.
                  3. Uncompressing of tarball must succeed.
                  4. Check if the SDK directory is added to PATH.
                  5. Run tar from the SDK directory.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        features = 'INHERIT += "testexport"\n'
        # These aren't the actual IP addresses but testexport class needs something defined
        features += 'TEST_SERVER_IP = "192.168.7.1"\n'
        features += 'TEST_TARGET_IP = "192.168.7.1"\n'
        features += 'TEST_SUITES = "ping"\n'
        features += 'TEST_EXPORT_SDK_ENABLED = "1"\n'
        features += 'TEST_EXPORT_SDK_PACKAGES = "nativesdk-tar"\n'
        self.write_config(features)

        # Build tesexport for core-image-minimal
        bitbake('core-image-minimal')
        bitbake('-c testexport core-image-minimal')

        needed_vars = ['TEST_EXPORT_DIR', 'TEST_EXPORT_SDK_DIR', 'TEST_EXPORT_SDK_NAME']
        bb_vars = get_bb_vars(needed_vars, 'core-image-minimal')
        testexport_dir = bb_vars['TEST_EXPORT_DIR']
        sdk_dir = bb_vars['TEST_EXPORT_SDK_DIR']
        sdk_name = bb_vars['TEST_EXPORT_SDK_NAME']

        # Check for SDK
        tarball_name = "%s.sh" % sdk_name
        tarball_path = os.path.join(testexport_dir, sdk_dir, tarball_name)
        msg = "Couldn't find SDK tarball: %s" % tarball_path
        self.assertEqual(os.path.isfile(tarball_path), True, msg)

        # Extract SDK and run tar from SDK
        result = runCmd("%s -y -d /tmp/sdk" % tarball_path)
        self.assertEqual(0, result.status, "Couldn't extract SDK")

        env_script = result.output.split()[-1]
        result = runCmd(". %s; which tar" % env_script, shell=True)
        self.assertEqual(0, result.status, "Couldn't setup SDK environment")
        is_sdk_tar = True if "/tmp/sdk" in result.output else False
        self.assertTrue(is_sdk_tar, "Couldn't setup SDK environment")

        tar_sdk = result.output
        result = runCmd("%s --version" % tar_sdk)
        self.assertEqual(0, result.status, "Couldn't run tar from SDK")


class TestImage(OESelftestTestCase):

    @OETestID(1644)
    def test_testimage_install(self):
        """
        Summary: Check install packages functionality for testimage/testexport.
        Expected: 1. Import tests from a directory other than meta.
                  2. Check install/uninstall of socat.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """
        if get_bb_var('DISTRO') == 'poky-tiny':
            self.skipTest('core-image-full-cmdline not buildable for poky-tiny')

        features = 'INHERIT += "testimage"\n'
        features += 'IMAGE_INSTALL_append = " libssl"\n'
        features += 'TEST_SUITES = "ping ssh selftest"\n'
        self.write_config(features)

        # Build core-image-sato and testimage
        bitbake('core-image-full-cmdline socat')
        bitbake('-c testimage core-image-full-cmdline')

    @OETestID(1883)
    def test_testimage_dnf(self):
        """
        Summary: Check package feeds functionality for dnf
        Expected: 1. Check that remote package feeds can be accessed
        Product: oe-core
        Author: Alexander Kanavin <alex.kanavin@gmail.com>
        """
        if get_bb_var('DISTRO') == 'poky-tiny':
            self.skipTest('core-image-full-cmdline not buildable for poky-tiny')

        features = 'INHERIT += "testimage"\n'
        features += 'TEST_SUITES = "ping ssh dnf_runtime dnf.DnfBasicTest.test_dnf_help"\n'
        # We don't yet know what the server ip and port will be - they will be patched
        # in at the start of the on-image test
        features += 'PACKAGE_FEED_URIS = "http://bogus_ip:bogus_port"\n'
        features += 'EXTRA_IMAGE_FEATURES += "package-management"\n'
        features += 'PACKAGE_CLASSES = "package_rpm"\n'

        bitbake('gnupg-native -c addto_recipe_sysroot')

        # Enable package feed signing
        self.gpg_home = tempfile.mkdtemp(prefix="oeqa-feed-sign-")
        signing_key_dir = os.path.join(self.testlayer_path, 'files', 'signing')
        runCmd('gpg --batch --homedir %s --import %s' % (self.gpg_home, os.path.join(signing_key_dir, 'key.secret')), native_sysroot=get_bb_var("RECIPE_SYSROOT_NATIVE", "gnupg-native"))
        features += 'INHERIT += "sign_package_feed"\n'
        features += 'PACKAGE_FEED_GPG_NAME = "testuser"\n'
        features += 'PACKAGE_FEED_GPG_PASSPHRASE_FILE = "%s"\n' % os.path.join(signing_key_dir, 'key.passphrase')
        features += 'GPG_PATH = "%s"\n' % self.gpg_home
        self.write_config(features)

        # Build core-image-sato and testimage
        bitbake('core-image-full-cmdline socat')
        bitbake('-c testimage core-image-full-cmdline')

        # remove the oeqa-feed-sign temporal directory
        shutil.rmtree(self.gpg_home, ignore_errors=True)

class Postinst(OESelftestTestCase):
    @OETestID(1540)
    @OETestID(1545)
    def test_postinst_rootfs_and_boot(self):
        """
        Summary:        The purpose of this test case is to verify Post-installation
                        scripts are called when rootfs is created and also test
                        that script can be delayed to run at first boot.
        Dependencies:   NA
        Steps:          1. Add proper configuration to local.conf file
                        2. Build a "core-image-minimal" image
                        3. Verify that file created by postinst_rootfs recipe is
                           present on rootfs dir.
                        4. Boot the image created on qemu and verify that the file
                           created by postinst_boot recipe is present on image.
        Expected:       The files are successfully created during rootfs and boot
                        time for 3 different package managers: rpm,ipk,deb and
                        for initialization managers: sysvinit and systemd.

        """

        import oe.path

        vars = get_bb_vars(("IMAGE_ROOTFS", "sysconfdir"), "core-image-minimal")
        rootfs = vars["IMAGE_ROOTFS"]
        self.assertIsNotNone(rootfs)
        sysconfdir = vars["sysconfdir"]
        self.assertIsNotNone(sysconfdir)
        # Need to use oe.path here as sysconfdir starts with /
        hosttestdir = oe.path.join(rootfs, sysconfdir, "postinst-test")
        targettestdir = os.path.join(sysconfdir, "postinst-test")

        for init_manager in ("sysvinit", "systemd"):
            for classes in ("package_rpm", "package_deb", "package_ipk"):
                with self.subTest(init_manager=init_manager, package_class=classes):
                    features = 'CORE_IMAGE_EXTRA_INSTALL = "postinst-delayed-b"\n'
                    features += 'IMAGE_FEATURES += "package-management empty-root-password"\n'
                    features += 'PACKAGE_CLASSES = "%s"\n' % classes
                    if init_manager == "systemd":
                        features += 'DISTRO_FEATURES_append = " systemd"\n'
                        features += 'VIRTUAL-RUNTIME_init_manager = "systemd"\n'
                        features += 'DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"\n'
                        features += 'VIRTUAL-RUNTIME_initscripts = ""\n'
                    self.write_config(features)

                    bitbake('core-image-minimal')

                    self.assertTrue(os.path.isfile(os.path.join(hosttestdir, "rootfs")),
                                    "rootfs state file was not created")

                    with runqemu('core-image-minimal') as qemu:
                        # Make the test echo a string and search for that as
                        # run_serial()'s status code is useless.'
                        for filename in ("rootfs", "delayed-a", "delayed-b"):
                            status, output = qemu.run_serial("test -f %s && echo found" % os.path.join(targettestdir, filename))
                            self.assertEqual(output, "found", "%s was not present on boot" % filename)



    def test_failing_postinst(self):
        """
        Summary:        The purpose of this test case is to verify that post-installation
                        scripts that contain errors are properly reported.
        Expected:       The scriptlet failure is properly reported.
                        The file that is created after the error in the scriptlet is not present.
        Product: oe-core
        Author: Alexander Kanavin <alex.kanavin@gmail.com>
        """

        import oe.path

        vars = get_bb_vars(("IMAGE_ROOTFS", "sysconfdir"), "core-image-minimal")
        rootfs = vars["IMAGE_ROOTFS"]
        self.assertIsNotNone(rootfs)
        sysconfdir = vars["sysconfdir"]
        self.assertIsNotNone(sysconfdir)
        # Need to use oe.path here as sysconfdir starts with /
        hosttestdir = oe.path.join(rootfs, sysconfdir, "postinst-test")

        for classes in ("package_rpm", "package_deb", "package_ipk"):
            with self.subTest(package_class=classes):
                features = 'CORE_IMAGE_EXTRA_INSTALL = "postinst-rootfs-failing"\n'
                features += 'PACKAGE_CLASSES = "%s"\n' % classes
                self.write_config(features)
                bb_result = bitbake('core-image-minimal', ignore_status=True)
                self.assertGreaterEqual(bb_result.output.find("Postinstall scriptlets of ['postinst-rootfs-failing'] have failed."), 0,
                    "Warning about a failed scriptlet not found in bitbake output: %s" %(bb_result.output))

                self.assertTrue(os.path.isfile(os.path.join(hosttestdir, "rootfs-before-failure")),
                                    "rootfs-before-failure file was not created")
                self.assertFalse(os.path.isfile(os.path.join(hosttestdir, "rootfs-after-failure")),
                                    "rootfs-after-failure file was created")

