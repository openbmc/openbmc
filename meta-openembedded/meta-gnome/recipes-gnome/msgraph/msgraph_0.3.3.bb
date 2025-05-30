SUMMARY = "libmsgraph is a GLib-based library for accessing online serive APIs using MS Graph protocol."
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

inherit gnomebase gobject-introspection gi-docgen features_check
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "37d7e12f2a990490aea21184f0b27e0b915ebb4e5096f4d6632c62051c054012"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dtests=false"

DEPENDS = " \
    glib-2.0\
    json-glib \
    rest \
    gnome-online-accounts \
"
