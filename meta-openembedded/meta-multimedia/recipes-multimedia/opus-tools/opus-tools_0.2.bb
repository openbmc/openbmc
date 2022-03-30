SUMMARY = "Opus Audio Tools"
HOMEPAGE = "http://www.opus-codec.org/"

LICENSE = "BSD-2-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=79f6fc2a6239fbe5f6e52f20ac76698c"

SRC_URI = "http://downloads.xiph.org/releases/opus/opus-tools-${PV}.tar.gz"
SRC_URI[md5sum] = "ff2d0536e960cabbfb8ca7c8c1759b6c"
SRC_URI[sha256sum] = "b4e56cb00d3e509acfba9a9b627ffd8273b876b4e2408642259f6da28fa0ff86"

S = "${WORKDIR}/opus-tools-${PV}"

DEPENDS = "libopus libopusenc flac opusfile"

inherit autotools pkgconfig
