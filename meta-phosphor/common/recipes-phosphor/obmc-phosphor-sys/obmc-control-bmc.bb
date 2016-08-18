SUMMARY = "OpenBMC org.openbmc.control.Bmc example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.control.Bmc DBUS API. \
org.openbmc.control.Bmc provides APIs for functions like resetting the BMC."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "bmcctl"
BASE_BUSNAME = "org.openbmc.control.Bmc"
TEMPLATE = "${BASE_BUSNAME}@.service"

SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_BMC_INSTANCES"
DBUS_SERVICE_${PN} += "${TEMPLATE}"
