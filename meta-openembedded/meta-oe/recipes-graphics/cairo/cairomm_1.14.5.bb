SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

inherit gnomebase

DEPENDS += "boost cairo libsigc++-2.0"

SRC_URI = "https://www.cairographics.org/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "70136203540c884e89ce1c9edfb6369b9953937f6cd596d97c78c9758a5d48db"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

