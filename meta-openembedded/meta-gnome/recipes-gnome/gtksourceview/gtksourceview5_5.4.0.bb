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

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.4/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "003bc217e670a8ec8aa3aece994b70e70b7d6b8074938adda21718555d84e637"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES:${PN} += "${datadir}/gtksourceview-5"
