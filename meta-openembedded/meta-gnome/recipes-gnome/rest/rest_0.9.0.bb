SUMMARY = "library to access web services that claim to be "RESTful""
HOMEPAGE = "https://wiki.gnome.org/Projects/Librest"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    gi-docgen \
    gi-docgen-native \
    glib-2.0 \
    glib-2.0-native \
    json-glib \
    libxml2-native \
"

inherit gnomebase gobject-introspection vala pkgconfig

PACKAGECONFIG_SOUP ?= "soup3"
PACKAGECONFIG ??= "${PACKAGECONFIG_SOUP}"

PACKAGECONFIG[soup2] = "-Dsoup2=true,,libsoup-2.4"
PACKAGECONFIG[soup3] = "-Dsoup2=false,,libsoup-3.0"

SRC_URI[archive.sha256sum] = "85b2bc9341128139539b53ee53f0533310bc96392fd645863a040410b81ebe66"
