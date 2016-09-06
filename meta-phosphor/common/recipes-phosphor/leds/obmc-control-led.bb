SUMMARY = "OpenBMC org.openbmc.Led example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Led DBUS API. \
The org.openbmc.Led provides APIs for controlling LEDs."
PR = "r1"

inherit skeleton-sdbus
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "libsystemd"

SKELETON_DIR = "ledctl"
DBUS_SERVICE_${PN} += "org.openbmc.control.led.service"
SYSTEMD_SERVICE_${PN} += "obmc-led-start@.service obmc-led-stop@.service"
