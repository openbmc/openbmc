SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

inherit gnomebase

DEPENDS += "boost cairo libsigc++-2.0"

SRC_URI = "https://www.cairographics.org/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "7e0d5c7f29175d573a03ab5c45aef63f48dd91a5caf335a404cd763e4b7cea4a"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

