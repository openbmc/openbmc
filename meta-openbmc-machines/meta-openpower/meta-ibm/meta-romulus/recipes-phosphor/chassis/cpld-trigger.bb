SUMMARY = "Romulus CPLD Trigger"
DESCRIPTION = "Romulus power on workaround to trigger CPLD to continue power sequence"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "obmc-pydevtools"

PROVIDES += 'virtual/p9-vcs-workaround'
RPROVIDES_${PN} += 'virtual-p9-vcs-workaround'

S = "${WORKDIR}"
SRC_URI += "file://cpld_trigger.sh \
            file://cpld_reset.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/cpld_trigger.sh ${D}${bindir}/cpld_trigger.sh
        install -m 0755 ${WORKDIR}/cpld_reset.sh ${D}${bindir}/cpld_reset.sh
}

TMPL = "cpld_trigger@.service"
TMPL_OFF = "cpld_reset@.service"
INSTFMT = "cpld_trigger@{0}.service"
INSTFMT_OFF = "cpld_reset@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
TGTFMT_OFF = "obmc-power-chassis-off@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"
FMT_OFF = "../${TMPL_OFF}:${TGTFMT_OFF}.wants/${INSTFMT_OFF}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += "${TMPL_OFF}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF', 'OBMC_CHASSIS_INSTANCES')}"
