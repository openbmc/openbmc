SUMMARY = "Soft power off of host"
DESCRIPTION = "Service to stop the host in a controlled fashion"
PR = "r1"

inherit allarch
inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

# use mapper, not busctl
RDEPENDS_${PN} += "phosphor-mapper"

TMPL = "op-stop-host@.service"
INSTFMT = "op-stop-host@{0}.service"
TGTFMT = "obmc-stop-host@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"