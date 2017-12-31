SUMMARY = "Talos fan watchdog services"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "busybox"

RESET_SERVICE = "talos-reset-fan-watchdog.service"
TGTFMT = "obmc-chassis-poweron@0.target"
RESET_FMT = "../${RESET_SERVICE}:${TGTFMT}.requires/${RESET_SERVICE}"

MONITOR_SERVICE = "talos-fan-watchdog-monitor@.service"

WATCHDOG_SERVICE = "talos-fan-watchdog.service"
WATCHDOG_FMT = "../${WATCHDOG_SERVICE}:${TGTFMT}.requires/${WATCHDOG_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${RESET_SERVICE} ${MONITOR_SERVICE} ${WATCHDOG_SERVICE}"
SYSTEMD_LINK_${PN} += "${RESET_FMT} ${WATCHDOG_FMT}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/talos-fan-watchdog/reset-fan-watchdog.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/talos-fan-watchdog/fan-watchdog.conf"
