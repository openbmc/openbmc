SUMMARY = "Library for file management"
DESCRIPTION = "Contains a library and other files required by menu-cache-gen libexec of menu-cache-1.1.0. "
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/fm-extra.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007"

SECTION = "x11/libs"
DEPENDS = "glib-2.0-native glib-2.0 intltool-native"

SOURCEFORGE_PROJECT = "pcmanfm"
SRC_URI = "${SOURCEFORGE_MIRROR}/${SOURCEFORGE_PROJECT}/libfm-${PV}.tar.xz \
          "

SRC_URI[sha256sum] = "a5042630304cf8e5d8cff9d565c6bd546f228b48c960153ed366a34e87cad1e5"

S = "${WORKDIR}/libfm-${PV}"

EXTRA_OECONF = "--with-extra-only --with-gtk=no"

inherit autotools pkgconfig gtk-doc gettext sourceforge-releases

do_configure[dirs] =+ "${S}/m4"
