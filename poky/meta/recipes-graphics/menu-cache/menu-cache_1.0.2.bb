SUMMARY = "Library for caching application menus"
DESCRIPTION = "A library creating and utilizing caches to speed up freedesktop.org application menus"
HOMEPAGE = "http://lxde.sourceforge.net/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0964c689fcf4c21c6797ea87408416b6"

SECTION = "x11/libs"
DEPENDS = "glib-2.0 libfm-extra"

SRC_URI = "${SOURCEFORGE_MIRROR}/lxde/menu-cache-${PV}.tar.xz"

SRC_URI[md5sum] = "8dde7a3f5bd9798d0129d1979a5d7640"
SRC_URI[sha256sum] = "6f83edf2de34f83e701dcb52145d755250a5677580cd413476cc4d7f2d2012d5"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/lxde/files/menu-cache/1.0/"

inherit autotools gettext pkgconfig gtk-doc
