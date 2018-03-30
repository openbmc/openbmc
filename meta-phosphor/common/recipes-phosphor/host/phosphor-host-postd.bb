SUMMARY = "Phosphor OpenBMC Post Code Daemon"
DESCRIPTION = "Phosphor OpenBMC Post Code Daemon"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"

RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

SYSTEMD_SERVICE_${PN} += "lpcsnoop.service"

require ${PN}.inc

S = "${WORKDIR}/git"

