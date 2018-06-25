SUMMARY = "Install one of these tasks to get support for truetype fonts"
SECTION = "fonts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PV = "1.0"
PR = "r2"

inherit packagegroup

PACKAGES += "\
    ${PN}-core \
    ${PN}-chinese \
    ${PN}-japanese \
"

RRECOMMENDS_${PN} = "\
    ${PN}-core \
    ${PN}-chinese \
    ${PN}-japanese \
"

RDEPENDS_${PN}-core = "\
    fontconfig-utils \
    \
    ttf-dejavu-common \
    ttf-dejavu-sans \
    ttf-dejavu-sans-mono \
"
#  ttf-dejavu-serif

RDEPENDS_${PN}-chinese = "\
    ${PN}-core \
    ttf-arphic-uming \
"

RDEPENDS_${PN}-japanese = "\
    ${PN}-core \
    ttf-sazanami-gothic \
    ttf-sazanami-mincho \
"
