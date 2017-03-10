SUMMARY = "POWER9 VCS workaround"
DESCRIPTION = "Apply fixes over FSI to POWER9 CPUs prior to host power on"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

FILESEXTRAPATHS_prepend := "${THISDIR}/op-host-control:"
RDEPENDS_${PN} += "virtual-p9-vcs-workaround op-proc-control"

S = "${WORKDIR}"

TMPL = "vcs_workaround@.service"
INSTFMT = "vcs_workaround@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
