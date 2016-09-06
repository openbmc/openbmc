SUMMARY = "OpenBMC checkstop monitor."
DESCRIPTION = "The checkstop monitor watches a GPIO for a checkstop signal \
and reboots a server."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "hostcheckstop"

FMT = "org.openbmc.control.Checkstop@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHECKSTOP_INSTANCES')}"
