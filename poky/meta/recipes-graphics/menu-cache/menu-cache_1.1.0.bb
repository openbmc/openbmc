SUMMARY = "Library for caching application menus"
DESCRIPTION = "A library creating and utilizing caches to speed up freedesktop.org application menus"
HOMEPAGE = "http://lxde.sourceforge.net/"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0964c689fcf4c21c6797ea87408416b6"

SECTION = "x11/libs"
DEPENDS = "glib-2.0 libfm-extra"

SRC_URI = "${SOURCEFORGE_MIRROR}/lxde/menu-cache-${PV}.tar.xz \
           file://0001-Support-gcc10-compilation.patch \
"

SRC_URI[md5sum] = "99999a0bca48b980105208760c8fd893"
SRC_URI[sha256sum] = "ed02eb459dcb398f69b9fa5bf4dd813020405afc84331115469cdf7be9273ec7"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/lxde/files/menu-cache/1.1/"

inherit autotools gettext pkgconfig gtk-doc
