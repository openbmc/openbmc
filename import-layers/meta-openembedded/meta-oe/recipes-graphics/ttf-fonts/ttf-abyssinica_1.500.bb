require ttf.inc

SUMMARY = "Ethiopia and Eritrea (Amharic) font - TTF Edition"
HOMEPAGE = "http://software.sil.org/abyssinica/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=1694c7fc245cdc85c9971db707928159"

SRCNAME = "AbyssinicaSIL"
SRC_URI = "http://software.sil.org/downloads/r/abyssinica/${SRCNAME}-${PV}.zip"
S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[md5sum] = "a3d943d18e303197c8d3d92d2de54d1e"
SRC_URI[sha256sum] = "e48a77d5ab8ee0b06464a5b29be70f292aa25dc1e73eb39ec933bd7fa47bbd86"

FONT_PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"

