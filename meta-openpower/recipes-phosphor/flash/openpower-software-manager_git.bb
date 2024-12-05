SUMMARY = "OpenPower Software Management"
DESCRIPTION = "OpenPower Software Manager provides a set of host software \
management daemons. It is suitable for use on a wide variety of OpenPower \
platforms."
HOMEPAGE = "https://github.com/openbmc/openpower-pnor-code-mgmt"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson pkgconfig systemd
inherit obmc-phosphor-dbus-service

# Static configuration. This is the default if no other layout is specified.
inherit ${@bb.utils.contains_any('DISTRO_FEATURES', \
        'openpower-ubi-fs phosphor-mmc', \
        '', \
        'openpower-software-manager-static', d)}

# UBI layout
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'openpower-ubi-fs', \
                             'openpower-software-manager-ubi', \
                             '', d)}
# eMMC layout
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-mmc', \
                             'openpower-software-manager-mmc', \
                             '', d)}

# Virtual PNOR
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'openpower-virtual-pnor', \
                             'openpower-software-manager-virtual-pnor', \
                             '', d)}

# PLDM Support
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'openpower-hostfw-over-pldm', \
                             'openpower-software-manager-pldm', \
                             '', d)}

PACKAGECONFIG[verify_pnor_signature] = "-Dverify-signature=enabled, -Dverify-signature=disabled"
PACKAGECONFIG[ubifs_layout] = "-Ddevice-type=ubi,,,mtd-utils-ubifs"
PACKAGECONFIG[mmc_layout] = "-Ddevice-type=mmc"
PACKAGECONFIG[virtual_pnor] = "-Dvpnor=enabled, -Dvpnor=disabled,,bash"
PACKAGECONFIG[pldm] = "-Dpldm=enabled, -Dpldm=disabled"

EXTRA_OEMESON += " \
    -Dtests=disabled \
    -Dmsl="v2.0.10 v2.2" \
    "

DEPENDS += " \
        cli11 \
        dbus \
        nlohmann-json \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        "

RDEPENDS:${PN} += " \
        virtual-obmc-image-manager \
        "

FILES:${PN} += "${datadir}/dbus-1/system.d/org.open_power.Software.Host.Updater.conf"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/openbmc/openpower-pnor-code-mgmt;branch=master;protocol=https"

SRCREV = "4302aa5734f487a5566e8e60efa2a939b84242ab"

DBUS_SERVICE:${PN} += "org.open_power.Software.Host.Updater.service"

SYSTEMD_SERVICE:${PN} += " \
        op-pnor-msl.service \
        "
