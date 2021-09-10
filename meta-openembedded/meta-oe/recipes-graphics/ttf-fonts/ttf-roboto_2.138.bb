require ttf.inc

SUMMARY = "Roboto fonts"
HOMEPAGE = "https://fonts.google.com/specimen/Roboto"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "https://github.com/google/roboto/releases/download/v${PV}/roboto-android.zip"
SRC_URI[md5sum] = "3b43a5cb33196ec25e44d5fcb40219e1"
SRC_URI[sha256sum] = "c825453253f590cfe62557733e7173f9a421fff103b00f57d33c4ad28ae53baf"

S = "${WORKDIR}"

FILES:${PN} = "${datadir}/fonts/truetype/*.ttf"
