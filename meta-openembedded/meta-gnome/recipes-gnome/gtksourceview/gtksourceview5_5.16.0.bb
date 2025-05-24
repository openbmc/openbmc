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

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"


inherit gnomebase lib_package gettext features_check gi-docgen gtk-icon-cache gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/5.16/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "ab35d420102f3e8b055dd3b8642d3a48209f888189e6254d0ffb4b6a7e8c3566"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_OPTION = "documentation"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)}"
PACKAGECONFIG[vulkan] = ",,vulkan-loader vulkan-headers"

FILES:${PN} += "${datadir}/gtksourceview-5"
