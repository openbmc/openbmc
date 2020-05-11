require ttf.inc

SUMMARY = "Hunky fonts - TTF Version"
HOMEPAGE = "http://sourceforge.net/projects/hunkyfonts"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://../COPYRIGHT.TXT;md5=70d34478e38b1ad9995079f9921f9ef7"
PR = "r7"

SRC_URI = "${SOURCEFORGE_MIRROR}/hunkyfonts/hunkyfonts-${PV}.tar.bz2"

S = "${WORKDIR}/hunkyfonts-${PV}/TTF/"

PACKAGES = "ttf-hunky-sans ttf-hunky-serif"
FONT_PACKAGES = "ttf-hunky-sans ttf-hunky-serif"

FILES_ttf-hunky-sans = "${datadir}/fonts/truetype/HunkySans*.ttf"
FILES_ttf-hunky-serif = "${datadir}/fonts/truetype/HunkySerif*.ttf"

SRC_URI[md5sum] = "b933312967842e5737b5415fa22d682a"
SRC_URI[sha256sum] = "3fc528737ccd12ec3c09c4a91447d241d3c5bceeeb4d24b7f2c29b15c9735328"
