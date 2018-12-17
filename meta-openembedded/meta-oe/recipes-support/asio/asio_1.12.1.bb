DESCRIPTION = "Asio is a cross-platform C++ library for network and low-level \
        I/O programming that provides developers with a consistent asynchronous \
        model using a modern C++ approach."
AUTHOR = "Christopher M. Kohlhoff (chris at kohlhoff dot com)"
HOMEPAGE = "http://think-async.com/Asio"
SECTION = "libs"
LICENSE = "BSL-1.0"

DEPENDS = "boost openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

inherit autotools

ALLOW_EMPTY_${PN} = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=3e73f311a3af69e6df275e8c3b1c09b5"

SRC_URI[md5sum] = "037854d113024f57c9753d6326b339bc"
SRC_URI[sha256sum] = "a9091b4de847539fa5b2259bf76a5355339c7eaaa5e33d7d4ae74d614c21965a"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"
