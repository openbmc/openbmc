SUMMARY = "Phosphor OpenBMC Kudo BIOS Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo BIOS Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/phosphor-ipmi-flash/config-bios.json"

SRC_URI_append_kudo = " \ 
    file://phosphor-ipmi-flash-bios-update.service \
    file://config-bios.json \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN}_append_kudo = " \
    phosphor-ipmi-flash-bios-update.service \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bios.json ${D}${datadir}/phosphor-ipmi-flash
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-bios-update.service  ${D}${systemd_system_unitdir}
}
