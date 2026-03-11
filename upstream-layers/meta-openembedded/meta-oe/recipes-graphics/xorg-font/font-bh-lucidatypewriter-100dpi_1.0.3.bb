require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Bigelow & Holmes Lucida Typewriter 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/bh-lucidatypewriter-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0d221a9cd144806cb469735cc4775939"

DEPENDS = "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

UPSTREAM_CHECK_REGEX = "font\-bh\-lucidatypewriter\-100dpi\-(?P<pver>\d+(\.\d+)+).tar.gz"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "62a83363c2536095fda49d260d21e0847675676e4e3415054064cbdffa641fbb"
