#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu
from oeqa.utils.sshcontrol import SSHControl
import os
import re
import tempfile
import shutil
import oe.lsb
from oeqa.core.decorator.data import skipIfNotQemu

class TestExport(OESelftestTestCase):

    @classmethod
    def tearDownClass(cls):
        runCmd("rm -rf /tmp/sdk")
        super(TestExport, cls).tearDownClass()

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
        self.track_for_cleanup(self.gpg_home)
        signing_key_dir = os.path.join(self.testlayer_path, 'files', 'signing')
        runCmd('gpgconf --list-dirs --homedir %s; gpg -v --batch --homedir %s --import %s' % (self.gpg_home, self.gpg_home, os.path.join(signing_key_dir, 'key.secret')), native_sysroot=get_bb_var("RECIPE_SYSROOT_NATIVE", "gnupg-native"), shell=True)
        features += 'INHERIT += "sign_package_feed"\n'
        features += 'PACKAGE_FEED_GPG_NAME = "testuser"\n'
        features += 'PACKAGE_FEED_GPG_PASSPHRASE_FILE = "%s"\n' % os.path.join(signing_key_dir, 'key.passphrase')
        features += 'GPG_PATH = "%s"\n' % self.gpg_home
        self.write_config(features)

        # Build core-image-sato and testimage
        bitbake('core-image-full-cmdline socat')
        bitbake('-c testimage core-image-full-cmdline')

    def test_testimage_virgl_gtk_sdl(self):
        """
        Summary: Check host-assisted accelerate OpenGL functionality in qemu with gtk and SDL frontends
        Expected: 1. Check that virgl kernel driver is loaded and 3d acceleration is enabled
                  2. Check that kmscube demo runs without crashing.
        Product: oe-core
        Author: Alexander Kanavin <alex.kanavin@gmail.com>
        """
        if "DISPLAY" not in os.environ:
            self.skipTest("virgl gtk test must be run inside a X session")
        distro = oe.lsb.distro_identifier()
        if distro and distro == 'debian-8':
            self.skipTest('virgl isn\'t working with Debian 8')
        if distro and distro == 'centos-7':
            self.skipTest('virgl isn\'t working with Centos 7')
        if distro and distro == 'opensuseleap-15.0':
            self.skipTest('virgl isn\'t working with Opensuse 15.0')

        qemu_packageconfig = get_bb_var('PACKAGECONFIG', 'qemu-system-native')
        sdl_packageconfig = get_bb_var('PACKAGECONFIG', 'libsdl2-native')
        features = 'INHERIT += "testimage"\n'
        if 'gtk+' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " gtk+"\n'
        if 'sdl' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " sdl"\n'
        if 'virglrenderer' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " virglrenderer"\n'
        if 'glx' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " glx"\n'
        if 'opengl' not in sdl_packageconfig:
            features += 'PACKAGECONFIG_append_pn-libsdl2-native = " opengl"\n'
        features += 'TEST_SUITES = "ping ssh virgl"\n'
        features += 'IMAGE_FEATURES_append = " ssh-server-dropbear"\n'
        features += 'IMAGE_INSTALL_append = " kmscube"\n'
        features_gtk = features + 'TEST_RUNQEMUPARAMS = "gtk gl"\n'
        self.write_config(features_gtk)
        bitbake('core-image-minimal')
        bitbake('-c testimage core-image-minimal')
        features_sdl = features + 'TEST_RUNQEMUPARAMS = "sdl gl"\n'
        self.write_config(features_sdl)
        bitbake('core-image-minimal')
        bitbake('-c testimage core-image-minimal')

    def test_testimage_virgl_headless(self):
        """
        Summary: Check host-assisted accelerate OpenGL functionality in qemu with egl-headless frontend
        Expected: 1. Check that virgl kernel driver is loaded and 3d acceleration is enabled
                  2. Check that kmscube demo runs without crashing.
        Product: oe-core
        Author: Alexander Kanavin <alex.kanavin@gmail.com>
        """
        import subprocess, os
        try:
            content = os.listdir("/dev/dri")
            if len([i for i in content if i.startswith('render')]) == 0:
                self.skipTest("No render nodes found in /dev/dri: %s" %(content))
        except FileNotFoundError:
            self.skipTest("/dev/dri directory does not exist; no render nodes available on this machine.")
        try:
            dripath = subprocess.check_output("pkg-config --variable=dridriverdir dri", shell=True)
        except subprocess.CalledProcessError as e:
            self.skipTest("Could not determine the path to dri drivers on the host via pkg-config.\nPlease install Mesa development files (particularly, dri.pc) on the host machine.")
        qemu_packageconfig = get_bb_var('PACKAGECONFIG', 'qemu-system-native')
        features = 'INHERIT += "testimage"\n'
        if 'virglrenderer' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " virglrenderer"\n'
        if 'glx' not in qemu_packageconfig:
            features += 'PACKAGECONFIG_append_pn-qemu-system-native = " glx"\n'
        features += 'TEST_SUITES = "ping ssh virgl"\n'
        features += 'IMAGE_FEATURES_append = " ssh-server-dropbear"\n'
        features += 'IMAGE_INSTALL_append = " kmscube"\n'
        features += 'TEST_RUNQEMUPARAMS = "egl-headless"\n'
        self.write_config(features)
        bitbake('core-image-minimal')
        bitbake('-c testimage core-image-minimal')

