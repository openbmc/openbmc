SUMMARY = "OpenPOWER Host checkstop monitor application"
PR = "r1"

inherit native
inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit obmc-phosphor-utils

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"
PROVIDES += "checkstop-monitor-native"
RPROVIDES_${PN} += "checkstop-monitor-native"

# For now, monitoring checkstop is the only usecase
OBMC_HOST_MONITOR_INSTANCES = "checkstop"

# Copies config file having arguments for monitoring host checkstop
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/checkstop"

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE_${PN} +="checkstop.conf:phosphor-gpio-monitor@checkstop.service.d/checkstop.conf"

STATES = "start"
GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{1}.service"
CHECKSTOP_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:obmc-host-{0}@{2}.target.wants/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHECKSTOP_MONITOR_FMT', 'STATES', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
