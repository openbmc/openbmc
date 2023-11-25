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


inherit gnomebase lib_package gettext features_check gi-docgen gtk-icon-cache gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.10/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "b38a3010c34f59e13b05175e9d20ca02a3110443fec2b1e5747413801bc9c23f"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)}"
PACKAGECONFIG[vulkan] = ",,vulkan-loader vulkan-headers"

FILES:${PN} += "${datadir}/gtksourceview-5"
