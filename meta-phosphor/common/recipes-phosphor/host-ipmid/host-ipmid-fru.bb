SUMMARY = "Phosphor OpenBMC WriteFRU for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC WriteFRU for  OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/ipmi-fru-parser"
PR = "r1"

RRECOMMENDS_${PN} = "virtual/obmc-phosphor-host-ipmi-hw"

inherit obmc-phosphor-license

DEPENDS += " \
        systemd \
        host-ipmid \
        "

RDEPENDS_${PN} += "libsystemd"

TARGET_CFLAGS += " -fpic -std=gnu++14"

SRC_URI += "git://github.com/bradbishop/ipmi-fru-parser"

SRCREV = "dcaae3a17c407ad83bae0b58c44085fbc3eb4bc1"

FILES_SOLIBSDEV += "${libdir}/host-ipmid/lib*${SOLIBSDEV}"
FILES_${PN} += "${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dbg += "${libdir}/host-ipmid/.debug/lib*${SOLIBS}"

S = "${WORKDIR}/git"

do_install() {
        oe_runmake install DESTDIR=${D} LIBDIR=${libdir} BINDIR=${sbindir}
}
