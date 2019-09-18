SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6c786c3b7a4afbd3c990f1b81261d516"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[md5sum] = "8de1b7724f578d2995c8fdfa35c3ad0e"
SRC_URI[sha256sum] = "48c6fdf98396fa245ed86e622028caf49b96fa22f3e5734f853f806fbc8e7d20"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
