SUMMARY = "Library and test program for decoding MPEG-2 and MPEG-1 video streams"
DESCRIPTION = "mpeg2dec is a test program for libmpeg2. It decodes \
mpeg-1 and mpeg-2 video streams, and also includes a demultiplexer \
for mpeg-1 and mpeg-2 program streams. The main purpose of mpeg2dec \
is to have a simple test bed for libmpeg2."
HOMEPAGE = "https://libmpeg2.sourceforge.io/"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://include/mpeg2.h;beginline=1;endline=22;md5=7766f4fcb58f0f8413c49a746f2ab89b"

SRC_URI = "http://libmpeg2.sourceforge.net/files/libmpeg2-${PV}.tar.gz \
           file://altivec_h_needed.patch \
           file://0001-check-for-available-arm-optimizations.patch \
           file://0002-Set-visibility-of-global-symbols-used-in-ARM-specifi.patch \
           file://61_global-symbol-test.patch \
           file://0001-Import-revision-1206-from-upstream-to-fix-PIE-build.patch \
           "

S = "${WORKDIR}/libmpeg2-${PV}"

SRC_URI[md5sum] = "0f92c7454e58379b4a5a378485bbd8ef"
SRC_URI[sha256sum] = "dee22e893cb5fc2b2b6ebd60b88478ab8556cb3b93f9a0d7ce8f3b61851871d4"

UPSTREAM_CHECK_URI = "http://libmpeg2.sourceforge.net/downloads.html"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-shared --disable-sdl"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--with-x,--without-x,virtual/libx11 libxext libxv"

PACKAGES = "mpeg2dec-dbg mpeg2dec mpeg2dec-doc libmpeg2 libmpeg2-dev libmpeg2convert libmpeg2convert-dev libmpeg2-staticdev libmpeg2convert-staticdev"

FILES:${PN} = "${bindir}/*"
FILES:libmpeg2 = "${libdir}/libmpeg2.so.*"
FILES:libmpeg2convert = "${libdir}/libmpeg2convert.so.*"
FILES:libmpeg2-dev = "${libdir}/libmpeg2.so \
                      ${libdir}/libmpeg2.la \
                      ${libdir}/libmpeg2arch.la \
                      ${libdir}/pkgconfig/libmpeg2.pc \
                      ${includedir}/mpeg2dec/mpeg2.h"
FILES:libmpeg2-staticdev = "${libdir}/libmpeg2.a"
FILES:libmpeg2convert-dev = "${libdir}/libmpeg2convert.so \
                             ${libdir}/libmpeg2convert.la \
                             ${libdir}/libmpeg2convertarch.la \
                             ${libdir}/pkgconfig/libmpeg2convert.pc \
                             ${includedir}/mpeg2dec/mpeg2convert.h"
FILES:libmpeg2convert-staticdev = "${libdir}/libmpeg2convert.a"
