SUMMARY = "Minizip Compression Library"
DESCRIPTION = "Minizip is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://www.winimage.com/zLibDll/minizip.html"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zip.h;beginline=14;endline=30;md5=8eaa8535a3a1a2296b303f40f75385e7"

GITHUB_BASE_URI ?= "https://github.com/madler/zlib/releases/"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/zlib-${PV}.tar.xz"

S = "${UNPACKDIR}/zlib-${PV}/contrib/minizip"

SRC_URI[sha256sum] = "38ef96b8dfe510d42707d9c781877914792541133e1870841463bfa73f883e32"

PACKAGECONFIG ??= "demos"
PACKAGECONFIG[demos] = "--enable-demos=yes,,,"

RCONFLICTS:${PN} += "minizip-ng"

DEPENDS = "zlib"

inherit autotools github-releases

BBCLASSEXTEND = "native nativesdk"
