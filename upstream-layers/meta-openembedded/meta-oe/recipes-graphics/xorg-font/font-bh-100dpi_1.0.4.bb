require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Bigelow & Holmes 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/bh-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cffd5e9c4b61b3d74166ca74b99e460e"

DEPENDS += "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

UPSTREAM_CHECK_REGEX = "font\-bh\-100dpi\-(?P<pver>\d+(\.\d+)+).tar.xz"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "fd8f5efe8491faabdd2744808d3d4eafdae5c83e617017c7fddd2716d049ab1e"
