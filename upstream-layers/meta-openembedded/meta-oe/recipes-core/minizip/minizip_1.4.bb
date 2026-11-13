SUMMARY = "Minizip Compression Library"
DESCRIPTION = "Minizip is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://www.winimage.com/zLibDll/minizip.html"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zip.h;beginline=14;endline=30;md5=b7d2930a7332b2bc68fc1a7fdc5ba775"

GITHUB_BASE_URI ?= "https://github.com/madler/zlib/releases/"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/zlib-${PV}.tar.xz"

S = "${UNPACKDIR}/zlib-${PV}/contrib/minizip"

SRC_URI[sha256sum] = "4f11c512053d7bab8d9e9b89bde3575add3e4c50a383b42ef6cb837035fc5a7c"

PACKAGECONFIG ??= "demos"
PACKAGECONFIG[demos] = "--enable-demos=yes,,,"

RCONFLICTS:${PN} += "minizip-ng"

DEPENDS = "zlib"

inherit autotools github-releases

BBCLASSEXTEND = "native nativesdk"
