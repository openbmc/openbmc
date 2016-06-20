SUMMARY = "Library for caching application menus"
DESCRIPTION = "A library creating and utilizing caches to speed up freedesktop.org application menus"
HOMEPAGE = "http://lxde.sourceforge.net/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0964c689fcf4c21c6797ea87408416b6"

SECTION = "x11/libs"
DEPENDS = "glib-2.0 intltool-native libfm-extra"

SRC_URI = "${SOURCEFORGE_MIRROR}/lxde/menu-cache-${PV}.tar.xz"

SRC_URI[md5sum] = "a856ba860b16fdc8c69ee784bc4ade36"
SRC_URI[sha256sum] = "0ac72649919946070258320aafc320467dd040bcef7b3a225e2ab7241ddffd59"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/lxde/files/menu-cache/1.0/"

inherit autotools gettext pkgconfig gtk-doc
