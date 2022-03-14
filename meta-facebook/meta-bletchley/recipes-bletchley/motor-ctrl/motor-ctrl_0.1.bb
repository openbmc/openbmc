SUMMARY = "Bletchley Motor control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "i2c-tools"
RDEPENDS:${PN} += "libgpiod-tools"

S = "${WORKDIR}"
SRC_URI += " \
    file://motor-ctrl \
    file://motor-init \
    file://power-ctrl \
    "

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/power-ctrl ${D}${sbindir}

        install -d ${D}${libexecdir}
        install -m 0755 ${WORKDIR}/motor-ctrl ${D}${libexecdir}
        install -m 0755 ${WORKDIR}/motor-init ${D}${libexecdir}
}

TGT = "${SYSTEMD_DEFAULT_TARGET}"
MOTOR_INIT_INSTFMT="../motor-init-calibration@.service:${TGT}.wants/motor-init-calibration@{0}.service"
PWR_ON_INSTFMT="host-poweron@.service:host-poweron@{0}.service"
PWR_OFF_INSTFMT="host-poweroff@.service:host-poweroff@{0}.service"
AC_ON_INSTFMT="../host-ac-on@.service:${TGT}.wants/host-ac-on@{0}.service"
AC_OFF_INSTFMT="host-ac-off@.service:host-ac-off@{0}.service"

SYSTEMD_SERVICE:${PN} += "motor-init-calibration@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'MOTOR_INIT_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-poweron@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_ON_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-poweroff@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_OFF_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-ac-on@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'AC_ON_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-ac-off@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'AC_OFF_INSTFMT', 'OBMC_HOST_INSTANCES')}"
