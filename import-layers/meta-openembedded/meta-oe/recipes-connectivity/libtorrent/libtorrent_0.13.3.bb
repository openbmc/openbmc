DESCRIPTION = "libTorrent is a BitTorrent library written in C++ for *nix, \
with a focus on high performance and good code."
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libsigc++-2.0 openssl cppunit"

SRC_URI = "http://libtorrent.rakshasa.no/downloads/${BP}.tar.gz \
    file://don-t-run-code-while-configuring-package.patch \
"

SRC_URI[md5sum] = "e94f6c590bb02aaf4d58618f738a85f2"
SRC_URI[sha256sum] = "34317d6783b7f8d0805274c9467475b5432a246c0de8e28fc16e3b0b43f35677"

inherit autotools pkgconfig

