SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/2.10/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "70bcbde2c900e4925d6ef4bf50954195"
SRC_URI[sha256sum] = "f843d6346260bfcb4426259e314512b99e296e8ca241d771d21ac64f28298d81"

S = "${WORKDIR}/libsigc++-${PV}"

inherit autotools

EXTRA_AUTORECONF = "--exclude=autoheader"

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
