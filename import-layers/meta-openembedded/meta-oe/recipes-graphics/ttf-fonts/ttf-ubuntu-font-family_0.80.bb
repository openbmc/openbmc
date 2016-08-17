require ttf.inc

SUMMARY = "Ubuntu Font Family - TTF Version"
HOMEPAGE = "http://font.ubuntu.com"
LICENSE = "UFL"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=325a1a9029112a2405e743c7f816427b"
PR = "r1"

SRC_URI = "http://font.ubuntu.com/download/ubuntu-font-family-${PV}.zip"

SRC_URI[md5sum] = "a1fc70f5a5b1d096ab8310886cddaa1c"
SRC_URI[sha256sum] = "107170099bbc3beae8602b97a5c423525d363106c3c24f787d43e09811298e4c"

S = "${WORKDIR}/ubuntu-font-family-${PV}"

PACKAGES = "ttf-ubuntu-mono ttf-ubuntu-sans"
FONT_PACKAGES = "ttf-ubuntu-mono ttf-ubuntu-sans"

FILES_ttf-ubuntu-mono  = "${datadir}/fonts/truetype/*Mono*"
FILES_ttf-ubuntu-sans  = "${datadir}/fonts/truetype/Ubuntu-*"
