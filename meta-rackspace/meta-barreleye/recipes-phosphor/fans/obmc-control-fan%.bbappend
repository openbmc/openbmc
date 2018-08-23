FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SVC = "obmc-max-fans.service"
TGTFMT = "obmc-power-start@{0}.target"
FMT = "../${SVC}:${TGTFMT}.wants/${SVC}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_POWER_INSTANCES')}"
