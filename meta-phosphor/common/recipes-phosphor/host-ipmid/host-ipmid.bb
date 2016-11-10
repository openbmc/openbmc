SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

RRECOMMENDS_${PN} += "packagegroup-obmc-ipmid-providers-libs"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service

TARGET_CFLAGS   += "-fpic"

DEPENDS += "phosphor-mapper"
DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN}-dev += "phosphor-mapper-dev"
RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RRECOMMENDS_${PN} += "virtual-obmc-settings-mgmt"
SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid"

SRCREV = "80f714beec97953e004dec330f3fc320d1c8bb02"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"

DBUS_SERVICE_${PN} += "org.openbmc.HostServices.service"
