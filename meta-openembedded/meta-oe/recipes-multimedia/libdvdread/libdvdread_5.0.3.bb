SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"
SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"

SRC_URI[md5sum] = "b7b7d2a782087ed2a913263087083715"
SRC_URI[sha256sum] = "321cdf2dbdc83c96572bc583cd27d8c660ddb540ff16672ecb28607d018ed82b"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS_remove = "--disable-silent-rules"

