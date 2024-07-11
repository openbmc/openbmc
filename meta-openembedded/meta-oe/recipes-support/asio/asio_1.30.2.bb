SUMMARY = "Asio is C++ library for network and low-level I/O programming"
DESCRIPTION = "Asio is a cross-platform C++ library for network and low-level \
        I/O programming that provides developers with a consistent asynchronous \
        model using a modern C++ approach."
HOMEPAGE = "http://think-async.com/Asio"
SECTION = "libs"
LICENSE = "BSL-1.0"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

inherit autotools

ALLOW_EMPTY:${PN} = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=2bfc909e030aeafefa72f764165b8d07"

SRC_URI[sha256sum] = "9f12cef05c0477eace9c68ccabd19f9e3a04b875d4768c323714cbd3a5fa3c2b"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost=${STAGING_LIBDIR},--without-boost,boost"

BBCLASSEXTEND = "native nativesdk"
