SUMMARY = "Phosphor OpenBMC WriteFRU for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC WriteFRU for  OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/ipmi-fru-parser"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += " \
        systemd \
        host-ipmid \
        "

RDEPENDS_${PN} += "libsystemd"

TARGET_CXXFLAGS += " -fpic -std=gnu++14"
TARGET_CFLAGS += " -fpic"
SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

SRC_URI += "git://github.com/openbmc/ipmi-fru-parser"

SRCREV = "ce3490e71f9f91bd20dbb9ac037079de4a3580a8"

FILES_SOLIBSDEV += "${libdir}/host-ipmid/lib*${SOLIBSDEV}"
FILES_${PN} += "${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dbg += "${libdir}/host-ipmid/.debug/lib*${SOLIBS}"

S = "${WORKDIR}/git"

do_install() {
        oe_runmake install DESTDIR=${D} LIBDIR=${libdir} BINDIR=${sbindir}
}
