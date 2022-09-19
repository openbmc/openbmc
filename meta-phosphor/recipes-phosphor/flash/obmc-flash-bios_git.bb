SUMMARY = "OpenBMC org.openbmc.Flash example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Flash DBUS API. \
org.openbmc.Flash provides APIs for functions like BIOS flash access control \
and updating."
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "flashbios"
SYSTEMD_SERVICE:${PN} += "obmc-flash-init.service"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

DBUS_SERVICE:${PN} += "org.openbmc.control.Flash.service"
