SUMMARY = "D-Bus wrapper in C++ for dbus"
HOMEPAGE = "https://dbus-cxx.github.io/"
BUGTRACKER = "https://github.com/libsigcplusplus/libsigcplusplus/issues"
SECTION = "base"
LICENSE = "LGPL-3.0-or-later | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=24594f493407a4cd401ce9794e0b9308"

SRC_URI = "git://github.com/dbus-cxx/dbus-cxx.git;branch=master;protocol=https"
SRCREV = "2c3b3a767a5898ea8e51159e8122ddbb3aaeeb94"

DEPENDS = "\
	dbus \
	libsigc++-3 \
"

RDEPENDS:${PN} = "\
	dbus \
	libsigc++-3 \
"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"
