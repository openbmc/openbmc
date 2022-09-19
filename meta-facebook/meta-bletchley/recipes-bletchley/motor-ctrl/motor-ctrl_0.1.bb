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
PWR_ON_INSTFMT="../host-poweron@.service:obmc-host-starting@{0}.target.wants/host-poweron@{0}.service"
PWR_OFF_INSTFMT="../host-poweroff@.service:obmc-host-stopping@{0}.target.wants/host-poweroff@{0}.service"
PWR_RESET_INSTFMT="host-reset@.service:host-reset@{0}.service"
PWR_CYCLE_INSTFMT="host-cycle@.service:host-cycle@{0}.service"
AC_ON_INSTFMT="../host-ac-on@.service:obmc-chassis-poweron@{0}.target.requires/host-ac-on@{0}.service"
AC_OFF_INSTFMT="../host-ac-off@.service:obmc-chassis-poweroff@{0}.target.requires/host-ac-off@{0}.service"

SYSTEMD_SERVICE:${PN} += "motor-init-calibration@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'MOTOR_INIT_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-poweron@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_ON_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-poweroff@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_OFF_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-reset@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_RESET_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-cycle@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'PWR_CYCLE_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-ac-on@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'AC_ON_INSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "host-ac-off@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'AC_OFF_INSTFMT', 'OBMC_HOST_INSTANCES')}"

# Host on requires these chassis on
START_TMPL_CTRL = "obmc-chassis-poweron@.target"
START_TGTFMT_CTRL = "obmc-host-startmin@{0}.target"
START_INSTFMT_CTRL = "obmc-chassis-poweron@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'START_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"

# Chassis off requires host off
STOP_TMPL_CTRL = "obmc-host-stop@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT_CTRL = "obmc-host-stop@{0}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'STOP_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"

# Hard power off requires chassis off
HARD_OFF_TMPL_CTRL = "obmc-chassis-poweroff@.target"
HARD_OFF_TGTFMT_CTRL = "obmc-chassis-hard-poweroff@{0}.target"
HARD_OFF_INSTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
HARD_OFF_FMT_CTRL = "../${HARD_OFF_TMPL_CTRL}:${HARD_OFF_TGTFMT_CTRL}.requires/${HARD_OFF_INSTFMT_CTRL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HARD_OFF_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"

# Host on unit configurations
HOST_ON_OVERRIDE_CONF_FMT = "host-poweron.conf:host-poweron@{0}.service.d/host-poweron.conf"
SYSTEMD_OVERRIDE:${PN}:bletchley += "${@compose_list_zip(d, 'HOST_ON_OVERRIDE_CONF_FMT', 'OBMC_HOST_INSTANCES')}"

# Host off unit configurations
HOST_OFF_OVERRIDE_CONF_FMT = "host-poweroff.conf:host-poweroff@{0}.service.d/host-poweroff.conf"
SYSTEMD_OVERRIDE:${PN}:bletchley += "${@compose_list_zip(d, 'HOST_OFF_OVERRIDE_CONF_FMT', 'OBMC_HOST_INSTANCES')}"
