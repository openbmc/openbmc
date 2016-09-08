SUMMARY = "OpenBMC org.openbmc.control.Bmc example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.control.Bmc DBUS API. \
org.openbmc.control.Bmc provides APIs for functions like resetting the BMC."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "bmcctl"

FMT = "org.openbmc.control.Bmc@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_BMC_INSTANCES')}"
