SUMMARY = "libmsgraph is a GLib-based library for accessing online serive APIs using MS Graph protocol."
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

inherit gnomebase gobject-introspection gi-docgen

SRC_URI[archive.sha256sum] = "e0e59eaa8ae3e0a48ec0a6c2fed0470856a709248e9212b6a1d037de5792ecbb"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dtests=false"

DEPENDS = " \
    glib-2.0\
    json-glib \
    rest \
    gnome-online-accounts \
"
