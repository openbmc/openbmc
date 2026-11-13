SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    fribidi \
    glib-2.0-native \
    fontconfig \
    gtk4 \
    libxml2 \
    libpcre2 \
    pango \
"

inherit gnomebase lib_package gettext features_check gi-docgen gtk-icon-cache gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "opengl"

GNOMEBN = "gtksourceview"

SRC_URI[archive.sha256sum] = "65f7ce9d2ec683c6ff9b27a50d909f3abef65f742d11011e1b8f8b848cadf0f7"
S = "${UNPACKDIR}/gtksourceview-${PV}"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_OPTION = "documentation"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)}"
PACKAGECONFIG[vulkan] = ",,vulkan-loader vulkan-headers"

FILES:${PN} += "${datadir}/gtksourceview-5"
