require ttf.inc

SUMMARY = "Ethiopia and Eritrea (Amharic) font - TTF Edition"
HOMEPAGE = "http://software.sil.org/abyssinica/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=6c56db018aa8803f8aff326829e3ce32"

SRCNAME = "AbyssinicaSIL"
SRC_URI = "http://software.sil.org/downloads/r/abyssinica/${SRCNAME}-${PV}.zip"
S = "${WORKDIR}/${SRCNAME}-${PV}"

UPSTREAM_CHECK_URI = "https://software.sil.org/abyssinica/download/"

SRC_URI[sha256sum] = "725e6cd58be6495e0e53f367608db36d9f818498bccba739d742892c5a3d8151"

FONT_PACKAGES = "${PN}"
FILES:${PN} = "${datadir}"
