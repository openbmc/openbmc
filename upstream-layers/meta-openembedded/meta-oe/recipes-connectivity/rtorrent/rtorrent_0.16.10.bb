SUMMARY = "Torrent client"
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "autoconf-archive libsigc++-2.0 curl cppunit libtool libtorrent ncurses"

SRC_URI = "git://github.com/rakshasa/rtorrent;branch=master;protocol=https;tag=v${PV}"

SRCREV = "b5a606649a4800deb44c31d3ad74d01b4c5ca37a"

inherit autotools pkgconfig

EXTRA_OECONF:append:libc-musl = " --disable-execinfo"
