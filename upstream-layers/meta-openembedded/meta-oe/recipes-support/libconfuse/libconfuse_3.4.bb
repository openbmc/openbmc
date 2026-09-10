SUMMARY = "libConfuse is a configuration file parser library"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42fa47330d4051cd219f7d99d023de3a"

SRC_URI = "https://github.com/libconfuse/libconfuse/releases/download/v${PV}/confuse-${PV}.tar.gz"
SRC_URI[sha256sum] = "d98a793f4cafc1b3c18e2509ba54f6cb9ac6291b181bcda152dc987cb78f43ec"

UPSTREAM_CHECK_URI = "https://github.com/libconfuse/libconfuse/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

inherit autotools-brokensep pkgconfig gettext

S = "${UNPACKDIR}/confuse-${PV}"

BBCLASSEXTEND = "native nativesdk"
