require ttf.inc

SUMMARY = "Ethiopia and Eritrea (Amharic) font - TTF Edition"
HOMEPAGE = "http://software.sil.org/abyssinica/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=347eaa34fdf555aaf6b2144a5ccca45b"

SRCNAME = "AbyssinicaSIL"
SRC_URI = "http://software.sil.org/downloads/r/abyssinica/${SRCNAME}-${PV}.zip"
S = "${WORKDIR}/${SRCNAME}-${PV}"

UPSTREAM_CHECK_URI = "https://software.sil.org/abyssinica/download/"

SRC_URI[sha256sum] = "961259a1d9ace083f737eb1e55cec40c9a56f4855866d7474bf212d2a4366ab8"

FONT_PACKAGES = "${PN}"
FILES:${PN} = "${datadir}"
