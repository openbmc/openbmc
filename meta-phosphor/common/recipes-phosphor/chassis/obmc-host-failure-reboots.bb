SUMMARY = "Enables reboots on host failures"
DESCRIPTION = "Manages the settings entry that controls reboots \
on host failures"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

TMPL = "host-failure-reboots@.service"
INSTFMT = "host-failure-reboots@{0}.service"
LINK_FMT = "${TMPL}:${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'LINK_FMT', 'OBMC_HOST_INSTANCES')}"
