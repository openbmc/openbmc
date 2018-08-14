SUMMARY = "Opus Audio Tools"
HOMEPAGE = "http://www.opus-codec.org/"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=843a066da9f1facfcc6ea6f616ffecb1"

SRC_URI = "http://downloads.xiph.org/releases/opus/opus-tools-${PV}.tar.gz"
SRC_URI[md5sum] = "b424790eda9357a4df394e2d7ca19eac"
SRC_URI[sha256sum] = "e4e188579ea1c4e4d5066460d4a7214a7eafe3539e9a4466fdc98af41ba4a2f6"

S = "${WORKDIR}/opus-tools-${PV}"

DEPENDS = "libopus flac"

inherit autotools pkgconfig
