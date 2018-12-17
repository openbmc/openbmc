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
    file://0001-mp4v2-Define-__STRING-if-cdefs.h-does-not-exist.patch \
"

SRC_URI[md5sum] = "78603720f655180059d6970c582a3005"
SRC_URI[sha256sum] = "8cc7b03ceb2722223a6457e95d4c994731d80214a03ba33d1af76ba53f4b3197"

EXTRA_OECONF += "--without-mp4v2"

PACKAGES =+ "lib${BPN} lib${BPN}-dev"

FILES_${PN} = " ${bindir}/faac "
FILES_lib${PN} = " ${libdir}/*.so.*"
FILES_lib${PN}-dev = " \
    ${includedir} \
    ${libdir}/*.so \
    ${libdir}/*.la \
"
