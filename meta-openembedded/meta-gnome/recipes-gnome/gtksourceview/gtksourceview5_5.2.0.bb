SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPLv2.1"
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

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.2/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "c9b34fa02654f56ce22fa08827d89db4ba81631b2e6d7d31ea65d13c729430e9"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

FILES:${PN} += "${datadir}/gtksourceview-5"
