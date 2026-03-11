SUMMARY = "Minizip Compression Library"
DESCRIPTION = "Minizip is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://www.winimage.com/zLibDll/minizip.html"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zip.h;beginline=14;endline=30;md5=8eaa8535a3a1a2296b303f40f75385e7"

SRC_URI = "${SOURCEFORGE_MIRROR}/libpng/zlib/${PV}/zlib-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "http://zlib.net/"

S = "${UNPACKDIR}/zlib-${PV}/contrib/minizip"

SRC_URI[sha256sum] = "9a93b2b7dfdac77ceba5a558a580e74667dd6fede4585b91eefb60f03b72df23"

PACKAGECONFIG ??= "demos"
PACKAGECONFIG[demos] = "--enable-demos=yes,,,"

RCONFLICTS:${PN} += "minizip-ng"

DEPENDS = "zlib"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
