SUMMARY = "C++ wrapper for libxml library"
DESCRIPTION = "C++ wrapper for libxml library"
HOMEPAGE = "http://libxmlplusplus.sourceforge.net"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml%2B%2B"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 "

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${SHRT_VER}/${BP}.tar.xz \
    file://libxml++_ptest.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "9b59059abe5545d28ceb388a55e341095f197bd219c73e6623aeb6d801e00be8"

DEPENDS = "libxml2 glibmm mm-common-native"

inherit autotools pkgconfig ptest

EXTRA_OECONF = "--disable-documentation"

do_configure:prepend() {
    mm-common-prepare --copy --force ${S}
}

do_compile_ptest() {
  oe_runmake -C examples buildtest
}

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/libxml++-2.6/include/libxml++config.h"

RDEPENDS:${PN}-ptest += "make"
