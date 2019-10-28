SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=959bffe2993816eb32ec4bc1ec1d5875"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/3.0/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "6ffe924f1d8624b5716468cde67dc03f"
SRC_URI[sha256sum] = "50a0855c1eb26e6044ffe888dbe061938ab4241f96d8f3754ea7ead38ab8ed06"

S = "${WORKDIR}/libsigc++-${PV}"

inherit autotools

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
