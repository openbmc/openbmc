SUMMARY = "Witherspoon fan watchdog services"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "busybox"

RESET_SERVICE = "witherspoon-reset-fan-watchdog.service"
TGTFMT = "obmc-chassis-poweron@0.target"
RESET_FMT = "../${RESET_SERVICE}:${TGTFMT}.requires/${RESET_SERVICE}"

MONITOR_SERVICE = "witherspoon-fan-watchdog-monitor@.service"

WATCHDOG_SERVICE = "witherspoon-fan-watchdog.service"
WATCHDOG_FMT = "../${WATCHDOG_SERVICE}:${TGTFMT}.requires/${WATCHDOG_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${RESET_SERVICE} ${MONITOR_SERVICE} ${WATCHDOG_SERVICE}"
SYSTEMD_LINK_${PN} += "${RESET_FMT} ${WATCHDOG_FMT}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/witherspoon-fan-watchdog/reset-fan-watchdog.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/witherspoon-fan-watchdog/fan-watchdog.conf"
