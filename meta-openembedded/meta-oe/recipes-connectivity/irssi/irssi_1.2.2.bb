SUMMARY = "ncurses IRC client"
DESCRIPTION = "Irssi is an ncurses IRC client"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55fdc1113306167d6ea2561404ce02f8"

DEPENDS = "glib-2.0 ncurses openssl"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "8547f89e014e23e1bbbb665bcf7e2f70"
SRC_URI[sha256sum] = "6727060c918568ba2ff4295ad736128dba0b995d7b20491bca11f593bd857578"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-textui \
                 --with-proxy \
                 --with-bot \
                 --with-perl=no \
                 --enable-true-color"

FILES_${PN}-staticdev += "${libdir}/${BPN}/modules/*.a"
