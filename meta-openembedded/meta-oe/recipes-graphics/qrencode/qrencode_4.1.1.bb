SUMMARY = "C library for encoding data in a QR Code symbol"
HOMEPAGE = "http://fukuchi.org/works/qrencode/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRCREV = "715e29fd4cd71b6e452ae0f4e36d917b43122ce8"
SRC_URI = "git://github.com/fukuchi/libqrencode.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"

EXTRA_OECONF += "--without-tests"

PACKAGECONFIG ??= ""
PACKAGECONFIG[tools] = "--with-tools,--without-tools,libpng"
