SUMMARY = "ncurses IRC client"
DESCRIPTION = "Irssi is an ncurses IRC client"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55fdc1113306167d6ea2561404ce02f8"

DEPENDS = "glib-2.0 ncurses openssl"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "381d3af259ad15d658be50c0a01f0c28"
SRC_URI[sha256sum] = "a647bfefed14d2221fa77b6edac594934dc672c4a560417b1abcbbc6b88d769f"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-textui \
                 --with-proxy \
                 --with-bot \
                 --with-perl=no \
                 --enable-true-color"

FILES:${PN}-staticdev += "${libdir}/${BPN}/modules/*.a"
