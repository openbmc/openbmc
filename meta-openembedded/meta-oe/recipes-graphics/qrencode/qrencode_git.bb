SUMMARY = "C library for encoding data in a QR Code symbol"
AUTHOR = "Kentaro Fukuchi"
HOMEPAGE = "http://fukuchi.org/works/qrencode/"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
PV = "4.0.1+git${SRCPV}"

SRCREV = "7c83deb8f562ae6013fea4c3e65278df93f98fb7"
SRC_URI = "git://github.com/fukuchi/libqrencode.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--without-tools --without-tests"
