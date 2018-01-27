SUMMARY = "OpenBMC fan control"
DESCRIPTION = "OpenBMC fan control."
PR = "r1"

inherit skeleton-sdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

RDEPENDS_${PN} += "libsystemd"
SKELETON_DIR = "fanctl"

DBUS_SERVICE_${PN} += "org.openbmc.control.Fans.service"
SYSTEMD_SERVICE_${PN} += "obmc-max-fans.service"
