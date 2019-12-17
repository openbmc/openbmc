SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"
SRC_URI[md5sum] = "b9eeaaaf3c41b1c3cb6c1622e7219aeb"
SRC_URI[sha256sum] = "28ce4f0063883ca4d37dfd40a2f6685503d679bca7d88d58e04ee8112382d5bd"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS_remove = "--disable-silent-rules"
