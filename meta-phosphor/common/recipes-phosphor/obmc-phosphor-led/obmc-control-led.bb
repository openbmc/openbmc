SUMMARY = "OpenBMC org.openbmc.Led example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Led DBUS API. \
The org.openbmc.Led provides APIs for controlling LEDs."
PR = "r1"

inherit skeleton-sdbus

RDEPENDS_${PN} += "libsystemd"

SKELETON_DIR = "ledctl"
