SUMMARY = "Library and test program for decoding MPEG-2 and MPEG-1 video streams"
HOMEPAGE = "http://libmpeg2.sourceforge.net/"
SECTION = "libs"
LICENSE = "GPLv2+"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://include/mpeg2.h;beginline=1;endline=22;md5=ead62602d4638329d3b5b86a55803154"

PR = "r2"

SRC_URI = "http://libmpeg2.sourceforge.net/files/mpeg2dec-${PV}.tar.gz \
           file://altivec_h_needed.patch"

SRC_URI[md5sum] = "7631b0a4bcfdd0d78c0bb0083080b0dc"
SRC_URI[sha256sum] = "c74a76068f8ec36d4bb59a03bf1157be44118ca02252180e8b358b0b5e3edeee"

UPSTREAM_CHECK_URI = "http://libmpeg2.sourceforge.net/downloads.html"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-shared --disable-sdl"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-x,--without-x,virtual/libx11 libxext libxv"

PACKAGES = "mpeg2dec-dbg mpeg2dec mpeg2dec-doc libmpeg2 libmpeg2-dev libmpeg2convert libmpeg2convert-dev libmpeg2-staticdev libmpeg2convert-staticdev"

FILES_${PN} = "${bindir}/*"
FILES_libmpeg2 = "${libdir}/libmpeg2.so.*"
FILES_libmpeg2convert = "${libdir}/libmpeg2convert.so.*"
FILES_libmpeg2-dev = "${libdir}/libmpeg2.so \
                      ${libdir}/libmpeg2.la \
                      ${libdir}/pkgconfig/libmpeg2.pc \
                      ${includedir}/mpeg2dec/mpeg2.h"
FILES_libmpeg2-staticdev = "${libdir}/libmpeg2.a"
FILES_libmpeg2convert-dev = "${libdir}/libmpeg2convert.so \
                             ${libdir}/libmpeg2convert.la \
                             ${libdir}/pkgconfig/libmpeg2convert.pc \
                             ${includedir}/mpeg2dec/mpeg2convert.h"
FILES_libmpeg2convert-staticdev = "${libdir}/libmpeg2convert.a"
