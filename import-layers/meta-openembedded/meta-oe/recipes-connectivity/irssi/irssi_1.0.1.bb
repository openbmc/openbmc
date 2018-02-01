SUMMARY = "ncurses IRC client"
DESCRIPTION = "Irssi is an ncurses IRC client"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55fdc1113306167d6ea2561404ce02f8"

DEPENDS = "glib-2.0 ncurses openssl"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "f6bed196cef63ea089f5cce089784445"
SRC_URI[sha256sum] = "9428c51a3f3598ffaef438c351a8d609cf10db34f2435bdcb84456226c383ccf"

inherit autotools pkgconfig
