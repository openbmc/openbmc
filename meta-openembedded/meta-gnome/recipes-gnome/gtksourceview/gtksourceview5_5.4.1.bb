SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    glib-2.0-native \
    gnome-common-native \
    intltool-native \
    gtk+3 \
    gtk4 \
    libxml2 \
    libpcre2 \
"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase lib_package gettext features_check gtk-doc gtk-icon-cache gobject-introspection vala

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.4/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "eb3584099cfa0adc9a0b1ede08def6320bd099e79e74a2d0aefb4057cd93d68e"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES:${PN} += "${datadir}/gtksourceview-5"
