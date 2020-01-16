SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"
SRC_URI[md5sum] = "49990935174bf6b2fa501e789c578135"
SRC_URI[sha256sum] = "f91401af213b219cdde24b46c50a57f29301feb7f965678f1d7ed4632cc6feb0"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS_remove = "--disable-silent-rules"
