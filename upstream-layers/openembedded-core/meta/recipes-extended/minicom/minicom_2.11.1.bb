SUMMARY = "Text-based modem control and terminal emulation program"
HOMEPAGE = "https://salsa.debian.org/minicom-team/minicom"
DESCRIPTION = "Minicom is a text-based modem control and terminal emulation program for Unix-like operating systems"
SECTION = "console/network"
DEPENDS = "ncurses virtual/libiconv"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
                    file://src/minicom.h;beginline=1;endline=10;md5=5c0ff1e7befaa8f7d7f570d77b1a88ef \
                    "

SRC_URI = "${DEBIAN_MIRROR}/main/m/${BPN}/${BPN}_${PV}.orig.tar.bz2"

SRC_URI[sha256sum] = "87cf0da91af0531357cd61b8e1906b907edd2c9ef82f9ae74c277e1893d0f98c"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lockdev] = "--enable-lockdev,--disable-lockdev,lockdev"

inherit autotools gettext pkgconfig

do_install() {
	for d in doc extras man lib src; do make -C $d DESTDIR=${D} install; done
}

RRECOMMENDS:${PN} += "lrzsz"

RDEPENDS:${PN} += "ncurses-terminfo-base"
