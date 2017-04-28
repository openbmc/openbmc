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

SYSTEMD_SERVICE_${PN} += "openpower-host-checkstop-monitor@.target"

CHECKSTOP_TMPL = "openpower-host-checkstop-monitor@.target"
CHECKSTOP_TGTFMT = "openpower-host-checkstop-monitor@{0}.target"
CHECKSTOP_MONITOR_FMT = "${CHECKSTOP_TMPL}:${CHECKSTOP_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHECKSTOP_MONITOR_FMT', 'OBMC_HOST_MONITOR_INSTANCES')}"

GPIO_TMPL = "phosphor-gpio-monitor@.service"
MONITOR_TGTFMT = "openpower-host-checkstop-monitor@{0}.target"
GPIO_INSTFMT = "phosphor-gpio-monitor@{0}.service"
MONITOR_GPIO_FMT = "../${GPIO_TMPL}:${MONITOR_TGTFMT}.wants/${GPIO_INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'MONITOR_GPIO_FMT', 'OBMC_HOST_MONITOR_INSTANCES')}"
