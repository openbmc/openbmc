SUMMARY = "OpenBMC org.openbmc.Watchdog example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Watchdog DBUS API."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "hostwatchdog"
BASE_BUSNAME = "org.openbmc.watchdog.Host"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_SERVICE_${PN} += "obmc-start-watchdog@.service obmc-stop-watchdog@.service"

SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_WATCHDOG_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../obmc-start-watchdog@.service:obmc-chassis-start@[1].target.wants/obmc-start-watchdog@[0].service:OBMC_WATCHDOG_INSTANCES:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../obmc-stop-watchdog@.service:obmc-chassis-stop@[1].target.wants/obmc-stop-watchdog@[0].service:OBMC_WATCHDOG_INSTANCES:OBMC_CHASSIS_INSTANCES"
