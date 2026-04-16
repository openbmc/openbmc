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

SRC_URI[sha256sum] = "d7a0654783a4da529d1bb793b7ad9c3318020af77667bcae35f95d0e42a792f3"

PACKAGECONFIG ??= "demos"
PACKAGECONFIG[demos] = "--enable-demos=yes,,,"

RCONFLICTS:${PN} += "minizip-ng"

DEPENDS = "zlib"

inherit autotools github-releases

BBCLASSEXTEND = "native nativesdk"
