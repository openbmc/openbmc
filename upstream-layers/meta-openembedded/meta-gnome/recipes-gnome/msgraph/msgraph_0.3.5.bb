SUMMARY = "libmsgraph is a GLib-based library for accessing online serive APIs using MS Graph protocol."
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

inherit gnomebase gobject-introspection gi-docgen features_check
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "e3dadbf638d0207040f6da6d852db516e463423da710af6cbc35d00572f17d9b"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dtests=false"

DEPENDS = " \
    glib-2.0\
    json-glib \
    rest \
    gnome-online-accounts \
"
