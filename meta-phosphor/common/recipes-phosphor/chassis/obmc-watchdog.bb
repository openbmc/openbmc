SUMMARY = "OpenBMC org.openbmc.Watchdog example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Watchdog DBUS API."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "hostwatchdog"

FMT = "org.openbmc.watchdog.Host@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_WATCHDOG_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "obmc-start-watchdog@.service obmc-stop-watchdog@.service"

START_TMPL = "obmc-start-watchdog@.service"
START_TGTFMT = "obmc-chassis-start@{1}.target"
START_INSTFMT = "obmc-start-watchdog@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"

STOP_TMPL = "obmc-stop-watchdog@.service"
STOP_TGTFMT = "obmc-chassis-stop@{1}.target"
STOP_INSTFMT = "obmc-stop-watchdog@{0}.service"
STOP_FMT = "../${STOP_TMPL}:${STOP_TGTFMT}.requires/${STOP_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_WATCHDOG_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'STOP_FMT', 'OBMC_WATCHDOG_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"
