SUMMARY = "Torrent client"
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsigc++-2.0 curl cppunit libtorrent ncurses"

SRC_URI = "git://github.com/rakshasa/rtorrent \
    file://don-t-run-code-while-configuring-package.patch \
"
# v0.9.7
SRCREV = "327164f9d86aafcd2500a317d485374df32ea622"

PV = "0.9.7+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

inherit autotools pkgconfig

do_configure_prepend() {
    (cd ${S}; ./autogen.sh; cd -)
}
