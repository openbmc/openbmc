SUMMARY = "D-Bus wrapper in C++ for dbus"
HOMEPAGE = "https://dbus-cxx.github.io/"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf0188f02184e1e84b9586ac53c3f83"

FILEEXTRAPATHS_prepend = "${THISDIR}/files"
SRC_URI = "git://github.com/dbus-cxx/dbus-cxx.git;branch=master"
SRC_URI += "file://fix_build_musl.patch"
SRCREV = "ea7f8e361d11dc7d41d9ae2c4128aed2cdadd84e"

DEPENDS = "\
	dbus \
	libsigc++-2.0 \
"

RDEPENDS_${PN} = "\
	dbus \
	libsigc++-2.0 \
"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"
