require ttf.inc

SUMMARY = "Liberation fonts - TTF Version"
HOMEPAGE = "https://github.com/liberationfonts"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f96db970a9a46c5369142b99f530366b \
"


SRCREV = "4b0192046158094654e865245832c66d2104219e"
SRC_URI = "git://github.com/liberationfonts/liberation-fonts.git;branch=main;protocol=https"

DEPENDS = "fontforge-native"
S = "${WORKDIR}/git"

PACKAGES = "ttf-liberation-mono ttf-liberation-sans ttf-liberation-serif"
FONT_PACKAGES = "ttf-liberation-mono ttf-liberation-sans ttf-liberation-serif"

FILES:ttf-liberation-mono  = "${datadir}/fonts/truetype/*Mono*"
FILES:ttf-liberation-sans  = "${datadir}/fonts/truetype/*Sans*"
FILES:ttf-liberation-serif = "${datadir}/fonts/truetype/*Serif*"

inherit python3native

do_compile() {
    cd ${S}; make ttf;
}
