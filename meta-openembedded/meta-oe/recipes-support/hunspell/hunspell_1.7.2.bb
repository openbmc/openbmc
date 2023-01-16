SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPL-2.0-only | LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING.LESSER;md5=c96ca6c1de8adc025adfada81d06fba5 \
"

SRCREV = "2969be996acad84b91ab3875b1816636fe61a40e"
SRC_URI = "git://github.com/${BPN}/${BPN}.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

RDEPENDS:${PN} = "perl"

BBCLASSEXTEND = "native"
