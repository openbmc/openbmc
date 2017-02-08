SUMMARY = "Witherspoon P9 power on"
DESCRIPTION = "Witherspoon power on workaround"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "i2c-tools"

PROVIDES += 'virtual/p9-vcs-workaround'
RPROVIDES_${PN} += 'virtual-p9-vcs-workaround'

S = "${WORKDIR}"
SRC_URI += "file://vcs_off.sh \
            file://vcs_on.sh \
            file://ucd_disable_vcs.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vcs_off.sh ${D}${bindir}/vcs_off.sh
        install -m 0755 ${WORKDIR}/vcs_on.sh ${D}${bindir}/vcs_on.sh
        install -m 0755 ${WORKDIR}/ucd_disable_vcs.sh \
                        ${D}${bindir}/ucd_disable_vcs.sh
}

TMPL_OFF = "vcs_off@.service"
TMPL_OFF_PO = "vcs_off_poweroff@.service"
TMPL_ON = "vcs_on@.service"
TMPL_UCD = "ucd_disable_vcs@.service"
INSTFMT_OFF = "vcs_off@{0}.service"
INSTFMT_OFF_PO = "vcs_off_poweroff@{0}.service"
INSTFMT_ON = "vcs_on@{0}.service"
INSTFMT_UCD = "ucd_disable_vcs@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
TGTFMT_OFF= "obmc-power-chassis-off@{0}.target"
FMT_OFF = "../${TMPL_OFF}:${TGTFMT}.requires/${INSTFMT_OFF}"
FMT_ON = "../${TMPL_ON}:${TGTFMT}.requires/${INSTFMT_ON}"
FMT_UCD = "../${TMPL_UCD}:${TGTFMT}.requires/${INSTFMT_UCD}"
FMT_OFF_PO = "../${TMPL_OFF_PO}:${TGTFMT_OFF}.requires/${INSTFMT_OFF_PO}"

SYSTEMD_SERVICE_${PN} += "${TMPL_OFF}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_ON}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_UCD}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_UCD', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_OFF_PO}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF_PO', 'OBMC_CHASSIS_INSTANCES')}"

