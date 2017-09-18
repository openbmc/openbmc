SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

require ${PN}.inc

DEPENDS += "autoconf-archive-native phosphor-logging"
RDEPENDS_${PN} += "phosphor-logging"

