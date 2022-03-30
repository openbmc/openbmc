SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase

DEPENDS += "boost cairo libsigc++-3"

SRC_URI = "https://www.cairographics.org/releases/cairomm-${PV}.tar.xz"
SRC_URI[sha256sum] = "6f6060d8e98dd4b8acfee2295fddbdd38cf487c07c26aad8d1a83bb9bff4a2c6"

S = "${WORKDIR}/cairomm-${PV}"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

