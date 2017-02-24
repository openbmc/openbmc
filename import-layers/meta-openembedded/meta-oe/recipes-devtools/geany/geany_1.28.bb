SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"
DEPENDS = "gtk+ python3-docutils-native"

inherit autotools pkgconfig perlnative pythonnative

SRC_URI = " \
    http://download.geany.org/${BP}.tar.bz2 \
    file://0001-configure.ac-remove-additional-c-test.patch \
"
SRC_URI[md5sum] = "39a29deb598e9e3503ee7d9d5fb51a34"
SRC_URI[sha256sum] = "f73a3708f1a26e9bf72da564d5037d6f7fedca2e0d6175db0681c2b672100a5a"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"
