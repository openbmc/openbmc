SUMMARY = "Phosphor OpenBMC Kudo BIOS Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo BIOS Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

PROVIDES:append = " virtual/bios-update"
RPROVIDES:${PN}:append = " virtual/bios-update"

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

FILES:${PN} += "${datadir}/phosphor-ipmi-flash/config-bios.json"

SRC_URI += " \
    file://phosphor-ipmi-flash-bios-update.service \
    file://config-bios.json \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
    phosphor-ipmi-flash-bios-update.service \
    "

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-bios.json ${D}${datadir}/phosphor-ipmi-flash
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-ipmi-flash-bios-update.service  ${D}${systemd_system_unitdir}
}
