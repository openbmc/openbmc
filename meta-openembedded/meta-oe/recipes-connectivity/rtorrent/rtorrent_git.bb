SUMMARY = "Torrent client"
HOMEPAGE = "http://libtorrent.rakshasa.no/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libsigc++-2.0 curl cppunit libtorrent ncurses"

SRC_URI = "git://github.com/rakshasa/rtorrent \
    file://don-t-run-code-while-configuring-package.patch \
"
# v0.9.8
SRCREV = "6154d1698756e0c4842b1c13a0e56db93f1aa947"

PV = "0.9.8"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

inherit autotools pkgconfig

do_configure_prepend() {
    (cd ${S}; ./autogen.sh; cd -)
}
