require recipes-graphics/xorg-font/xorg-font-common.inc

SUMMARY = "X.org miscellaneous fonts"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/font/misc-misc/"
SECTION = "x11/font"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=200c507f595ee97008c7c5c3e94ab9a8"

DEPENDS = "util-macros-native font-util-native bdftopcf-native font-util"
RDEPENDS:${PN} = "encodings font-util"

inherit features_check
# depends on bdftopcf-native -> virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "79abe361f58bb21ade9f565898e486300ce1cc621d5285bec26e14b6a8618fed"
