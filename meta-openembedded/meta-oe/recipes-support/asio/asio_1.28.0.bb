SUMMARY = "Asio is C++ library for network and low-level I/O programming"
DESCRIPTION = "Asio is a cross-platform C++ library for network and low-level \
        I/O programming that provides developers with a consistent asynchronous \
        model using a modern C++ approach."
HOMEPAGE = "http://think-async.com/Asio"
SECTION = "libs"
LICENSE = "BSL-1.0"

DEPENDS = "openssl"

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2 \
    file://0001-Add-the-pkgconfigdir-location.patch \
"

inherit autotools

ALLOW_EMPTY:${PN} = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=ff668366bbdb687b6029d33a5fe4b999"

SRC_URI[sha256sum] = "d0ddc2361abd2f4c823e970aaf8e28b4b31ab21b1a68af16b114fc093661e232"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost=${STAGING_LIBDIR},--without-boost,boost"

BBCLASSEXTEND = "native nativesdk"
