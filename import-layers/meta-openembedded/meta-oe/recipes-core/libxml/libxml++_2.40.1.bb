SUMMARY = "C++ wrapper for libxml library"
DESCRIPTION = "C++ wrapper for libxml library"
HOMEPAGE = "http://libxmlplusplus.sourceforge.net"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml%2B%2B"
SECTION = "libs"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 "

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${SHRT_VER}/${BP}.tar.xz \
    file://libxml++_ptest.patch \
    file://run-ptest \
"
SRC_URI[md5sum] = "377a87bea899f2b4ff62df2418c3d8a6"
SRC_URI[sha256sum] = "4ad4abdd3258874f61c2e2a41d08e9930677976d303653cd1670d3e9f35463e9"

DEPENDS = "libxml2 glibmm"

inherit autotools pkgconfig ptest

do_compile_ptest() {
  oe_runmake -C examples buildtest
}

FILES_${PN}-doc += "${datadir}/devhelp"
FILES_${PN}-dev += "${libdir}/libxml++-2.6/include/libxml++config.h"

RDEPENDS_${PN}-ptest += "make"
