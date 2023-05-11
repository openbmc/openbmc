SUMMARY = "Google Hoth USB library"
DESCRIPTION = "A library implements Google USB protocol to communication with Hoth device"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://github.com/google/libhoth;protocol=https;branch=main"
SRCREV = "769296220dc88df33f4726aa11e39e049257b3c4"

PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit pkgconfig meson

DEPENDS += "libusb1"