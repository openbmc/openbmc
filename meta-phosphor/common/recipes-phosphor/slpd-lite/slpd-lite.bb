SUMMARY = "Lightweight SLP Server"
DESCRIPTION = "Unicast SLP Server"
HOMEPAGE = "http://github.com/openbmc/slpd-lite"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} += "slpd-lite.service"

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"

SRC_URI += "git://github.com/openbmc/slpd-lite"

SRCREV = "04ccedc5999dfb0107c127f184971465c7fe08b4"

S = "${WORKDIR}/git"
