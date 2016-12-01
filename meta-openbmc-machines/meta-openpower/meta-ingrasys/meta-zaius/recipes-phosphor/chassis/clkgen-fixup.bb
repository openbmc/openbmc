DESCRIPTION = "Zaius clock generator fixup"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TMPL = "op-clkgen-fixup@.service"
INSTFMT = "op-clkgen-fixup@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

SRC_URI += "file://fix_clock_gen.sh"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/fix_clock_gen.sh ${D}${sbindir}/fix_clock_gen.sh
}
