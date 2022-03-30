SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"
SRC_URI[md5sum] = "034581479968405ed415c34a50d00224"
SRC_URI[sha256sum] = "cc190f553758ced7571859e301f802cb4821f164d02bfacfd320c14a4e0da763"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS:remove = "--disable-silent-rules"
