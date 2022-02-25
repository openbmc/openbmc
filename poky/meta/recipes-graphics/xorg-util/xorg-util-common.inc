HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/utils"
LICENSE = "MIT"

XORG_PN = "${BPN}"

SRC_URI = "${XORG_MIRROR}/individual/util/${XORG_PN}-${PV}.tar.gz"

S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools pkgconfig
