DESCRIPTION = "Image with Phosphor, a software stack for hardware management \
in devices with baseboard management controllers.  The image supports the \
full OpenBMC feature set for devices of all types."

IMAGE_LINGUAS = ""

IMAGE_FEATURES += " \
        obmc-bmc-state-mgmt \
        obmc-bmcweb \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-console \
        obmc-dbus-monitor \
        obmc-devtools \
        obmc-fan-control \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-fru-ipmi \
        obmc-health-monitor \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-ikvm \
        obmc-inventory \
        obmc-leds \
        obmc-logging-mgmt \
        obmc-remote-logging-mgmt \
        obmc-rng \
        obmc-net-ipmi \
        obmc-sensors \
        obmc-software \
        obmc-system-mgmt \
        obmc-user-mgmt \
        obmc-user-mgmt-ldap \
        ${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-fs', 'read-only-rootfs', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-mmc', 'read-only-rootfs', '', d)} \
        ssh-server-dropbear \
        obmc-debug-collector \
        obmc-network-mgmt \
        obmc-settings-mgmt \
        obmc-telemetry \
        "

LICENSE = "Apache-2.0"

inherit obmc-phosphor-image

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version ; "

# The shadow recipe provides the binaries(like useradd, usermod) needed by the
# phosphor-user-manager.
ROOTFS_RO_UNNEEDED_remove = "shadow"
