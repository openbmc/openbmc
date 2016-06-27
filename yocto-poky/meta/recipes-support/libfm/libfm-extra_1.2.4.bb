SUMMARY = "Library for file management"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/fm-extra.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007"

SECTION = "x11/libs"
DEPENDS = "glib-2.0 intltool-native gettext-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-nls.m4-Take-it-from-gettext-0.15.patch \
          "

SRC_URI[md5sum] = "74997d75e7e87dc73398746fd373bf52"
SRC_URI[sha256sum] = "7804f6f28cb3d1bc8ffb3151ab7ff0c063b27c5f9b06c682eb903e01cf25502f"

S = "${WORKDIR}/libfm-${PV}"

EXTRA_OECONF = "--with-extra-only --with-gtk=no"

inherit autotools pkgconfig gtk-doc

do_configure[dirs] =+ "${S}/m4"
