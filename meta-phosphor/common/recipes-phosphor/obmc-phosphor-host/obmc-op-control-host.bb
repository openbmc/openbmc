SUMMARY = "org.openbmc.control.Host implementation for OpenPOWER"
DESCRIPTION = "A host control implementation suitable for OpenPOWER systems."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "op-hostctl"
BASE_BUSNAME = "org.openbmc.control.Host"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} = "${TEMPLATE}"
SYSTEMD_SERVICE_${PN} = "op-start-host@.service"

SYSTEMD_GENLINKS_${PN} = "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../op-start-host@.service:obmc-chassis-start@[1].target.wants/op-start-host@[0].service:OBMC_HOST_INSTANCES:OBMC_CHASSIS_INSTANCES"
