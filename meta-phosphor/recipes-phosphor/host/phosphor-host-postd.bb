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

SNOOP_DEVICE ?= "aspeed-lpc-snoop0"
POST_CODE_BYTES ?= "1"

SERVICE_FILE = "lpcsnoop.service"
SYSTEMD_SERVICE_${PN} += "${SERVICE_FILE}"
SYSTEMD_SUBSTITUTIONS += "SNOOP_DEVICE:${SNOOP_DEVICE}:${SERVICE_FILE}"
SYSTEMD_SUBSTITUTIONS += "POST_CODE_BYTES:${POST_CODE_BYTES}:${SERVICE_FILE}"

require ${PN}.inc

S = "${WORKDIR}/git"

