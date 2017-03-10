SUMMARY = "POWER9 start host"
DESCRIPTION = "Service to start POWER9 IPL through SBE"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

FILESEXTRAPATHS_prepend := "${THISDIR}/op-host-control:"

PROVIDES += 'virtual/obmc-host-ctl'
RPROVIDES_${PN} += 'virtual-obmc-host-ctl'

RDEPENDS_${PN} += "p9-vcs-workaround op-proc-control"

S = "${WORKDIR}"

TMPL = "start_host@.service"
INSTFMT = "start_host@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
