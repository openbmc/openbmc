SUMMARY = "OpenBMC org.openbmc.Button example implementation"
DESCRIPTION = "A sample implementation for a button controlling a power domain."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

SKELETON_DIR = "pwrbutton"

FMT = "org.openbmc.buttons.Power@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_POWER_BUTTON_INSTANCES')}"
