SUMMARY = "library for DVD navigation features"
SECTION = "libs/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libdvdread"

SRC_URI = "http://download.videolan.org/pub/videolan/${BPN}/${PV}/${BP}.tar.bz2"
SRC_URI[md5sum] = "e9ea4de3bd8f204e61301d407d09f033"
SRC_URI[sha256sum] = "5097023e3d2b36944c763f1df707ee06b19dc639b2b68fb30113a5f2cbf60b6d"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS_remove = "--disable-silent-rules"

