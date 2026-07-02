SUMMARY = "library for DVD navigation features"
SECTION = "libs/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libdvdread"

SRC_URI = "https://download.videolan.org/pub/videolan/${BPN}/${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "a2a18f5ad36d133c74bf9106b6445806fa253b09141a46392550394b647b221e"

inherit meson lib_package binconfig pkgconfig manpages

PACKAGECONFIG[manpages] = "-Denable_docs=true,-Denable_docs=false,doxygen-native"


