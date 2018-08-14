SUMMARY = "C library for encoding data in a QR Code symbol"
AUTHOR = "Kentaro Fukuchi"
HOMEPAGE = "http://fukuchi.org/works/qrencode/"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
PV = "4.0.0+git${SRCPV}"

SRCREV = "07f3c5d4bf9136711422cc7dbf28aff469da220a"
SRC_URI = "git://github.com/fukuchi/libqrencode.git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--without-tools --without-tests"
