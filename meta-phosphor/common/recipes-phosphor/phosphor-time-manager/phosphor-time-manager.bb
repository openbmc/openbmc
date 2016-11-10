SUMMARY = "Phosphor Time Manager daemon"
DESCRIPTION = "Daemon to cater to BMC and HOST time management"
HOMEPAGE = "http://github.com/openbmc/phosphor-time-manager"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service
inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "phosphor-settings"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "libsystemd"
SRC_URI += "git://github.com/openbmc/phosphor-time-manager"

SRCREV = "81ee91f4507fe5a75df9dfa21fc406813a09a7ff"

S = "${WORKDIR}/git"
