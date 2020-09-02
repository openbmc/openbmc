SUMMARY = "OpenPower Software Management"
DESCRIPTION = "OpenPower Software Manager provides a set of host software \
management daemons. It is suitable for use on a wide variety of OpenPower \
platforms."
HOMEPAGE = "https://github.com/openbmc/openpower-pnor-code-mgmt"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig systemd
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

PACKAGECONFIG[verify_pnor_signature] = "--enable-verify_pnor_signature,--disable-verify_pnor_signature"
PACKAGECONFIG[ubifs_layout] = "--enable-ubifs_layout,--disable-ubifs_layout,,mtd-utils-ubifs"
PACKAGECONFIG[mmc_layout] = "--enable-mmc_layout,--disable-mmc_layout"
PACKAGECONFIG[virtual_pnor] = "--enable-virtual_pnor,--disable-virtual_pnor"

EXTRA_OECONF += " \
    PNOR_MSL="v2.0.10 v2.2" \
    "

DEPENDS += " \
        autoconf-archive-native \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        "

RDEPENDS_${PN} += " \
        virtual-obmc-image-manager \
        "

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/openpower-pnor-code-mgmt"

SRCREV = "f6608a826007e39f1ba1aa595833dc562b3f2799"

DBUS_SERVICE_${PN} += "org.open_power.Software.Host.Updater.service"

SYSTEMD_SERVICE_${PN} += " \
        op-pnor-msl.service \
        "
