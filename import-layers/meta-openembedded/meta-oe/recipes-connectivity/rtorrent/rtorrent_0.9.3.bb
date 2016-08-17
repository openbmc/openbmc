SUMMARY = "Torrent client"
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsigc++-2.0 curl cppunit libtorrent ncurses"

SRC_URI = "http://libtorrent.rakshasa.no/downloads/${BP}.tar.gz \
    file://don-t-run-code-while-configuring-package.patch \
"

SRC_URI[md5sum] = "0bf2f262faa8c8c8d3b11ce286ea2bf2"
SRC_URI[sha256sum] = "9e93ca41beb1afe74ad7ad8013e0d53ae3586c9b0e97263d722f721535cc7310"

inherit autotools pkgconfig
