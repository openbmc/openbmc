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
TMPL_RESET = "cpld_reset@.service"
INSTFMT = "cpld_trigger@{0}.service"
INSTFMT_RESET = "cpld_reset@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
TGTFMT_RESET = "obmc-chassis-poweroff@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"
FMT_RESET = "../${TMPL_RESET}:${TGTFMT_RESET}.requires/${INSTFMT_RESET}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += "${TMPL_RESET}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_RESET', 'OBMC_CHASSIS_INSTANCES')}"
