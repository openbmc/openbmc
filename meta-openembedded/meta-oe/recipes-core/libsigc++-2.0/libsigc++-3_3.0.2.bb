SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=959bffe2993816eb32ec4bc1ec1d5875"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/3.0/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "1f93f8ed4ead38e876157834b2c57c21"
SRC_URI[sha256sum] = "4b77676de1e74774ec456bcc6ac6f04a2791a12cc1fe07f8305d4c3c86e2f339"

S = "${WORKDIR}/libsigc++-${PV}"

inherit setuptools3 meson

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
