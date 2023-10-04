SUMMARY = "ncurses IRC client"
DESCRIPTION = "Irssi is an ncurses IRC client"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55fdc1113306167d6ea2561404ce02f8"

DEPENDS = "glib-2.0 ncurses openssl"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "fefe9ec8c7b1475449945c934a2360ab12693454892be47a6d288c63eb107ead"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-textui \
                 --with-proxy \
                 --with-bot \
                 --with-perl=no \
                 --enable-true-color"

FILES:${PN}-staticdev += "${libdir}/${BPN}/modules/*.a"
