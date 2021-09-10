SUMMARY = "Opus Audio Codec"
DESCRIPTION = "The libopusenc libraries provide a high-level API for encoding \
               .opus files. libopusenc depends only on libopus."
HOMEPAGE = "http://www.opus-codec.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=174b92049c2c697eb73112801662a07c"

DEPENDS = "libopus"

UPSTREAM_CHECK_URI = "https://github.com/xiph/libopusenc/releases"

SRC_URI = "https://ftp.osuosl.org/pub/xiph/releases/opus/libopusenc-${PV}.tar.gz"
SRC_URI[md5sum] = "f038ea0f4168d184c76b42d293697c57"
SRC_URI[sha256sum] = "8298db61a8d3d63e41c1a80705baa8ce9ff3f50452ea7ec1c19a564fe106cbb9"

S = "${WORKDIR}/libopusenc-${PV}"

inherit autotools pkgconfig
