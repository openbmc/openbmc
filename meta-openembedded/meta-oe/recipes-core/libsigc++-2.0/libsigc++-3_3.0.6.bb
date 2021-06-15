SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=959bffe2993816eb32ec4bc1ec1d5875"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/3.0/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "8cc0c1df6b7d9c466555f4a2963ab06a"
SRC_URI[sha256sum] = "b70edcf4611651c54a426e109b17196e1fa17da090592a5000e2d134c03ac5ce"

S = "${WORKDIR}/libsigc++-${PV}"

inherit setuptools3 meson

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
