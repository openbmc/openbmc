require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "X.org miscellaneous fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/misc-misc/"
SECTION = "x11/font"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=200c507f595ee97008c7c5c3e94ab9a8"

DEPENDS = "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS_${PN} = "encodings font-util"

inherit distro_features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "c88eb44b3b903d79fb44b860a213e623"
SRC_URI[sha256sum] = "b8e77940e4e1769dc47ef1805918d8c9be37c708735832a07204258bacc11794"
