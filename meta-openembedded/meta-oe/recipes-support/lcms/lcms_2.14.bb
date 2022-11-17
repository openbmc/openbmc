SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac638b4bc6b67582a11379cfbaeb93dd"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[sha256sum] = "28474ea6f6591c4d4cee972123587001a4e6e353412a41b3e9e82219818d5740"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
