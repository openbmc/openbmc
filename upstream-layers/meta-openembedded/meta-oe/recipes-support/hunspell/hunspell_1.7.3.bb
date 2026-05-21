SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPL-2.0-only | LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING.LESSER;md5=c96ca6c1de8adc025adfada81d06fba5 \
"

SRCREV = "c5f98152a274e25b5107101104bef632b83a0cc9"
SRC_URI = "git://github.com/${BPN}/${BPN}.git;branch=master;protocol=https;tag=v${PV}"


inherit autotools pkgconfig gettext

# ispellaff2myspell: A program to convert ispell affix tables to myspell format
PACKAGES =+ "${PN}-ispell"
FILES:${PN}-ispell = "${bindir}/ispellaff2myspell"
RDEPENDS:${PN}-ispell = "perl"

BBCLASSEXTEND = "native"
