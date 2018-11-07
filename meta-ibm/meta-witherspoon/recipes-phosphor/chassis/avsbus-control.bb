SUMMARY = "Witherspoon AVSBus control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"
SRC_URI += "file://power-workarounds.sh \
            file://avsbus-enable.sh \
            file://avsbus-disable.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/power-workarounds.sh \
            ${D}${bindir}/power-workarounds.sh
        install -m 0755 ${WORKDIR}/avsbus-disable.sh \
            ${D}${bindir}/avsbus-disable.sh
        install -m 0755 ${WORKDIR}/avsbus-enable.sh \
            ${D}${bindir}/avsbus-enable.sh
}

TMPL_EN= "avsbus-enable@.service"
TMPL_DIS= "avsbus-disable@.service"
TMPL_WA= "power-workarounds@.service"
INSTFMT_EN= "avsbus-enable@{0}.service"
INSTFMT_DIS= "avsbus-disable@{0}.service"
INSTFMT_WA= "power-workarounds@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_EN = "../${TMPL_EN}:${TGTFMT}.requires/${INSTFMT_EN}"
FMT_DIS = "../${TMPL_DIS}:${TGTFMT}.requires/${INSTFMT_DIS}"
FMT_WA = "../${TMPL_WA}:${TGTFMT}.requires/${INSTFMT_WA}"

SYSTEMD_SERVICE_${PN} += "${TMPL_EN}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_EN', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_DIS}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_DIS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_WA}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_WA', 'OBMC_CHASSIS_INSTANCES')}"
