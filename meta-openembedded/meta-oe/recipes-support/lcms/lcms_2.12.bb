SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac638b4bc6b67582a11379cfbaeb93dd"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[md5sum] = "8cb583c8447461896320b43ea9a688e0"
SRC_URI[sha256sum] = "18663985e864100455ac3e507625c438c3710354d85e5cbb7cd4043e11fe10f5"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
