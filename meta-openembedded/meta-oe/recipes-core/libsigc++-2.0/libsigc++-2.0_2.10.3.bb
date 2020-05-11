SUMMARY = "A library for loose coupling of C++ method calls"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

DEPENDS = "mm-common"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libsigc++/2.10/libsigc++-${PV}.tar.xz"
SRC_URI[md5sum] = "ea68c7afc23a4b89a2dfa78344460785"
SRC_URI[sha256sum] = "0b68dfc6313c6cc90ac989c6d722a1bf0585ad13846e79746aa87cb265904786"

S = "${WORKDIR}/libsigc++-${PV}"

inherit autotools

FILES_${PN}-dev += "${libdir}/sigc++-*/"
FILES_${PN}-doc += "${datadir}/devhelp"

BBCLASSEXTEND = "native"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
