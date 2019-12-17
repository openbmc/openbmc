HOMEPAGE = "http://www.x.org"
SECTION = "x11/data"
LICENSE = "MIT-X"

XORG_PN = "${BPN}"

SRC_URI = "${XORG_MIRROR}/individual/data/${BP}.tar.bz2"
S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"
