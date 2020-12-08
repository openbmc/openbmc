SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=959bffe2993816eb32ec4bc1ec1d5875"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/3.0/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "8dca4559e732f47710370baa8bc2e66f"
SRC_URI[sha256sum] = "e4f4866a894bdbe053e4fb22ccc6bc4b6851fd31a4746fdd20b2cf6e87c6edb6"

S = "${WORKDIR}/libsigc++-${PV}"

inherit setuptools3 meson

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
