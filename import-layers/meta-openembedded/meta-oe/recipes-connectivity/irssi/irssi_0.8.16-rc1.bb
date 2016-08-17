SUMMARY = "ncurses IRC client"
DESCRIPTION = "Irssi is an ncurses IRC client"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55fdc1113306167d6ea2561404ce02f8"

DEPENDS = "glib-2.0 ncurses openssl"

REALPV = "0.8.16-rc1"
PV = "0.8.15+${REALPV}"

SRC_URI = "http://irssi.org/files/irssi-${REALPV}.tar.gz"

SRC_URI[md5sum] = "769fec4df8e633c583c411ccd2cd563a"
SRC_URI[sha256sum] = "bb6c0125db30b697f80837941c17372b7484c64d57a6920b8bfa7ee3def92de3"

S = "${WORKDIR}/irssi-${REALPV}"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-ssl \
    --with-ncurses=${STAGING_EXECPREFIXDIR} \
"
