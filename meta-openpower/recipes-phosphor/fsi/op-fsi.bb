SUMMARY = "FSI Services"
DESCRIPTION = "Install FSI related services"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "op-proc-control"

TGTFMT = "obmc-chassis-poweron@{0}.target"

TMPL_SCAN = "fsi-scan@.service"
INSTFMT_SCAN = "fsi-scan@{0}.service"
FMT_SCAN = "../${TMPL_SCAN}:${TGTFMT}.requires/${INSTFMT_SCAN}"

SYSTEMD_SERVICE_${PN} += "${TMPL_SCAN} fsi-enable.service fsi-disable.service"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_SCAN', 'OBMC_CHASSIS_INSTANCES')}"
