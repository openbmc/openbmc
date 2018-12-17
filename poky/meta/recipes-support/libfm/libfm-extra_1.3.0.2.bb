SUMMARY = "Library for file management"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/fm-extra.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007"

SECTION = "x11/libs"
DEPENDS = "glib-2.0-native glib-2.0 intltool-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-nls.m4-Take-it-from-gettext-0.15.patch \
          "

SRC_URI[md5sum] = "02a024714d51e0d37afc7bd596a44f3b"
SRC_URI[sha256sum] = "18d06f7996ce1cf8947df6e106bc0338c6ae0c4138c316f2501f6f6f435c7c72"

S = "${WORKDIR}/libfm-${PV}"

EXTRA_OECONF = "--with-extra-only --with-gtk=no"

inherit autotools pkgconfig gtk-doc gettext

do_configure[dirs] =+ "${S}/m4"
