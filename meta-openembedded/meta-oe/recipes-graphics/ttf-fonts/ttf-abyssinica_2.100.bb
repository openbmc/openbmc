require ttf.inc

SUMMARY = "Ethiopia and Eritrea (Amharic) font - TTF Edition"
HOMEPAGE = "http://software.sil.org/abyssinica/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=fd664aaab50445c3c1c97544554a6bda"

SRCNAME = "AbyssinicaSIL"
SRC_URI = "http://software.sil.org/downloads/r/abyssinica/${SRCNAME}-${PV}.zip"
S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[sha256sum] = "1a1fc8e82f0c0f2e824f42bff05c1f4a0356330a89dc7ba798c1a20bc3e333e0"

FONT_PACKAGES = "${PN}"
FILES:${PN} = "${datadir}"
