require ttf.inc

SUMMARY = "Ethiopia and Eritrea (Amharic) font - TTF Edition"
HOMEPAGE = "http://software.sil.org/abyssinica/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=80cc8cdcdc3f8ce96957bbac946b70ae"

SRCNAME = "AbyssinicaSIL"
SRC_URI = "http://software.sil.org/downloads/r/abyssinica/${SRCNAME}-${PV}.zip"
S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[sha256sum] = "274204a53b30f64cca662d78c7199e3c0325ea95ad4109886b47af734c92d0f9"

FONT_PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"
