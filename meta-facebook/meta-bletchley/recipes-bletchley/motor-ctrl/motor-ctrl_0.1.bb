SUMMARY = "Bletchley Motor control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "i2c-tools"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "mdio-tools"
RDEPENDS:${PN} += "bletchley-common-functions"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI += " \
    file://motor-ctrl \
    file://motor-init \
    file://power-ctrl \
    "

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${UNPACKDIR}/power-ctrl ${D}${sbindir}

        install -d ${D}${libexecdir}
        install -m 0755 ${UNPACKDIR}/motor-ctrl ${D}${libexecdir}
        install -m 0755 ${UNPACKDIR}/motor-init ${D}${libexecdir}
}

TGT = "${SYSTEMD_DEFAULT_TARGET}"
MOTOR_INIT_INSTFMT="../motor-init-calibration@.service:${TGT}.wants/motor-init-calibration@{0}.service"
SYSTEMD_SERVICE:${PN} += "motor-init-calibration@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'MOTOR_INIT_INSTFMT', 'OBMC_HOST_INSTANCES')}"
