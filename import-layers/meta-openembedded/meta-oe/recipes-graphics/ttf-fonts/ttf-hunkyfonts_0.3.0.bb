require ttf.inc

SUMMARY = "Hunky fonts - TTF Version"
HOMEPAGE = "http://sourceforge.net/projects/hunkyfonts"
LICENSE = "LGPL"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=7fbc338309ac38fefcd64b04bb903e34"
PR = "r7"

SRC_URI = "${SOURCEFORGE_MIRROR}/hunkyfonts/hunkyfonts-${PV}.tar.bz2"

S = "${WORKDIR}/hunkyfonts-${PV}/TTF/"

PACKAGES = "ttf-hunky-sans ttf-hunky-serif"
FONT_PACKAGES = "ttf-hunky-sans ttf-hunky-serif"

FILES_ttf-hunky-sans = "${datadir}/fonts/truetype/HunkySans*.ttf"
FILES_ttf-hunky-serif = "${datadir}/fonts/truetype/HunkySerif*.ttf"

SRC_URI[md5sum] = "36444795a356fb8a56c63b2840729bab"
SRC_URI[sha256sum] = "e0b1849c545b6af276407d93025c73094dd74fc259b07c1d91594fdbb9a0b829"
