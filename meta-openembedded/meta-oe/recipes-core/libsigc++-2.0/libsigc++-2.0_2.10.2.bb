SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/2.10/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "1b067bfae0b502e6a5127336cb09d2dd"
SRC_URI[sha256sum] = "b1ca0253379596f9c19f070c83d362b12dfd39c0a3ea1dd813e8e21c1a097a98"

S = "${WORKDIR}/libsigc++-${PV}"

inherit autotools

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
