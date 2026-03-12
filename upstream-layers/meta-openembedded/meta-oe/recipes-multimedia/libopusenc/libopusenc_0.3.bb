SUMMARY = "Opus Audio Codec"
DESCRIPTION = "The libopusenc libraries provide a high-level API for encoding \
               .opus files. libopusenc depends only on libopus."
HOMEPAGE = "http://www.opus-codec.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=174b92049c2c697eb73112801662a07c"

DEPENDS = "libopus"

UPSTREAM_CHECK_URI = "https://github.com/xiph/libopusenc/tags"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

SRC_URI = "git://github.com/xiph/libopusenc.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "0dba1bea736ab8bb811409dce80c994a00a2ced9"

S = "${UNPACKDIR}/libopusenc-${PV}"

inherit autotools pkgconfig
