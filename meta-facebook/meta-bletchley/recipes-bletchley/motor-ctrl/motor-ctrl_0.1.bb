SUMMARY = "Bletchley Motor control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "i2c-tools"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "mdio-tools"
RDEPENDS:${PN} += "bletchley-common-functions"

S = "${UNPACKDIR}"

SRC_URI += " \
    file://motor-ctrl \
    file://motor-init \
    file://power-ctrl \
    file://motor-init-calibration@.service \
    "

do_install() {
        install -D -m 0755 ${UNPACKDIR}/power-ctrl ${D}${sbindir}/power-ctrl
        install -D -m 0755 ${UNPACKDIR}/motor-ctrl ${D}${libexecdir}/${BPN}/motor-ctl
        install -D -m 0755 ${UNPACKDIR}/motor-init ${D}${libexecdir}/${BPN}/motor-init
        install -D -m 0644 ${UNPACKDIR}/motor-init-calibration@.service \
            ${D}${systemd_system_unitdir}/motor-init-calibration@.service
}

FILES:${PN}:append = " ${systemd_system_unitdir}/motor-init-calibration@.service"

SYSTEMD_SERVICE_FMT = "motor-init-calibration@{0}.service"
SYSTEMD_SERVICE:${PN} += " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"
