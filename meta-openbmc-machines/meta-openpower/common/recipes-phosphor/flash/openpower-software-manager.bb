SUMMARY = "OpenPower Software Management"
DESCRIPTION = "OpenPower Software Manager provides a set of host software \
management daemons. It is suitable for use on a wide variety of OpenPower \
platforms."
HOMEPAGE = "https://github.com/openbmc/openpower-pnor-code-mgmt"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += " \
        autoconf-archive-native \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        "

RDEPENDS_${PN} += " \
        mtd-utils-ubifs \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        virtual-obmc-image-manager \
        "

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/openpower-pnor-code-mgmt"

SRC_URI += "file://obmc-flash-bios"

SRCREV = "ef8cd9f6777b4db0eb5fac8ec0c215d6aeb9716a"

do_install_append() {
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/obmc-flash-bios ${D}${sbindir}/obmc-flash-bios
}

DBUS_SERVICE_${PN} += "org.open_power.Software.Host.Updater.service"

SYSTEMD_SERVICE_${PN} += " \
        obmc-flash-bios-ubiattach.service \
        obmc-flash-bios-ubimount@.service \
        obmc-flash-bios-ubiumount-prsv.service \
        obmc-flash-bios-ubiumount-ro@.service \
        obmc-flash-bios-ubiumount-rw@.service \
        obmc-flash-bios-ubipatch.service \
        obmc-flash-bios-squashfsmount@.service \
        obmc-flash-bios-ubiremount.service \
        obmc-flash-bios-updatesymlinks.service \
        "
