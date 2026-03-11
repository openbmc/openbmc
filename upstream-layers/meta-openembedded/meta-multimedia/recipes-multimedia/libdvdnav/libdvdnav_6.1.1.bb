SUMMARY = "library for DVD navigation features"
SECTION = "libs/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libdvdread"

SRC_URI = "http://download.videolan.org/pub/videolan/${BPN}/${PV}/${BP}.tar.bz2"
SRC_URI[md5sum] = "46c46cb0294fbd1fcb8a0181818dad15"
SRC_URI[sha256sum] = "c191a7475947d323ff7680cf92c0fb1be8237701885f37656c64d04e98d18d48"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS:remove = "--disable-silent-rules"

