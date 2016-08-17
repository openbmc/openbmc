SUMMARY = "Text-based modem control and terminal emulation program"
DESCRIPTION = "Minicom is a text-based modem control and terminal emulation program for Unix-like operating systems"
SECTION = "console/network"
DEPENDS = "ncurses virtual/libiconv"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
                    file://src/minicom.h;beginline=1;endline=12;md5=a58838cb709f0db517f4e42730c49e81"

SRC_URI = "https://alioth.debian.org/frs/download.php/latestfile/3/${BP}.tar.gz \
           file://allow.to.disable.lockdev.patch \
           file://0001-fix-minicom-h-v-return-value-is-not-0.patch \
           file://0001-Fix-build-issus-surfaced-due-to-musl.patch \
          "

SRC_URI[md5sum] = "7044ca3e291268c33294f171d426dc2d"
SRC_URI[sha256sum] = "9ac3a663b82f4f5df64114b4792b9926b536c85f59de0f2d2b321c7626a904f4"

UPSTREAM_CHECK_URI = "https://alioth.debian.org/frs/?group_id=30018"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lockdev] = "--enable-lockdev,--disable-lockdev,lockdev"

inherit autotools gettext pkgconfig

do_install() {
	for d in doc extras man lib src; do make -C $d DESTDIR=${D} install; done
}

