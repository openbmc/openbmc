SUMMARY = "POWER9 CFAM override"
DESCRIPTION = "Applies user CFAM register overrides from file"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

FILESEXTRAPATHS_prepend := "${THISDIR}/op-host-control:"
RDEPENDS_${PN} += "op-proc-control"

S = "${WORKDIR}"

TMPL = "cfam_override@.service"
INSTFMT = "cfam_override@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
