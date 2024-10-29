DESCRIPTION = "Image with Phosphor, a software stack for hardware management \
in devices with baseboard management controllers.  The image supports the \
full OpenBMC feature set for devices of all types."
LICENSE = "Apache-2.0"

inherit obmc-phosphor-image

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version ; "

IMAGE_LINGUAS = ""
IMAGE_FEATURES += " \
        obmc-bmc-state-mgmt \
        obmc-bmcweb \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-console \
        obmc-devtools \
        obmc-fan-control \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-health-monitor \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-ikvm \
        obmc-inventory \
        obmc-leds \
        obmc-logging-mgmt \
        obmc-remote-logging-mgmt \
        obmc-net-ipmi \
        obmc-sensors \
        obmc-software \
        obmc-system-mgmt \
        obmc-user-mgmt \
        obmc-user-mgmt-ldap \
        ${@bb.utils.contains_any('DISTRO_FEATURES', \
            'obmc-ubi-fs phosphor-mmc obmc-static-norootfs', \
            'read-only-rootfs overlayfs-etc', '', d)} \
        ssh-server-dropbear \
        obmc-debug-collector \
        obmc-network-mgmt \
        obmc-settings-mgmt \
        obmc-telemetry \
        obmc-dmtf-pmci \
        obmc-webui \
        "
# The shadow recipe provides the binaries(like useradd, usermod) needed by the
# phosphor-user-manager.
ROOTFS_RO_UNNEEDED:remove = "shadow"

# We need to set overlayfs-etc so that the dropbear/openssh keys don't end up
# in a volatile file system, but we always have our own init that sets these
# up.  Add enough bogus values here that rootfs-postcommands.bbclass does what
# we want without overlayfs-etc.bbclass messing things up.
OVERLAYFS_ETC_USE_ORIG_INIT_NAME="0"
OVERLAYFS_ETC_MOUNT_POINT = "/this/is/unused"
OVERLAYFS_ETC_FSTYPE = "not_a_fs_type"
OVERLAYFS_ETC_DEVICE = "/dev/null"
python create_overlayfs_etc_preinit:append() {
    os.unlink(preinitPath)
}
