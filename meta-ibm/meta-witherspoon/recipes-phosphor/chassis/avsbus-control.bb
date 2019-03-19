SUMMARY = "Witherspoon AVSBus control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"
SRC_URI_append_swift = " file://swift-avsbus-disable.sh"
SRC_URI_append_swift = " file://swift-avsbus-enable.sh"
SRC_URI_append_swift = " file://swift-power-workarounds.sh"
SRC_URI_append_witherspoon = " file://witherspoon-avsbus-disable.sh"
SRC_URI_append_witherspoon = " file://witherspoon-avsbus-enable.sh"
SRC_URI_append_witherspoon = " file://witherspoon-power-workarounds.sh"
SRC_URI_append_witherspoon-128 = " file://witherspoon-avsbus-disable.sh"
SRC_URI_append_witherspoon-128 = " file://witherspoon-avsbus-enable.sh"
SRC_URI_append_witherspoon-128 = " file://witherspoon-power-workarounds.sh"

do_install() {
        install -d ${D}${bindir}
}
do_install_append_swift() {
        install -m 0755 ${WORKDIR}/swift-avsbus-disable.sh \
            ${D}${bindir}/avsbus-disable.sh
        install -m 0755 ${WORKDIR}/swift-avsbus-enable.sh \
            ${D}${bindir}/avsbus-enable.sh
        install -m 0755 ${WORKDIR}/swift-power-workarounds.sh \
            ${D}${bindir}/power-workarounds.sh
}
do_install_append_witherspoon() {
        install -m 0755 ${WORKDIR}/witherspoon-avsbus-disable.sh \
            ${D}${bindir}/avsbus-disable.sh
        install -m 0755 ${WORKDIR}/witherspoon-avsbus-enable.sh \
            ${D}${bindir}/avsbus-enable.sh
        install -m 0755 ${WORKDIR}/witherspoon-power-workarounds.sh \
            ${D}${bindir}/power-workarounds.sh
}
do_install_append_witherspoon-128() {
        install -m 0755 ${WORKDIR}/witherspoon-avsbus-disable.sh \
            ${D}${bindir}/avsbus-disable.sh
        install -m 0755 ${WORKDIR}/witherspoon-avsbus-enable.sh \
            ${D}${bindir}/avsbus-enable.sh
        install -m 0755 ${WORKDIR}/witherspoon-power-workarounds.sh \
            ${D}${bindir}/power-workarounds.sh
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
