SUMMARY = "Witherspoon Power Fault Analysis"
DESCRIPTION = "Analyzes power devices for faults"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit pkgconfig
inherit obmc-phosphor-systemd
inherit pythonnative

require ${PN}.inc

S = "${WORKDIR}/git"

DEPENDS += " \
         phosphor-logging \
         autoconf-archive-native \
         openpower-dbus-interfaces \
         sdbus++-native \
         sdeventplus \
         "

RDEPENDS_${PN} += " \
         phosphor-logging \
         phosphor-dbus-interfaces \
         sdbusplus \
         "

CHASSIS_ON_TGT = "obmc-chassis-poweron@0.target"
SEQ_MONITOR_SVC = "witherspoon-pseq-monitor.service"
SEQ_MONITOR_FMT = "../${SEQ_MONITOR_SVC}:${CHASSIS_ON_TGT}.wants/${SEQ_MONITOR_SVC}"

SEQ_PGOOD_SVC = "witherspoon-pseq-monitor-pgood.service"
SEQ_PGOOD_FMT = "../${SEQ_PGOOD_SVC}:${CHASSIS_ON_TGT}.wants/${SEQ_PGOOD_SVC}"

SYSTEMD_SERVICE_${PN} += "${SEQ_MONITOR_SVC} ${SEQ_PGOOD_SVC}"
SYSTEMD_LINK_${PN} += "${SEQ_MONITOR_FMT} ${SEQ_PGOOD_FMT}"

PSU_MONITOR_TMPL = "witherspoon-power-supply-monitor@.service"
PSU_MONITOR_INSTFMT = "witherspoon-power-supply-monitor@{0}.service"
PSU_MONITOR_TGT = "${SYSTEMD_DEFAULT_TARGET}"
PSU_MONITOR_FMT = "../${PSU_MONITOR_TMPL}:${PSU_MONITOR_TGT}.requires/${PSU_MONITOR_INSTFMT}"

FILES_witherspoon-psu-monitor = "${sbindir}/witherspoon-psu-monitor"
SYSTEMD_SERVICE_${PN} += "${PSU_MONITOR_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'PSU_MONITOR_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

PSU_MONITOR_ENV_FMT = "obmc/power-supply-monitor/power-supply-monitor-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'PSU_MONITOR_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
