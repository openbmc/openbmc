SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/2.10/libsigc++-${PV}.tar.xz"
SRC_URI[sha256sum] = "d082a2ce72c750f66b1a415abe3e852df2eae1e8af53010f4ac2ea261a478832"

S = "${WORKDIR}/libsigc++-${PV}"

inherit meson

FILES:${PN}-dev += "${libdir}/sigc++-*/"
FILES:${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
