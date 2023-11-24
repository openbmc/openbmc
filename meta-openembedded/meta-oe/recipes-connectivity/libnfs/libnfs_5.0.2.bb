SUMMARY = "NFS client library"
HOMEPAGE = "https://github.com/sahlberg/libnfs"
LICENSE = "LGPL-2.1-only & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=825301ba17efc9d188ee0abd4b924ada"

SRC_URI = " \
	git://github.com/sahlberg/libnfs.git;protocol=https;branch=master \
	file://0001-CMakeLists.txt-respect-CMAKE_INSTALL_LIBDIR-for-mult.patch \
"
SRCREV = "40348f45d6beb8a8f50b6b63414a98fc1a061b7d"
S = "${WORKDIR}/git"

inherit cmake

do_install:append() {
	rm -f ${D}${libdir}/cmake/libnfs/libnfs-config.cmake
}
