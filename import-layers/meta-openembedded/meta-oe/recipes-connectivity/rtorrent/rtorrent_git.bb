SUMMARY = "Torrent client"
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsigc++-2.0 curl cppunit libtorrent ncurses"

SRC_URI = "git://github.com/rakshasa/rtorrent \
    file://don-t-run-code-while-configuring-package.patch \
"
SRCREV = "226e670decf92e7adaa845a6982aca4f164ea740"

PV = "0.9.6+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

inherit autotools pkgconfig

do_configure_prepend() {
    (cd ${S}; ./autogen.sh; cd -)
}
