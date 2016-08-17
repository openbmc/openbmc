SUMMARY = "C++ wrapper for libxml library"
DESCRIPTION = "C++ wrapper for libxml library"
HOMEPAGE = "http://libxmlplusplus.sourceforge.net"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml%2B%2B"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 "

SHRT_VER = "${@d.getVar('PV',True).split('.')[0]}.${@d.getVar('PV',True).split('.')[1]}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${SHRT_VER}/${BP}.tar.xz \
    file://libxml++_ptest.patch \
    file://run-ptest \
"
SRC_URI[md5sum] = "6b16aac575725a9bc0e9d96489e9251f"
SRC_URI[sha256sum] = "882529189b03db6c69925b3f579ab1941feb4f02b5fe2612504ee7e498a4a05f"

DEPENDS = "libxml2 glibmm"

inherit autotools pkgconfig ptest

do_compile_ptest() {
  oe_runmake -C examples buildtest
}

FILES_${PN}-doc += "${datadir}/devhelp"
FILES_${PN}-dev += "${libdir}/libxml++-2.6/include/libxml++config.h"

RDEPENDS_${PN}-ptest += "make"
