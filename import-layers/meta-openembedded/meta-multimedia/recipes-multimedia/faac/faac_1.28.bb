SUMMARY = "Freeware Advanced Audio Coder (MPEG2-AAC, MPEG4-AAC)"
SECTION = "libs"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://libfaac/coder.h;beginline=5;endline=17;md5=fa1fd6a5fa8cdc877d63a12530d273e0"

inherit autotools-brokensep

SRC_URI = "${SOURCEFORGE_MIRROR}/faac/${BP}.tar.gz \
           file://build-fix.patch \
           file://address-gcc-6-narrowing-errors.patch \
"

SRC_URI[md5sum] = "80763728d392c7d789cde25614c878f6"
SRC_URI[sha256sum] = "c5141199f4cfb17d749c36ba8cfe4b25f838da67c22f0fec40228b6b9c3d19df"

PACKAGES =+ "lib${BPN} lib${BPN}-dev"

FILES_${PN} = " ${bindir}/faac "
FILES_lib${PN} = " ${libdir}/libfaac.so.*"
FILES_lib${PN}-dev = "${includedir}/faac.h ${includedir}/faaccfg.h ${libdir}/libfaac.so ${libdir}/libfaac.la"
FILES_lib${PN}-staticdev = "${libdir}/libfaac.a"
