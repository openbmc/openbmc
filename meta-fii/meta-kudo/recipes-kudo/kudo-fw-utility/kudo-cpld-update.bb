SUMMARY = "Phosphor OpenBMC Kudo CPLD Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo CPLD Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-bmccpld.json"
FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-mbcpld.json"

SRC_URI += " \
    file://phosphor-ipmi-flash-bmccpld-update.service \
    file://phosphor-ipmi-flash-mbcpld-update.service \
    file://config-bmccpld.json \
    file://config-mbcpld.json \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
    phosphor-ipmi-flash-bmccpld-update.service \
    phosphor-ipmi-flash-mbcpld-update.service \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-bmccpld.json ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-mbcpld.json ${D}${datadir}/phosphor-ipmi-flash
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-ipmi-flash-bmccpld-update.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-ipmi-flash-mbcpld-update.service ${D}${systemd_system_unitdir}
}
