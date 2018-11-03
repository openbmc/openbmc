SUMMARY = "Enables reboots on host failures"
DESCRIPTION = "Manages the settings entry that controls reboots \
on host failures"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

TMPL = "host-failure-reboots@.service"
INSTFMT = "host-failure-reboots@{0}.service"
LINK_FMT = "${TMPL}:${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'LINK_FMT', 'OBMC_HOST_INSTANCES')}"
