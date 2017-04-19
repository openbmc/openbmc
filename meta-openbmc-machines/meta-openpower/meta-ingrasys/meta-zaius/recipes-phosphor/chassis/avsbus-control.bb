SUMMARY = "Zaius AVSBus control"
DESCRIPTION = "Voltage regulator module (VRM) AVSBus control for Zaius"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TMPL_OFF = "avsbus-disable@.service"
TMPL_ON = "avsbus-enable@.service"
INSTFMT_OFF = "avsbus-disable@{0}.service"
INSTFMT_ON = "avsbus-enable@{0}.service"
TGTFMT_OFF = "obmc-host-stop@{0}.target"
TGTFMT_ON = "obmc-chassis-poweron@{0}.target"
FMT_OFF = "../${TMPL_OFF}:${TGTFMT_OFF}.wants/${INSTFMT_OFF}"
FMT_ON = "../${TMPL_ON}:${TGTFMT_ON}.requires/${INSTFMT_ON}"

SYSTEMD_SERVICE_${PN} += "${TMPL_OFF}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_ON}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"

SRC_URI += "file://zaius_avsbus.sh"
RDEPENDS_${PN} += "i2c-tools"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/zaius_avsbus.sh ${D}${bindir}/zaius_avsbus.sh
}
