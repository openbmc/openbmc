SUMMARY = "OpenBMC org.openbmc.Button example implementation"
DESCRIPTION = "A sample implementation for a reset button."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

SKELETON_DIR = "rstbutton"

FMT = "org.openbmc.buttons.reset@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_RESET_BUTTON_INSTANCES')}"
