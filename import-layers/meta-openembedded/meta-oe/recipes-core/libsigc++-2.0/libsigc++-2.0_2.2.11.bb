SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/2.2/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "815d0c6d61601f51bbcaeef6826606b0"
SRC_URI[sha256sum] = "9834045f74f56752c2c6b3cdc195c30ab8314ad22dc8e626d6f67f940f1e4957"

S = "${WORKDIR}/libsigc++-${PV}"

inherit autotools

EXTRA_AUTORECONF = "--exclude=autoheader"

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
