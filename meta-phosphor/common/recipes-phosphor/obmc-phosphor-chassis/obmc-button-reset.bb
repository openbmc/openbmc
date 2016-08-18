SUMMARY = "OpenBMC org.openbmc.Button example implementation"
DESCRIPTION = "A sample implementation for a reset button."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

SKELETON_DIR = "rstbutton"
BASE_BUSNAME = "org.openbmc.buttons.reset"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_RESET_BUTTON_INSTANCES"
