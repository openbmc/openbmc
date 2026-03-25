SUMMARY = "Bletchley SLED Present Service"
DESCRIPTION = "OpenBMC Quanta Detect Present Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

S = "${UNPACKDIR}"

DEPENDS += "systemd"
RDEPENDS:${PN} += " bash phosphor-gpio-monitor-presence"

SRC_URI = " \
    file://detect-sled-present \
    file://detect-sled-present@.service \
    file://bletchley-sled-insertion@.target \
    file://bletchley-sled-removal@.target \
    "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/detect-sled-present ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/detect-sled-present@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/bletchley-sled-insertion@.target ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/bletchley-sled-removal@.target ${D}${systemd_system_unitdir}
}

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/detect-sled-present@.service \
    ${systemd_system_unitdir}/bletchley-sled-insertion@.target \
    ${systemd_system_unitdir}/bletchley-sled-removal@.target \
    "

SYSTEMD_SERVICE_FMT = "detect-sled-present@{0}.service bletchley-sled-insertion@{0}.target bletchley-sled-removal@{0}.target"
SYSTEMD_SERVICE:${PN} += "${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"
