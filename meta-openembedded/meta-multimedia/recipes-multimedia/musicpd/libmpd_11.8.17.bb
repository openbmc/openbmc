SUMMARY = "Music Player Daemon library"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE ="http://sourceforge.net/projects/musicpd"
DEPENDS = "glib-2.0"

SRC_URI = "http://www.musicpd.org/download/${BPN}/${PV}/${BP}.tar.gz \
    file://glibc-2.20.patch \
"
SRC_URI[md5sum] = "5ae3d87467d52aef3345407adb0a2488"
SRC_URI[sha256sum] = "fe20326b0d10641f71c4673fae637bf9222a96e1712f71f170fca2fc34bf7a83"

inherit autotools pkgconfig
