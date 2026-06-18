require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Adobe Utopia 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/adobe-utopia-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa13e704b7241f60ef9105cc041b9732"

DEPENDS = "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

UPSTREAM_CHECK_REGEX = "font\-adobe\-utopia\-100dpi\-(?P<pver>\d+(\.\d+)+).tar.xz"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "fb84ec297a906973548ca59b7c6daeaad21244bec5d3fb1e7c93df5ef43b024b"
