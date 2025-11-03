SUMMARY = "Text-based modem control and terminal emulation program"
HOMEPAGE = "https://salsa.debian.org/minicom-team/minicom"
DESCRIPTION = "Minicom is a text-based modem control and terminal emulation program for Unix-like operating systems"
SECTION = "console/network"
DEPENDS = "ncurses virtual/libiconv"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
                    file://src/minicom.h;beginline=1;endline=12;md5=a58838cb709f0db517f4e42730c49e81"

SRC_URI = "https://salsa.debian.org/minicom-team/${BPN}/-/archive/${PV}/${BPN}-${PV}.tar.bz2"

SRC_URI[sha256sum] = "9efbb6458140e5a0de445613f0e76bcf12cbf7a9892b2f53e075c2e7beaba86c"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lockdev] = "--enable-lockdev,--disable-lockdev,lockdev"

inherit autotools gettext pkgconfig

do_install() {
	for d in doc extras man lib src; do make -C $d DESTDIR=${D} install; done
}

RRECOMMENDS:${PN} += "lrzsz"

RDEPENDS:${PN} += "ncurses-terminfo-base"
