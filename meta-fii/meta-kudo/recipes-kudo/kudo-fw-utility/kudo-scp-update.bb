SUMMARY = "Phosphor OpenBMC Kudo SCP Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo SCP Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/phosphor-ipmi-flash/config-scp.json"
FILES_${PN} += "${datadir}/phosphor-ipmi-flash/config-scpback.json"

SRC_URI_append_kudo = " \
    file://phosphor-ipmi-flash-scp-update.service \
    file://phosphor-ipmi-flash-scpback-update.service \
    file://config-scp.json \
    file://config-scpback.json \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN}_append_kudo = " \
    phosphor-ipmi-flash-scp-update.service \
    phosphor-ipmi-flash-scpback-update.service \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-scp.json ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-scpback.json ${D}${datadir}/phosphor-ipmi-flash

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-scp-update.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-scpback-update.service ${D}${systemd_system_unitdir}
}
