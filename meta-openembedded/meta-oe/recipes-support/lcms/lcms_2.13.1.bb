SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac638b4bc6b67582a11379cfbaeb93dd"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[sha256sum] = "d473e796e7b27c5af01bd6d1552d42b45b43457e7182ce9903f38bb748203b88"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
