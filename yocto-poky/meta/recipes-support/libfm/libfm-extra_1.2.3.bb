SUMMARY = "Library for file management"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/fm-extra.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007"

SECTION = "x11/libs"
DEPENDS = "glib-2.0 intltool-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-nls.m4-Take-it-from-gettext-0.15.patch \
          "

SRC_URI[md5sum] = "3ff38200701658f7e80e25ed395d92dd"
SRC_URI[sha256sum] = "c692f1624a4cbc8d1dd55f3b3f3369fbf5d26f63a916e2c295230b2344e1fbf9"

S = "${WORKDIR}/libfm-${PV}"

EXTRA_OECONF = "--with-extra-only --with-gtk=no"

PR = "r1"

inherit autotools pkgconfig gtk-doc

do_configure[dirs] =+ "${S}/m4"
