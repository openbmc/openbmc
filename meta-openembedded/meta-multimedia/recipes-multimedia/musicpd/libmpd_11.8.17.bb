SUMMARY = "Music Player Daemon library"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE ="http://sourceforge.net/projects/musicpd"
DEPENDS = "glib-2.0"

SRC_URI = "http://www.musicpd.org/download/${BPN}/${PV}/${BP}.tar.gz \
           file://0001-fix-return-makes-integer-from-pointer-without-a-cast.patch \
           file://0002-fix-comparison-between-pointer-and-zero-character-co.patch \
           file://0003-include-config.h.patch \
"
SRC_URI[sha256sum] = "fe20326b0d10641f71c4673fae637bf9222a96e1712f71f170fca2fc34bf7a83"

inherit autotools pkgconfig
