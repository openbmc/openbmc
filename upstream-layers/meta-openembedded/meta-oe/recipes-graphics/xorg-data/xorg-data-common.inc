HOMEPAGE = "http://www.x.org"
SECTION = "x11/data"
LICENSE = "MIT"

DEPENDS = "util-macros-native virtual/libx11"

SRC_URI_EXT = "xz"
SRC_URI = "${XORG_MIRROR}/individual/data/${BP}.tar.${SRC_URI_EXT}"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"
