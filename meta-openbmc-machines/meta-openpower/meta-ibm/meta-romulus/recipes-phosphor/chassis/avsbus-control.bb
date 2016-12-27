SUMMARY = "Romulus AVSBus control"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "i2c-tools bash"

S = "${WORKDIR}"
SRC_URI += "file://avsbus-workaround.sh \
            file://avsbus-enable.sh \
            file://avsbus-disable.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/avsbus-workaround.sh \
            ${D}${bindir}/avsbus-workaround.sh
        install -m 0755 ${WORKDIR}/avsbus-disable.sh \
            ${D}${bindir}/avsbus-disable.sh
        install -m 0755 ${WORKDIR}/avsbus-enable.sh \
            ${D}${bindir}/avsbus-enable.sh
}

TMPL_EN= "avsbus-enable@.service"
TMPL_DIS= "avsbus-disable@.service"
TMPL_WA= "avsbus-workaround@.service"
INSTFMT_EN= "avsbus-enable@{0}.service"
INSTFMT_DIS= "avsbus-disable@{0}.service"
INSTFMT_WA= "avsbus-workaround@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT_EN = "../${TMPL_EN}:${TGTFMT}.wants/${INSTFMT_EN}"
FMT_DIS = "../${TMPL_DIS}:${TGTFMT}.wants/${INSTFMT_DIS}"
FMT_WA = "../${TMPL_WA}:${TGTFMT}.wants/${INSTFMT_WA}"

SYSTEMD_SERVICE_${PN} += "${TMPL_EN}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_EN', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_DIS}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_DIS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_WA}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_WA', 'OBMC_CHASSIS_INSTANCES')}"
