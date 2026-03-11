DESCRIPTION = "X documentation"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/docs"
LICENSE = "MIT"

DEPENDS = "util-macros-native"

SRC_URI_EXT = "bz2"
SRC_URI = "${XORG_MIRROR}/individual/doc/${BPN}-${PV}.tar.${SRC_URI_EXT}"

inherit autotools pkgconfig


