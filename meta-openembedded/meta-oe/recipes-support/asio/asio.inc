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
