SUMMARY = "Phosphor OpenBMC WriteFRU for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC WriteFRU for  OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/ipmi-fru-parser"
PR = "r1"

RRECOMMENDS_${PN} = "virtual/obmc-phosphor-host-ipmi-hw"

inherit obmc-phosphor-license

DEPENDS += "systemd    \
		 	host-ipmid \
		 	"


RDEPENDS_${PN} += "libsystemd"

TARGET_CFLAGS += "-std=gnu++14"

SRC_URI += "git://github.com/openbmc/ipmi-fru-parser"

SRCREV = "20656b1e15bebd7bc2754bd680f045e253bd2809"

FILES_${PN} += "${libdir}/host-ipmid/*.so"
FILES_${PN}-dbg += "${libdir}/host-ipmid/.debug"

S = "${WORKDIR}/git"

do_install() {
	oe_runmake install DESTDIR=${D} LIBDIR=${libdir} BINDIR=${sbindir}
}


#        install -m 0755 -d ${D}${libdir}/host-ipmid
#        install -m 0755 ${S}/*.so ${D}${libdir}/host-ipmid/