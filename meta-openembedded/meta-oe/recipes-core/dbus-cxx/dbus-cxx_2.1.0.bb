SUMMARY = "D-Bus wrapper in C++ for dbus"
HOMEPAGE = "https://dbus-cxx.github.io/"
BUGTRACKER = "https://github.com/libsigcplusplus/libsigcplusplus/issues"
SECTION = "base"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf0188f02184e1e84b9586ac53c3f83"

SRC_URI = "git://github.com/dbus-cxx/dbus-cxx.git;branch=master;protocol=https \
           file://0001-Include-typeinfo-for-typeid.patch \
           file://0001-include-utility-header.patch \
"
SRC_URI:append:libc-musl = " file://fix_build_musl.patch"
SRCREV = "73532d6a5faae9c721c2cc9535b8ef32d4d18264"

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
