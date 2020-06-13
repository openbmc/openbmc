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

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtksourceview/4.6/${PNAME}-${PV}.tar.xz"
SRC_URI[md5sum] = "51558b386cd12602b7bb1c460c09bc62"
SRC_URI[sha256sum] = "4c13e30ab2e602abdc56f55d35f43c1142a79b1cd77aa8839d2fc85e966d9a85"

GIR_MESON_OPTION = 'gir'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES_${PN} += "${datadir}/gtksourceview-4"
