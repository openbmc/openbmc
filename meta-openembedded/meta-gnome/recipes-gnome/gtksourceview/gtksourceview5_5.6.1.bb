SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    fribidi \
    glib-2.0-native \
    gnome-common-native \
    fontconfig \
    gtk4 \
    libxml2 \
    libpcre2 \
    pango \
"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase lib_package gettext features_check gtk-doc gtk-icon-cache gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.6/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "659d9cc9d034a114f07e7e134ee80d77dec0497cb1516ae5369119c2fcb9da16"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)}"
PACKAGECONFIG[vulkan] = ",,vulkan-loader vulkan-headers"

FILES:${PN} += "${datadir}/gtksourceview-5"
