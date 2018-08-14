DESCRIPTION = "X documentation"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/docs"
LICENSE = "MIT-X"

SRC_URI = "${XORG_MIRROR}/individual/doc/${BPN}-${PV}.tar.bz2"

S = "${WORKDIR}/${BPN}-${PV}"

INC_PR = "r1"

inherit autotools pkgconfig
