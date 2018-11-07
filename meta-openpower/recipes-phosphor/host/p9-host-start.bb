SUMMARY = "POWER9 start host"
DESCRIPTION = "Service to start POWER9 IPL through SBE"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

FILESEXTRAPATHS_prepend := "${THISDIR}/op-host-control:"

PROVIDES += 'virtual/obmc-host-ctl'
RPROVIDES_${PN} += 'virtual-obmc-host-ctl'

RDEPENDS_${PN} += "p9-vcs-workaround op-proc-control"

S = "${WORKDIR}"

TMPL = "start_host@.service"
INSTFMT = "start_host@{0}.service"
TGTFMT = "obmc-host-startmin@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
