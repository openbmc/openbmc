SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

inherit gnomebase

DEPENDS += "boost cairo libsigc++-2.0"

SRC_URI = "https://www.cairographics.org/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "0d37e067c5c4ca7808b7ceddabfe1932c5bd2a750ad64fb321e1213536297e78"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

