SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 intltool-native gnome-common-native glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase lib_package gettext features_check gtk-doc gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtksourceview/4.4/${PNAME}-${PV}.tar.xz"
SRC_URI[md5sum] = "5c47e314517692f2622a0b724c65e628"
SRC_URI[sha256sum] = "9ddb914aef70a29a66acd93b4f762d5681202e44094d2d6370e51c9e389e689a"

GIR_MESON_OPTION = 'gir'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES_${PN} += "${datadir}/gtksourceview-4"
