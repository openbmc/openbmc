SUMMARY = "Phosphor OpenBMC Kudo SCP Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo SCP Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-scp.json"
FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-scpback.json"

SRC_URI += " \
    file://phosphor-ipmi-flash-scp-update.service \
    file://phosphor-ipmi-flash-scpback-update.service \
    file://config-scp.json \
    file://config-scpback.json \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
    phosphor-ipmi-flash-scp-update.service \
    phosphor-ipmi-flash-scpback-update.service \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-scp.json ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-scpback.json ${D}${datadir}/phosphor-ipmi-flash

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-ipmi-flash-scp-update.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-ipmi-flash-scpback-update.service ${D}${systemd_system_unitdir}
}
