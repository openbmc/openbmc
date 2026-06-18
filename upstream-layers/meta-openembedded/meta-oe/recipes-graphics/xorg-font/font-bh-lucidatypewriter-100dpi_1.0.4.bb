require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Bigelow & Holmes Lucida Typewriter 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/bh-lucidatypewriter-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0d221a9cd144806cb469735cc4775939"

DEPENDS = "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

UPSTREAM_CHECK_REGEX = "font\-bh\-lucidatypewriter\-100dpi\-(?P<pver>\d+(\.\d+)+).tar.xz"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "76ec09eda4094a29d47b91cf59c3eba229c8f7d1ca6bae2abbb3f925e33de8f2"
