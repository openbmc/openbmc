SUMMARY = "Witherspoon P9 power on"
DESCRIPTION = "Witherspoon power on workaround"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "pdbg"

S = "${WORKDIR}"
SRC_URI += "file://vcs_off.sh \
            file://vcs_on.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vcs_off.sh ${D}${bindir}/vcs_off.sh
        install -m 0755 ${WORKDIR}/vcs_on.sh ${D}${bindir}/vcs_on.sh
}

TMPL_OFF = "vcs_off@.service"
TMPL_ON = "vcs_on@.service"
INSTFMT_OFF = "vcs_off@{0}.service"
INSTFMT_ON = "vcs_on@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT_OFF = "../${TMPL_OFF}:${TGTFMT}.wants/${INSTFMT_OFF}"
FMT_ON = "../${TMPL_ON}:${TGTFMT}.wants/${INSTFMT_ON}"

SYSTEMD_SERVICE_${PN} += "${TMPL_OFF}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_ON}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"

