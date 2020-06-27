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

SRC_URI[md5sum] = "569f4b082c652ae8a8ad94fb470016f9"
SRC_URI[sha256sum] = "e271db76dbbcda9835ed1c9c94deb2ba3f4589c3ebcaa71d99ac694b8d62638c"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost,--without-boost,boost"
