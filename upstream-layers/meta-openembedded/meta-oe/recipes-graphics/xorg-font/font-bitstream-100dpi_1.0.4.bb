require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Bitstream 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/bitstream-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=30330812324ff9d9bd9ea645bb944427"

DEPENDS = "util-macros-native font-util-native bdftopcf-native"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

UPSTREAM_CHECK_REGEX = "font\-bitstream\-100dpi\-(?P<pver>\d+(\.\d+)+).tar.xz"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "2d1cc682efe4f7ebdf5fbd88961d8ca32b2729968728633dea20a1627690c1a7"
