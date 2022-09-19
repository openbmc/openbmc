SUMMARY = "OpenBMC org.openbmc.control.Bmc example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.control.Bmc DBUS API. \
org.openbmc.control.Bmc provides APIs for functions like resetting the BMC."
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "bmcctl"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

FMT = "org.openbmc.control.Bmc@{0}.service"
DBUS_SERVICE:${PN} += "${@compose_list(d, 'FMT', 'OBMC_BMC_INSTANCES')}"
