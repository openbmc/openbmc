SUMMARY = "OpenBMC fan control"
DESCRIPTION = "OpenBMC fan control."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton-sdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

DEPENDS = "systemd"

SKELETON_DIR = "fanctl"

DBUS_SERVICE:${PN} += "org.openbmc.control.Fans.service"
SYSTEMD_SERVICE:${PN} += "obmc-max-fans.service"
