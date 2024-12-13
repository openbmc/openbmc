#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu, get_bb_vars
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import skipIfNotMachine

def getline_qemu(out, line):
    for l in out.split('\n'):
        if line in l:
            return l

def getline(res, line):
    return getline_qemu(res.output, line)

class OverlayFSTests(OESelftestTestCase):
    """Overlayfs class usage tests"""

    def add_overlay_conf_to_machine(self):
        machine_inc = """
OVERLAYFS_MOUNT_POINT[mnt-overlay] = "/mnt/overlay"
"""
        self.set_machine_config(machine_inc)

    def test_distro_features_missing(self):
        """
        Summary:   Check that required DISTRO_FEATURES are set
        Expected:  Fail when either systemd or overlayfs are not in DISTRO_FEATURES
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user"
"""
        overlayfs_recipe_append = """
inherit overlayfs
"""
        self.write_config(config)
        self.add_overlay_conf_to_machine()
        self.write_recipeinc('overlayfs-user', overlayfs_recipe_append)

        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "overlayfs-user was skipped: missing required distro features")
        self.assertTrue("overlayfs" in res.output, msg=res.output)
        self.assertTrue("systemd" in res.output, msg=res.output)
        self.assertTrue("ERROR: Required build target 'core-image-minimal' has no buildable providers." in res.output, msg=res.output)

    def test_not_all_units_installed(self):
        """
        Summary:   Test QA check that we have required mount units in the image
        Expected:  Fail because mount unit for overlay partition is not installed
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user"
DISTRO_FEATURES:append = " systemd overlayfs usrmerge"
"""

        self.write_config(config)
        self.add_overlay_conf_to_machine()

        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, " Mount path /mnt/overlay not found in fstab and unit mnt-overlay.mount not found in systemd unit directories")
        self.assertTrue(line and line.startswith("WARNING:"), msg=res.output)
        line = getline(res, "Not all mount paths and units are installed in the image")
        self.assertTrue(line and line.startswith("ERROR:"), msg=res.output)

    def test_not_all_units_installed_but_qa_skipped(self):
        """
        Summary:   Test skipping the QA check
        Expected:  Image is created successfully
        Author:    Claudius Heine <ch@denx.de>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user"
DISTRO_FEATURES:append = " systemd overlayfs usrmerge"
OVERLAYFS_QA_SKIP[mnt-overlay] = "mount-configured"
"""

        self.write_config(config)
        self.add_overlay_conf_to_machine()

        bitbake('core-image-minimal')

    def test_mount_unit_not_set(self):
        """
        Summary:   Test whether mount unit was set properly
        Expected:  Fail because mount unit was not set
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user"
DISTRO_FEATURES:append = " systemd overlayfs usrmerge"
"""

        self.write_config(config)

        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "A recipe uses overlayfs class but there is no OVERLAYFS_MOUNT_POINT set in your MACHINE configuration")
        self.assertTrue(line and line.startswith("Parsing recipes...ERROR:"), msg=res.output)

    def test_wrong_mount_unit_set(self):
        """
        Summary:   Test whether mount unit was set properly
        Expected:  Fail because not the correct flag used for mount unit
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user"
DISTRO_FEATURES:append = " systemd overlayfs usrmerge"
"""

        wrong_machine_config = """
OVERLAYFS_MOUNT_POINT[usr-share-overlay] = "/usr/share/overlay"
"""

        self.write_config(config)
        self.set_machine_config(wrong_machine_config)

        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "Missing required mount point for OVERLAYFS_MOUNT_POINT[mnt-overlay] in your MACHINE configuration")
        self.assertTrue(line and line.startswith("Parsing recipes...ERROR:"), msg=res.output)

    def _test_correct_image(self, recipe, data):
        """
        Summary:   Check that we can create an image when all parameters are
                   set correctly
        Expected:  Image is created successfully
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
IMAGE_INSTALL:append = " overlayfs-user systemd-machine-units"
DISTRO_FEATURES:append = " overlayfs"

# Use systemd as init manager
INIT_MANAGER = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"
"""

        overlayfs_recipe_append = """
OVERLAYFS_WRITABLE_PATHS[mnt-overlay] += "/usr/share/another-overlay-mount"

SYSTEMD_SERVICE:${PN} += " \
    my-application.service \
"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    cat <<EOT > ${D}${systemd_system_unitdir}/my-application.service
[Unit]
Description=Sample application start-up unit
After=overlayfs-user-overlays.service
Requires=overlayfs-user-overlays.service

[Service]
Type=oneshot
ExecStart=/bin/true
RemainAfterExit=true

[Install]
WantedBy=multi-user.target
EOT
}
"""

        self.write_config(config)
        self.add_overlay_conf_to_machine()
        self.write_recipeinc(recipe, data)
        self.write_recipeinc('overlayfs-user', overlayfs_recipe_append)

        bitbake('core-image-minimal')

        with runqemu('core-image-minimal') as qemu:
            # Check that application service started
            status, output = qemu.run_serial("systemctl status my-application")
            self.assertTrue("active (exited)" in output, msg=output)

            # Check that overlay mounts are dependencies of our application unit
            status, output = qemu.run_serial("systemctl list-dependencies my-application")
            self.assertTrue("overlayfs-user-overlays.service" in output, msg=output)

            status, output = qemu.run_serial("systemctl list-dependencies overlayfs-user-overlays")
            self.assertTrue("usr-share-another\\x2doverlay\\x2dmount.mount" in output, msg=output)
            self.assertTrue("usr-share-my\\x2dapplication.mount" in output, msg=output)

            # Check that we have /mnt/overlay fs mounted as tmpfs and
            # /usr/share/my-application as an overlay (see overlayfs-user recipe)
            status, output = qemu.run_serial("/bin/mount -t tmpfs,overlay")

            line = getline_qemu(output, "on /mnt/overlay")
            self.assertTrue(line and line.startswith("tmpfs"), msg=output)

            line = getline_qemu(output, "upperdir=/mnt/overlay/upper/usr/share/my-application")
            self.assertTrue(line and line.startswith("overlay"), msg=output)

            line = getline_qemu(output, "upperdir=/mnt/overlay/upper/usr/share/another-overlay-mount")
            self.assertTrue(line and line.startswith("overlay"), msg=output)

    @OETestTag("runqemu")
    def test_correct_image_fstab(self):
        """
        Summary:   Check that we can create an image when all parameters are
                   set correctly via fstab
        Expected:  Image is created successfully
        Author:    Stefan Herbrechtsmeier <stefan.herbrechtsmeier@weidmueller.com>
        """

        base_files_append = """
do_install:append() {
    cat <<EOT >> ${D}${sysconfdir}/fstab
tmpfs                /mnt/overlay         tmpfs      mode=1777,strictatime,nosuid,nodev  0  0
EOT
}
"""

        self._test_correct_image('base-files', base_files_append)

    @OETestTag("runqemu")
    def test_correct_image_unit(self):
        """
        Summary:   Check that we can create an image when all parameters are
                   set correctly via mount unit
        Expected:  Image is created successfully
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        systemd_machine_unit_append = """
SYSTEMD_SERVICE:${PN} += " \
    mnt-overlay.mount \
"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    cat <<EOT > ${D}${systemd_system_unitdir}/mnt-overlay.mount
[Unit]
Description=Tmpfs directory
DefaultDependencies=no

