SUMMARY = "Text-based modem control and terminal emulation program"
HOMEPAGE = "http://alioth.debian.org/projects/minicom/"
DESCRIPTION = "Minicom is a text-based modem control and terminal emulation program for Unix-like operating systems"
SECTION = "console/network"
DEPENDS = "ncurses virtual/libiconv"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
                    file://src/minicom.h;beginline=1;endline=12;md5=a58838cb709f0db517f4e42730c49e81"

SRC_URI = "${DEBIAN_MIRROR}/main/m/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://allow.to.disable.lockdev.patch \
           file://0001-fix-minicom-h-v-return-value-is-not-0.patch \
           file://0001-Fix-build-issus-surfaced-due-to-musl.patch \
          "

SRC_URI[md5sum] = "9021cb8c5445f6e6e74b2acc39962d62"
SRC_URI[sha256sum] = "532f836b7a677eb0cb1dca8d70302b73729c3d30df26d58368d712e5cca041f1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lockdev] = "--enable-lockdev,--disable-lockdev,lockdev"

inherit autotools gettext pkgconfig

do_install() {
	for d in doc extras man lib src; do make -C $d DESTDIR=${D} install; done
}

RRECOMMENDS_${PN} += "lrzsz"
