SUMMARY = "Witherspoon VRM Overrides"
DESCRIPTION = "Sets Witherspoon VRMs to custom voltages"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools bash"

S = "${WORKDIR}"
SRC_URI += "file://vrm-control.sh \
            file://ir35221-unbind-bind.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vrm-control.sh ${D}${bindir}/vrm-control.sh
        install -m 0755 ${WORKDIR}/ir35221-unbind-bind.sh ${D}${bindir}/ir35221-unbind-bind.sh
}

TMPL = "vrm-control@.service"
INSTFMT = "vrm-control@{0}.service"
TGTFMT_ON = "obmc-chassis-poweron@{0}.target"
FMT_ON = "../${TMPL}:${TGTFMT_ON}.requires/${INSTFMT}"

TMPL_ON_IRBIND = "ir35221-on-bind@.service"
INSTFMT_ON_IRBIND = "ir35221-on-bind@{0}.service"
FMT_ON_IRBIND = "../${TMPL_ON_IRBIND}:${TGTFMT_ON}.wants/${INSTFMT_ON_IRBIND}"

TMPL_ON_IRUNBIND = "ir35221-on-unbind@.service"
INSTFMT_ON_IRUNBIND = "ir35221-on-unbind@{0}.service"
FMT_ON_IRUNBIND = "../${TMPL_ON_IRUNBIND}:${TGTFMT_ON}.requires/${INSTFMT_ON_IRUNBIND}"

SYSTEMD_SERVICE_${PN} += "${TMPL} ${TMPL_ON_IRUNBIND} ${TMPL_ON_IRBIND}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON_IRBIND', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON_IRUNBIND', 'OBMC_CHASSIS_INSTANCES')}"