[Mount]
What=tmpfs
Where=/mnt/overlay
Type=tmpfs
Options=mode=1777,strictatime,nosuid,nodev

[Install]
WantedBy=multi-user.target
EOT
}

"""

        self._test_correct_image('systemd-machine-units', systemd_machine_unit_append)

@OETestTag("runqemu")
class OverlayFSEtcRunTimeTests(OESelftestTestCase):
    """overlayfs-etc class tests"""

    def test_all_required_variables_set(self):
        """
        Summary:   Check that required variables are set
        Expected:  Fail when any of required variables is missing
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        configBase = """
# Use systemd as init manager
INIT_MANAGER = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"

# Image configuration for overlayfs-etc
EXTRA_IMAGE_FEATURES += "overlayfs-etc"
IMAGE_FEATURES:remove = "package-management"
"""
        configMountPoint = """
OVERLAYFS_ETC_MOUNT_POINT = "/data"
"""
        configDevice = """
OVERLAYFS_ETC_DEVICE = "/dev/mmcblk0p1"
"""

        self.write_config(configBase)
        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "OVERLAYFS_ETC_MOUNT_POINT must be set in your MACHINE configuration")
        self.assertTrue(line, msg=res.output)

        self.append_config(configMountPoint)
        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "OVERLAYFS_ETC_DEVICE must be set in your MACHINE configuration")
        self.assertTrue(line, msg=res.output)

        self.append_config(configDevice)
        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "OVERLAYFS_ETC_FSTYPE should contain a valid file system type on /dev/mmcblk0p1")
        self.assertTrue(line, msg=res.output)

    def test_image_feature_conflict(self):
        """
        Summary:   Overlayfs-etc is not allowed to be used with package-management
        Expected:  Feature conflict
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
# Use systemd as init manager
INIT_MANAGER = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"
EXTRA_IMAGE_FEATURES += "overlayfs-etc"
EXTRA_IMAGE_FEATURES += "package-management"
"""

        self.write_config(config)

        res = bitbake('core-image-minimal', ignore_status=True)
        line = getline(res, "contains conflicting IMAGE_FEATURES")
        self.assertTrue("overlayfs-etc" in res.output, msg=res.output)
        self.assertTrue("package-management" in res.output, msg=res.output)

    # https://bugzilla.yoctoproject.org/show_bug.cgi?id=14963
    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_image_feature_is_missing(self):
        """
        Summary:   Overlayfs-etc class is not applied when image feature is not set
        Expected:  Image is created successfully but /etc is not an overlay
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
# Use systemd as init manager
INIT_MANAGER = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"

IMAGE_FSTYPES += "wic"
WKS_FILE = "overlayfs_etc.wks.in"

EXTRA_IMAGE_FEATURES += "read-only-rootfs"
# Image configuration for overlayfs-etc
OVERLAYFS_ETC_MOUNT_POINT = "/data"
OVERLAYFS_ETC_DEVICE = "/dev/sda3"
OVERLAYFS_ROOTFS_TYPE = "ext4"
"""

        self.write_config(config)

        bitbake('core-image-minimal')

        with runqemu('core-image-minimal', image_fstype='wic') as qemu:
            status, output = qemu.run_serial("/bin/mount")

            line = getline_qemu(output, "upperdir=/data/overlay-etc/upper")
            self.assertFalse(line, msg=output)

    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_sbin_init_preinit(self):
        self.run_sbin_init(False, "ext4")

    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_sbin_init_original(self):
        self.run_sbin_init(True, "ext4")

    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_sbin_init_read_only(self):
        self.run_sbin_init(True, "squashfs")

    def run_sbin_init(self, origInit, rootfsType):
        """
        Summary:   Confirm we can replace original init and mount overlay on top of /etc
        Expected:  Image is created successfully and /etc is mounted as an overlay
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = self.get_working_config()

        args = {
            'OVERLAYFS_INIT_OPTION': "" if origInit else "init=/sbin/preinit",
            'OVERLAYFS_ETC_USE_ORIG_INIT_NAME': int(origInit == True),
            'OVERLAYFS_ROOTFS_TYPE': rootfsType,
            'OVERLAYFS_ETC_CREATE_MOUNT_DIRS': int(rootfsType == "ext4")
        }

        self.write_config(config.format(**args))

        bitbake('core-image-minimal')
        testFile = "/etc/my-test-data"

        with runqemu('core-image-minimal', image_fstype='wic', discard_writes=False) as qemu:
            status, output = qemu.run_serial("/bin/mount")

            line = getline_qemu(output, "/dev/sda3")
            self.assertTrue("/data" in output, msg=output)

            line = getline_qemu(output, "upperdir=/data/overlay-etc/upper")
            self.assertTrue(line and line.startswith("/data/overlay-etc/upper on /etc type overlay"), msg=output)

            # check that lower layer is not available
            status, output = qemu.run_serial("ls -1 /data/overlay-etc/lower")
            line = getline_qemu(output, "No such file or directory")
            self.assertTrue(line, msg=output)

            status, output = qemu.run_serial("touch " + testFile)
            status, output = qemu.run_serial("sync")
            status, output = qemu.run_serial("ls -1 " + testFile)
            line = getline_qemu(output, testFile)
            self.assertTrue(line and line.startswith(testFile), msg=output)

        # Check that file exists in /etc after reboot
        with runqemu('core-image-minimal', image_fstype='wic') as qemu:
            status, output = qemu.run_serial("ls -1 " + testFile)
            line = getline_qemu(output, testFile)
            self.assertTrue(line and line.startswith(testFile), msg=output)

    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_lower_layer_access(self):
        """
        Summary:   Test that lower layer of /etc is available read-only when configured
        Expected:  Can't write to lower layer. The files on lower and upper different after
                   modification
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = self.get_working_config()

        configLower = """
OVERLAYFS_ETC_EXPOSE_LOWER = "1"
IMAGE_INSTALL:append = " overlayfs-user"
"""
        testFile = "lower-layer-test.txt"

        args = {
            'OVERLAYFS_INIT_OPTION': "",
            'OVERLAYFS_ETC_USE_ORIG_INIT_NAME': 1,
            'OVERLAYFS_ROOTFS_TYPE': "ext4",
            'OVERLAYFS_ETC_CREATE_MOUNT_DIRS': 1
        }

        self.write_config(config.format(**args))

        self.append_config(configLower)
        bitbake('core-image-minimal')

        with runqemu('core-image-minimal', image_fstype='wic') as qemu:
            status, output = qemu.run_serial("echo \"Modified in upper\" > /etc/" + testFile)
            status, output = qemu.run_serial("diff /etc/" + testFile + " /data/overlay-etc/lower/" + testFile)
            line = getline_qemu(output, "Modified in upper")
            self.assertTrue(line, msg=output)
            line = getline_qemu(output, "Original file")
            self.assertTrue(line, msg=output)

            status, output = qemu.run_serial("touch /data/overlay-etc/lower/ro-test.txt")
            line = getline_qemu(output, "Read-only file system")
            self.assertTrue(line, msg=output)

    @skipIfNotMachine("qemux86-64", "tests are qemux86-64 specific currently")
    def test_postinst_on_target_for_read_only_rootfs(self):
        """
        Summary:  The purpose of this test case is to verify that post-installation
                  on target scripts are executed even if using read-only rootfs when
                  read-only-rootfs-delayed-postinsts is set
        Expected: The test files are created on first boot
        """

        import oe.path

        vars = get_bb_vars(("IMAGE_ROOTFS", "sysconfdir"), "core-image-minimal")
        sysconfdir = vars["sysconfdir"]
        self.assertIsNotNone(sysconfdir)
        # Need to use oe.path here as sysconfdir starts with /
        targettestdir = os.path.join(sysconfdir, "postinst-test")

        config = self.get_working_config()

        args = {
            'OVERLAYFS_INIT_OPTION': "",
            'OVERLAYFS_ETC_USE_ORIG_INIT_NAME': 1,
            'OVERLAYFS_ROOTFS_TYPE': "ext4",
            'OVERLAYFS_ETC_CREATE_MOUNT_DIRS': 1
        }

        # read-only-rootfs is already set in get_working_config()
        config += 'EXTRA_IMAGE_FEATURES += "read-only-rootfs-delayed-postinsts"\n'
        config += 'CORE_IMAGE_EXTRA_INSTALL = "postinst-delayed-b"\n'

        self.write_config(config.format(**args))

        res = bitbake('core-image-minimal')

        with runqemu('core-image-minimal', image_fstype='wic') as qemu:
            for filename in ("rootfs", "delayed-a", "delayed-b"):
                status, output = qemu.run_serial("test -f %s && echo found" % os.path.join(targettestdir, filename))
                self.assertIn("found", output, "%s was not present on boot" % filename)

    def get_working_config(self):
        return """
# Use systemd as init manager
INIT_MANAGER = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " \
    features/overlayfs/overlayfs.scc \
    cfg/fs/squashfs.scc"

IMAGE_FSTYPES += "wic"
OVERLAYFS_INIT_OPTION = "{OVERLAYFS_INIT_OPTION}"
OVERLAYFS_ROOTFS_TYPE = "{OVERLAYFS_ROOTFS_TYPE}"
OVERLAYFS_ETC_CREATE_MOUNT_DIRS = "{OVERLAYFS_ETC_CREATE_MOUNT_DIRS}"
WKS_FILE = "overlayfs_etc.wks.in"

EXTRA_IMAGE_FEATURES += "read-only-rootfs"
# Image configuration for overlayfs-etc
EXTRA_IMAGE_FEATURES += "overlayfs-etc"
IMAGE_FEATURES:remove = "package-management"
OVERLAYFS_ETC_MOUNT_POINT = "/data"
OVERLAYFS_ETC_FSTYPE = "ext4"
OVERLAYFS_ETC_DEVICE = "/dev/sda3"
OVERLAYFS_ETC_USE_ORIG_INIT_NAME = "{OVERLAYFS_ETC_USE_ORIG_INIT_NAME}"

ROOTFS_POSTPROCESS_COMMAND += "{OVERLAYFS_ROOTFS_TYPE}_rootfs"

ext4_rootfs() {{
}}

squashfs_rootfs() {{
    mkdir -p ${{IMAGE_ROOTFS}}/data
}}
"""
