SUMMARY = "C library for encoding data in a QR Code symbol"
AUTHOR = "Kentaro Fukuchi"
HOMEPAGE = "http://fukuchi.org/works/qrencode/"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRCREV = "715e29fd4cd71b6e452ae0f4e36d917b43122ce8"
SRC_URI = "git://github.com/fukuchi/libqrencode.git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--without-tools --without-tests"
