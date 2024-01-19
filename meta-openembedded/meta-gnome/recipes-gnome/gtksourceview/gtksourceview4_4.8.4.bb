SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

inherit gnomebase lib_package gettext features_check gtk-doc gobject-introspection vala

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/4.8/${PNAME}-${PV}.tar.xz \
           file://0001-remove-pointless-check.patch"
SRC_URI[sha256sum] = "7ec9d18fb283d1f84a3a3eff3b7a72b09a10c9c006597b3fbabbb5958420a87d"

GIR_MESON_OPTION = 'gir'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES:${PN} += "${datadir}/gtksourceview-4"