class Postinst(OESelftestTestCase):

    def init_manager_loop(self, init_manager):
        import oe.path

        vars = get_bb_vars(("IMAGE_ROOTFS", "sysconfdir"), "core-image-minimal")
        rootfs = vars["IMAGE_ROOTFS"]
        self.assertIsNotNone(rootfs)
        sysconfdir = vars["sysconfdir"]
        self.assertIsNotNone(sysconfdir)
        # Need to use oe.path here as sysconfdir starts with /
        hosttestdir = oe.path.join(rootfs, sysconfdir, "postinst-test")
        targettestdir = os.path.join(sysconfdir, "postinst-test")

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



    @skipIfNotQemu('qemuall', 'Test only runs in qemu')
    def test_postinst_rootfs_and_boot_sysvinit(self):
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
                        for initialization managers: sysvinit.

        """
        self.init_manager_loop("sysvinit")


    @skipIfNotQemu('qemuall', 'Test only runs in qemu')
    def test_postinst_rootfs_and_boot_systemd(self):
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
                        for initialization managers: systemd.

        """

        self.init_manager_loop("systemd")


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

class SystemTap(OESelftestTestCase):
        """
        Summary:        The purpose of this test case is to verify native crosstap
                        works while talking to a target.
        Expected:       The script should successfully connect to the qemu machine
                        and run some systemtap examples on a qemu machine.
        """

        @classmethod
        def setUpClass(cls):
            super(SystemTap, cls).setUpClass()
            cls.image = "core-image-minimal"

        def default_config(self):
            return """
# These aren't the actual IP addresses but testexport class needs something defined
TEST_SERVER_IP = "192.168.7.1"
TEST_TARGET_IP = "192.168.7.2"

EXTRA_IMAGE_FEATURES += "tools-profile dbg-pkgs"
IMAGE_FEATURES_append = " ssh-server-dropbear"

# enables kernel debug symbols
KERNEL_EXTRA_FEATURES_append = " features/debug/debug-kernel.scc"
KERNEL_EXTRA_FEATURES_append = " features/systemtap/systemtap.scc"

# add systemtap run-time into target image if it is not there yet
IMAGE_INSTALL_append = " systemtap"
"""

        def test_crosstap_helloworld(self):
            self.write_config(self.default_config())
            bitbake('systemtap-native')
            systemtap_examples = os.path.join(get_bb_var("WORKDIR","systemtap-native"), "usr/share/systemtap/examples")
            bitbake(self.image)

            with runqemu(self.image) as qemu:
                cmd = "crosstap -r root@192.168.7.2 -s %s/general/helloworld.stp " % systemtap_examples 
                result = runCmd(cmd)
                self.assertEqual(0, result.status, 'crosstap helloworld returned a non 0 status:%s' % result.output)

        def test_crosstap_pstree(self):
            self.write_config(self.default_config())

            bitbake('systemtap-native')
            systemtap_examples = os.path.join(get_bb_var("WORKDIR","systemtap-native"), "usr/share/systemtap/examples")
            bitbake(self.image)

            with runqemu(self.image) as qemu:
                cmd = "crosstap -r root@192.168.7.2 -s %s/process/pstree.stp" % systemtap_examples
                result = runCmd(cmd)
                self.assertEqual(0, result.status, 'crosstap pstree returned a non 0 status:%s' % result.output)

        def test_crosstap_syscalls_by_proc(self):
            self.write_config(self.default_config())

            bitbake('systemtap-native')
            systemtap_examples = os.path.join(get_bb_var("WORKDIR","systemtap-native"), "usr/share/systemtap/examples")
            bitbake(self.image)

            with runqemu(self.image) as qemu:
                cmd = "crosstap -r root@192.168.7.2 -s %s/process/ syscalls_by_proc.stp" % systemtap_examples
                result = runCmd(cmd)
                self.assertEqual(0, result.status, 'crosstap  syscalls_by_proc returned a non 0 status:%s' % result.output)

        def test_crosstap_syscalls_by_pid(self):
            self.write_config(self.default_config())

            bitbake('systemtap-native')
            systemtap_examples = os.path.join(get_bb_var("WORKDIR","systemtap-native"), "usr/share/systemtap/examples")
            bitbake(self.image)

            with runqemu(self.image) as qemu:
                cmd = "crosstap -r root@192.168.7.2 -s %s/process/ syscalls_by_pid.stp" % systemtap_examples
                result = runCmd(cmd)
                self.assertEqual(0, result.status, 'crosstap  syscalls_by_pid returned a non 0 status:%s' % result.output)

