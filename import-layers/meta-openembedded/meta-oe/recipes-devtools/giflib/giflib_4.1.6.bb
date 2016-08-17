SUMMARY = "shared library for GIF images"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae11c61b04b2917be39b11f78d71519a"
PR = "r3"

SRC_URI = "${SOURCEFORGE_MIRROR}/giflib/${BP}.tar.bz2"

inherit autotools

EXTRA_OECONF = "--disable-x11"

PACKAGES += "${PN}-utils"
FILES_${PN} = "${libdir}/libgif.so.*"
FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"

RDEPENDS_${PN}-utils = "perl"

SRC_URI[md5sum] = "7125644155ae6ad33dbc9fc15a14735f"
SRC_URI[sha256sum] = "e1c1ced9c5bc8f93ef0faf0a8c7717abf784d10a7b270d2285e8e1f3b93f2bed"
