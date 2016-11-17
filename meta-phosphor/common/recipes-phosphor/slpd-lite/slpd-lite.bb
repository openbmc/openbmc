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
#SRCREV = "" //TODO once code merged in the slpd-lite repo

S = "${WORKDIR}/git"

do_install_append() {
        install -m 0755 -d ${D}/${sbindir}
        install -m 0755 ${WORKDIR}/build/slpd ${D}/${sbindir}
}
