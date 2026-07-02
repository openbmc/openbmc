SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "https://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.xz"
SRC_URI[sha256sum] = "2e3e04a305c15c3963aa03ae1b9a83c1d239880003fcf3dde986d3943355d407"

inherit meson lib_package binconfig pkgconfig manpages

PACKAGECONFIG ?= "libdvdcss"
PACKAGECONFIG[libdvdcss] = "-Dlibdvdcss=enabled,-Dlibdvdcss=disabled,libdvdcss"
PACKAGECONFIG[manpages] = "-Denable_docs=true,-Denable_docs=false,doxygen-native"
