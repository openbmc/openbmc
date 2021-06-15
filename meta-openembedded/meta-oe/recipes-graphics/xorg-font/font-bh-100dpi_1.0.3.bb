require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "Bigelow & Holmes 100 DPI fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/bh-100dpi/"
SECTION = "x11/font"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cffd5e9c4b61b3d74166ca74b99e460e"

DEPENDS += "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS_${PN} = "encodings font-util"
RDEPENDS_${PN}_class-native = "font-util-native"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "9f11ade089d689b9d59e0f47d26f39cd"
SRC_URI[sha256sum] = "23c07162708e4b79eb33095c8bfa62c783717a9431254bbf44863734ea239481"
