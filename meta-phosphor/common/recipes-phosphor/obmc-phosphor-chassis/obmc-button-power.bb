SUMMARY = "OpenBMC org.openbmc.Button example implementation"
DESCRIPTION = "A sample implementation for a button controlling a power domain."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "pwrbutton"
BASE_BUSNAME = "org.openbmc.buttons.Power"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_POWER_BUTTON_INSTANCES"
