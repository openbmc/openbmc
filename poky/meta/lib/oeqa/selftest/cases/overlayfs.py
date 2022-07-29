#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runqemu
from oeqa.core.decorator import OETestTag

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
DISTRO_FEATURES:append = " systemd overlayfs"
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
DISTRO_FEATURES += "systemd overlayfs"
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
DISTRO_FEATURES:append = " systemd overlayfs"
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
DISTRO_FEATURES:append = " systemd overlayfs"
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
DISTRO_FEATURES:append = " systemd overlayfs"

# Use systemd as init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

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
DISTRO_FEATURES:append = " systemd"

# Use systemd as init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

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
DISTRO_FEATURES:append = " systemd"

# Use systemd as init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

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

    def test_image_feature_is_missing_class_included(self):
        configAppend = """
INHERIT += "overlayfs-etc"
"""
        self.run_check_image_feature(configAppend)

    def test_image_feature_is_missing(self):
        self.run_check_image_feature()

    def run_check_image_feature(self, appendToConfig=""):
        """
        Summary:   Overlayfs-etc class is not applied when image feature is not set
                   even if we inherit it directly,
        Expected:  Image is created successfully but /etc is not an overlay
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = f"""
DISTRO_FEATURES:append = " systemd"

# Use systemd as init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"

IMAGE_FSTYPES += "wic"
WKS_FILE = "overlayfs_etc.wks.in"

EXTRA_IMAGE_FEATURES += "read-only-rootfs"
# Image configuration for overlayfs-etc
OVERLAYFS_ETC_MOUNT_POINT = "/data"
OVERLAYFS_ETC_DEVICE = "/dev/sda3"
{appendToConfig}
"""

        self.write_config(config)

        bitbake('core-image-minimal')

        with runqemu('core-image-minimal', image_fstype='wic') as qemu:
            status, output = qemu.run_serial("/bin/mount")

            line = getline_qemu(output, "upperdir=/data/overlay-etc/upper")
            self.assertFalse(line, msg=output)

    def test_sbin_init_preinit(self):
        self.run_sbin_init(False)

    def test_sbin_init_original(self):
        self.run_sbin_init(True)

    def run_sbin_init(self, origInit):
        """
        Summary:   Confirm we can replace original init and mount overlay on top of /etc
        Expected:  Image is created successfully and /etc is mounted as an overlay
        Author:    Vyacheslav Yurkov <uvv.mail@gmail.com>
        """

        config = """
DISTRO_FEATURES:append = " systemd"

# Use systemd as init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

# enable overlayfs in the kernel
KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"

IMAGE_FSTYPES += "wic"
OVERLAYFS_INIT_OPTION = "{OVERLAYFS_INIT_OPTION}"
WKS_FILE = "overlayfs_etc.wks.in"

EXTRA_IMAGE_FEATURES += "read-only-rootfs"
# Image configuration for overlayfs-etc
EXTRA_IMAGE_FEATURES += "overlayfs-etc"
IMAGE_FEATURES:remove = "package-management"
OVERLAYFS_ETC_MOUNT_POINT = "/data"
OVERLAYFS_ETC_FSTYPE = "ext4"
OVERLAYFS_ETC_DEVICE = "/dev/sda3"
OVERLAYFS_ETC_USE_ORIG_INIT_NAME = "{OVERLAYFS_ETC_USE_ORIG_INIT_NAME}"
"""

        args = {
            'OVERLAYFS_INIT_OPTION': "" if origInit else "init=/sbin/preinit",
            'OVERLAYFS_ETC_USE_ORIG_INIT_NAME': int(origInit == True)
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
