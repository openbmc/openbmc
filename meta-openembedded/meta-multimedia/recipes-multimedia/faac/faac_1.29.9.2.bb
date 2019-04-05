SUMMARY = "Freeware Advanced Audio Coder (MPEG2-AAC, MPEG4-AAC)"
SECTION = "libs"
LICENSE = "LGPLv2+"
HOMEPAGE = "http://www.audiocoding.com/faac.html"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://libfaac/coder.h;beginline=5;endline=17;md5=fa1fd6a5fa8cdc877d63a12530d273e0"

LICENSE_FLAGS = "commercial"

inherit autotools

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/faac/${BP}.tar.gz \
"

SRC_URI[md5sum] = "2b58d621fad8fda879f07b7cad8bfe10"
SRC_URI[sha256sum] = "d45f209d837c49dae6deebcdd87b8cc3b04ea290880358faecf5e7737740c771"

PACKAGES =+ "lib${BPN} lib${BPN}-dev"

FILES_${PN} = " ${bindir}/faac "
FILES_lib${BPN} = " ${libdir}/*.so.*"
FILES_lib${BPN}-dev = " \
    ${includedir} \
    ${libdir}/*.so \
    ${libdir}/*.la \
"
