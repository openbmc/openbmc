SUMMARY = "Witherspoon fan watchdog services"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "phosphor-gpio-monitor"

RESET_SERVICE = "witherspoon-reset-fan-watchdog.service"
TGTFMT = "obmc-chassis-poweron@0.target"
RESET_FMT = "../${RESET_SERVICE}:${TGTFMT}.requires/${RESET_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${RESET_SERVICE}"
SYSTEMD_LINK_${PN} += "${RESET_FMT}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/witherspoon-fan-watchdog/witherspoon-reset-fan-watchdog.conf"
