SUMMARY = "NFS client library"
HOMEPAGE = "https://github.com/sahlberg/libnfs"
LICENSE = "LGPL-2.1-only & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=825301ba17efc9d188ee0abd4b924ada"

SRC_URI = "git://github.com/sahlberg/libnfs.git;protocol=https;branch=master \
           file://0001-CMakeLists.txt-respect-CMAKE_INSTALL_LIBDIR-for-mult.patch \
           "
SRCREV = "18c5c73ee88bb7dc8da0d55dc95164bb77e49dc6"
S = "${WORKDIR}/git"

DEPENDS += "gnutls"

inherit cmake

do_install:append() {
	rm -f ${D}${libdir}/cmake/libnfs/libnfs-config.cmake
}
