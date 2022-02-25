SUMMARY = "Text-based modem control and terminal emulation program"
HOMEPAGE = "https://salsa.debian.org/minicom-team/minicom"
DESCRIPTION = "Minicom is a text-based modem control and terminal emulation program for Unix-like operating systems"
SECTION = "console/network"
DEPENDS = "ncurses virtual/libiconv"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
                    file://src/minicom.h;beginline=1;endline=12;md5=a58838cb709f0db517f4e42730c49e81"

SRC_URI = "${DEBIAN_MIRROR}/main/m/${BPN}/${BPN}_${PV}.orig.tar.bz2 \
           file://allow.to.disable.lockdev.patch \
           file://0001-fix-minicom-h-v-return-value-is-not-0.patch \
"

SRC_URI[sha256sum] = "38cea30913a20349326ff3f1763ee1512b7b41601c24f065f365e18e9db0beba"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lockdev] = "--enable-lockdev,--disable-lockdev,lockdev"

inherit autotools gettext pkgconfig

do_install() {
	for d in doc extras man lib src; do make -C $d DESTDIR=${D} install; done
}

RRECOMMENDS:${PN} += "lrzsz"

RDEPENDS:${PN} += "ncurses-terminfo-base"
