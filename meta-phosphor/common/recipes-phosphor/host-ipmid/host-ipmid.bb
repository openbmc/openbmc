SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

RRECOMMENDS_${PN} += "virtual/obmc-phosphor-host-ipmi-hw"

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_obmc-phosphor-ipmi-parsers}"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service

TARGET_CFLAGS   += "-fpic"

DEPENDS += "obmc-mapper"
DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN}-dev += "obmc-mapper-dev"
RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "settings"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
#SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid"
SRC_URI += "git:///home/msbarth/OpenBMC/gitrepos/phosphor-host-ipmid;rev=master"

#SRCREV = "bc40c178bb0b345ed1edf553b94369330003af34"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"
