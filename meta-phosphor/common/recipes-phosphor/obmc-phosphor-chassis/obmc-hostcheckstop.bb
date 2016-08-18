SUMMARY = "OpenBMC checkstop monitor."
DESCRIPTION = "The checkstop monitor watches a GPIO for a checkstop signal \
and reboots a server."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "hostcheckstop"
BASE_BUSNAME = "org.openbmc.control.Checkstop"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_CHECKSTOP_INSTANCES"
