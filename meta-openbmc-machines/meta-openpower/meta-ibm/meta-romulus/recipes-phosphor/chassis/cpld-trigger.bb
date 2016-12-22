SUMMARY = "Romulus CPLD Trigger"
DESCRIPTION = "Romulus power on workaround to trigger CPLD to continue power sequence"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "obmc-pydevtools"

PROVIDES += 'virtual/p9-vcs-workaround'
RPROVIDES_${PN} += 'virtual-p9-vcs-workaround'

S = "${WORKDIR}"
SRC_URI += "file://cpld_trigger.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/cpld_trigger.sh ${D}${bindir}/cpld_trigger.sh
}

TMPL = "cpld_trigger@.service"
INSTFMT = "cpld_trigger@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

