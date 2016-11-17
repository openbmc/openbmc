SUMMARY = "Light SLPD Server"
DESCRIPTION = "Unicast Server"
HOMEPAGE = "http://github.com/openbmc/slpd-lite"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} += "slpd-lite.service"

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "network"

SRC_URI += "git://github.com/openbmc/slpd-lite"

SRCREV = "07c462ac60219fab263aa319c657927d918d0309"

S = "${WORKDIR}/git"

