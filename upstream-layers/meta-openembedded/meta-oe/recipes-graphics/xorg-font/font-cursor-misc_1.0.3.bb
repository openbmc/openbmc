require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "X.org cursor fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/cursor-misc/"
SECTION = "x11/font"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=8b32ccac3ad25e75e68478deb7780265"

DEPENDS = "util-macros-native font-util-native bdftopcf-native"
RDEPENDS:${PN} = "encodings font-util"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "17363eb35eece2e08144da5f060c70103b59d0972b4f4d77fd84c9a7a2dba635"
