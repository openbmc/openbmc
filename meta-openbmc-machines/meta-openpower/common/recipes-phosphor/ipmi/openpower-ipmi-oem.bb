SUMMARY = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
DESCRIPTION = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
HOMEPAGE = "https://github.com/openbmc/openpower-host-ipmi-oem"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license

DEPENDS += "phosphor-ipmi-host"
DEPENDS += "autoconf-archive-native"

TARGET_CFLAGS += "-fpic"

SRC_URI += "git://github.com/openbmc/openpower-host-ipmi-oem"
SRCREV = "cbfd6ec40ab2f61498599f7f34cae2e9b51c4970"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"
