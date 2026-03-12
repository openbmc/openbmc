SUMMARY = "libmsgraph is a GLib-based library for accessing online serive APIs using MS Graph protocol."
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

inherit gnomebase gobject-introspection gi-docgen features_check
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "0731ece6b02b32eeffbbbd98efdc77bc03ddd20651eeae3a4343f0879b04d6c7"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dtests=false"

DEPENDS = " \
    glib-2.0\
    json-glib \
    rest \
    gnome-online-accounts \
"
