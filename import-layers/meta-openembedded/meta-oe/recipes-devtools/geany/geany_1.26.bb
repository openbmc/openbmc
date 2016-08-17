SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"
DEPENDS = "gtk+ python-docutils-native"

inherit autotools pkgconfig perlnative pythonnative

SRC_URI = " \
    http://download.geany.org/${BP}.tar.bz2 \
    file://0001-configure.ac-remove-additional-c-test.patch \
"
SRC_URI[md5sum] = "bd457caba57099cfa23b063e78b6f819"
SRC_URI[sha256sum] = "e38530e87c577e1e9806be3b40e08fb9ee321eb1abc6361ddacdad89c825f90d"

FILES_${PN} += "${datadir}/icons"
