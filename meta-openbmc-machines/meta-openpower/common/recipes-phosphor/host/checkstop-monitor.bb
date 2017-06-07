SUMMARY = "OpenPOWER Host checkstop monitor application"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit allarch

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

# For now, monitoring checkstop is the only usecase
OBMC_HOST_MONITOR_INSTANCES = "checkstop"

# Copies config file having arguments for monitoring host checkstop
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/checkstop"

STATES = "start stop"
CHECKSTOP_SERVICE_FMT = "openpower-host-checkstop-monitor-{0}@.target"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'CHECKSTOP_SERVICE_FMT', 'STATES')}"

CHECKSTOP_TMPL = "openpower-host-checkstop-monitor-{0}@.target"
CHECKSTOP_TGTFMT = "openpower-host-checkstop-monitor-{0}@{1}.target"
CHECKSTOP_MONITOR_FMT = "../${CHECKSTOP_TMPL}:obmc-host-{0}@{2}.target.wants/${CHECKSTOP_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHECKSTOP_MONITOR_FMT', 'STATES', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
