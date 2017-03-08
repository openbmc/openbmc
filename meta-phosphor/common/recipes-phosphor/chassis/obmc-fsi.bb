SUMMARY = "FSI Services"
DESCRIPTION = "Install FSI related services"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TGTFMT = "obmc-power-chassis-on@{0}.target"

TMPL_SCAN = "fsi-scan@.service"
INSTFMT_SCAN = "fsi-scan@{0}.service"
FMT_SCAN = "../${TMPL_SCAN}:${TGTFMT}.requires/${INSTFMT_SCAN}"

TMPL_BIND = "fsi-bind@.service"
INSTFMT_BIND = "fsi-bind@{0}.service"
FMT_BIND = "../${TMPL_BIND}:${TGTFMT}.requires/${INSTFMT_BIND}"

SYSTEMD_SERVICE_${PN} += "${TMPL_SCAN} ${TMPL_BIND} fsi-enable.service fsi-disable.service"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_SCAN', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_BIND', 'OBMC_CHASSIS_INSTANCES')}"
