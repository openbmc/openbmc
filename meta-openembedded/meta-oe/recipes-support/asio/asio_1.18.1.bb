SUMMARY = "Asio is C++ library for network and low-level I/O programming"
DESCRIPTION = "Asio is a cross-platform C++ library for network and low-level \
        I/O programming that provides developers with a consistent asynchronous \
        model using a modern C++ approach."
AUTHOR = "Christopher M. Kohlhoff (chris at kohlhoff dot com)"
HOMEPAGE = "http://think-async.com/Asio"
SECTION = "libs"
LICENSE = "BSL-1.0"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

inherit autotools

ALLOW_EMPTY_${PN} = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=de86c8210a433f72bd3cc98e797a6084"

SRC_URI[md5sum] = "00807b2e976f467b3cec85d1589f0825"
SRC_URI[sha256sum] = "4af9875df5497fdd507231f4b7346e17d96fc06fe10fd30e2b3750715a329113"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost=${STAGING_LIBDIR},--without-boost,boost"

BBCLASSEXTEND = "native nativesdk"
