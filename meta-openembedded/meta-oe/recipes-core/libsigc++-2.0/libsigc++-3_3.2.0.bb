SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=959bffe2993816eb32ec4bc1ec1d5875"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/3.2/libsigc++-${PV}.tar.xz"
SRC_URI[sha256sum] = "8cdcb986e3f0a7c5b4474aa3c833d676e62469509f4899110ddf118f04082651"

S = "${WORKDIR}/libsigc++-${PV}"

inherit setuptools3 meson

FILES:${PN}-dev += "${libdir}/sigc++-*/"
FILES:${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
