SUMMARY = "OpenBMC fan control"
DESCRIPTION = "OpenBMC fan control."
DEPENDS = "systemd"
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "fanctl"
SYSTEMD_SERVICE:${PN} += "obmc-max-fans.service"

inherit skeleton-sdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

DBUS_SERVICE:${PN} += "org.openbmc.control.Fans.service"
