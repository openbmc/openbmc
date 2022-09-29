SUMMARY = "Google Hoth USB library"
DESCRIPTION = "A library implements Google USB protocol to communication with Hoth device"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://github.com/google/libhoth;protocol=https;branch=main"
SRCREV = "b31307b7bf525efda0164e461fb1e0a5c302d306"

PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit pkgconfig meson

DEPENDS += "libusb1"