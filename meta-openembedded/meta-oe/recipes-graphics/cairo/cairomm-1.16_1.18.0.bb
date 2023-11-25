SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

inherit gnomebase

DEPENDS += "boost cairo libsigc++-3"

SRC_URI = "https://www.cairographics.org/releases/cairomm-${PV}.tar.xz"
SRC_URI[sha256sum] = "b81255394e3ea8e8aa887276d22afa8985fc8daef60692eb2407d23049f03cfb"

S = "${WORKDIR}/cairomm-${PV}"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

