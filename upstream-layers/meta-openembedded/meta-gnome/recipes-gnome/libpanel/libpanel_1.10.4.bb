# your responsibility to verify that the values are complete and correct.
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even gobject-introspection gtk-icon-cache

DEPENDS += " \
    glib-2.0 \
    gtk4 \
    libadwaita \
"

SRC_URI[archive.sha256sum] = "593888a7691f0af8aaa6e193c9e14afa86a810c0c2f27515c6d813f18733b1cd"

PACKAGECONFIG ?= ""
#EXTRA_OEMESON += "-Ddocs=disabled"
#GTKDOC_MESON_DISABLE_FLAG = "disabled"

EXTRA_OEMESON += "-Ddocs=disabled  -Dintrospection=enabled -Dvapi=false"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data opengl"
