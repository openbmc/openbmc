SUMMARY = "CPU ERR LED application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "phosphor-gpio-monitor"
RDEPENDS:${PN} += "phosphor-gpio-monitor-monitor"

S = "${WORKDIR}"
SRC_URI += "file://toggle_caterr_led.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/toggle_caterr_led.sh \
            ${D}${bindir}/toggle_caterr_led.sh
}

SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/gpio/cpu_err"

CPU_ERR_SERVICE = "cpu_err"

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += "cpu-err.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'CPU_ERR_SERVICE')}"
