SUMMARY = "OpenPOWER Host Watchdog application"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit allarch

DEPENDS += "virtual/obmc-watchdog"
RDEPENDS_${PN} += "virtual/obmc-watchdog"

# For now, watching PowerOn is the only usecase
OBMC_HOST_WATCHDOG_INSTANCES = "poweron"

# Copies config file having arguments for host watchdog
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/watchdog/poweron"

STATES = "start stop"
WATCHDOG_SERVICE_FMT = "openpower-host-watchdog-{0}@.target"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'WATCHDOG_SERVICE_FMT', 'STATES')}"

WATCHDOG_TMPL = "openpower-host-watchdog-{0}@.target"
WATCHDOG_TGTFMT = "openpower-host-watchdog-{0}@{1}.target"
WATCHDOG_FMT = "../${WATCHDOG_TMPL}:obmc-host-{0}@{2}.target.wants/${WATCHDOG_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'WATCHDOG_FMT', 'STATES', 'OBMC_HOST_WATCHDOG_INSTANCES', 'OBMC_HOST_INSTANCES')}"
