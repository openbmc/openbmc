SUMMARY = "Library for file management"
DESCRIPTION = "Contains a library and other files required by menu-cache-gen libexec of menu-cache-1.1.0. "
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/fm-extra.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007"

SECTION = "x11/libs"
DEPENDS = "glib-2.0-native glib-2.0 intltool-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-nls.m4-Take-it-from-gettext-0.15.patch \
          "

SRC_URI[sha256sum] = "a5042630304cf8e5d8cff9d565c6bd546f228b48c960153ed366a34e87cad1e5"

S = "${WORKDIR}/libfm-${PV}"

EXTRA_OECONF = "--with-extra-only --with-gtk=no"

inherit autotools pkgconfig gtk-doc gettext

do_configure[dirs] =+ "${S}/m4"
