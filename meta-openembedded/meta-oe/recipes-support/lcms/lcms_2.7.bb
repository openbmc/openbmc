SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6c786c3b7a4afbd3c990f1b81261d516"
SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[md5sum] = "06c1626f625424a811fb4b5eb070839d"
SRC_URI[sha256sum] = "4524234ae7de185e6b6da5d31d6875085b2198bc63b1211f7dde6e2d197d6a53"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
