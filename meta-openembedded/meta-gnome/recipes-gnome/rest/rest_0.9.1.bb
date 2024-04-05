SUMMARY = "library to access web services that claim to be "RESTful""
HOMEPAGE = "https://wiki.gnome.org/Projects/Librest"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"


DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    gtksourceview5 \
    json-glib \
    libadwaita \
    libsoup-3.0 \
    libxml2-native \
"

inherit gnomebase gobject-introspection vala pkgconfig gi-docgen features_check
REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ?= ""
PACKAGECONFIG[examples] = "-Dexamples=true,-Dexamples=false"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

SRC_URI[archive.sha256sum] = "9266a5c10ece383e193dfb7ffb07b509cc1f51521ab8dad76af96ed14212c2e3"
