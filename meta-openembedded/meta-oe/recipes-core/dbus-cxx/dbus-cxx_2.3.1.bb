SUMMARY = "D-Bus wrapper in C++ for dbus"
HOMEPAGE = "https://dbus-cxx.github.io/"
BUGTRACKER = "https://github.com/libsigcplusplus/libsigcplusplus/issues"
SECTION = "base"
LICENSE = "LGPL-3.0-or-later | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=24594f493407a4cd401ce9794e0b9308"

SRC_URI = "git://github.com/dbus-cxx/dbus-cxx.git;branch=master;protocol=https \
           file://0001-Include-missing-cstdint.patch"
SRCREV = "898f6ea8f7ffe454e81a9337002df555728d4199"

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
