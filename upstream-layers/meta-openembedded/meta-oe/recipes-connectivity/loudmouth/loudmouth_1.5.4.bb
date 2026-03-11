DESCRIPTION = "Loudmouth is a lightweight and easy-to-use C library for programming with the Jabber protocol."
HOMEPAGE = "http://www.loudmouth-project.org/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 libcheck openssl libidn"

GNOMEBASEBUILDCLASS = "autotools"

inherit gnomebase gtk-doc

SRC_URI = "https://github.com/mcabber/${BPN}/releases/download/${PV}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "31cbc91c1fddcc5346b3373b8fb45594e9ea9cc7fe36d0595e8912c47ad94d0d"

EXTRA_OECONF = "--with-ssl=openssl --with-compile-warnings=no"
