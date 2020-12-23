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

SRC_URI[md5sum] = "ed1f2dc4cf24aa2da8361179ade01682"
SRC_URI[sha256sum] = "9d539e7c09aa6394d512c433c5601c1f26dc4975f022ad7d5e8e57c3b635b370"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost=${STAGING_LIBDIR},--without-boost,boost"

BBCLASSEXTEND = "native nativesdk"
