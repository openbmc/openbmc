SUMMARY = "Phosphor OpenBMC WriteFRU for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC WriteFRU for  OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/ipmi-fru-parser"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += " \
        systemd \
        host-ipmid \
        phosphor-mapper \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

SRC_URI += "git://github.com/openbmc/ipmi-fru-parser"
SRCREV = "ce3490e71f9f91bd20dbb9ac037079de4a3580a8"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"
