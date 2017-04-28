SUMMARY = "Set Witherspoon cooling type properties"
DESCRIPTION = "Uses arguments passed into service to set cooling properties"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

S = "${WORKDIR}/git"

TMPL = "phosphor-cooling-type@.service"
INSTFMT = "phosphor-cooling-type@{0}.service"
TGTFMT = "obmc-host-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

