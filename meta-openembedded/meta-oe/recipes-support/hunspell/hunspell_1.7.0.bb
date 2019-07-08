SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPLv2 | LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING.LESSER;md5=c96ca6c1de8adc025adfada81d06fba5 \
"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "b2399a4aa927e8b3e171d9ea6737cc02"
SRC_URI[sha256sum] = "bb27b86eb910a8285407cf3ca33b62643a02798cf2eef468c0a74f6c3ee6bc8a"

inherit autotools pkgconfig gettext

RDEPENDS_${PN} = "perl"

BBCLASSEXTEND = "native"
