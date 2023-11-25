SUMMARY = "A convenience library for the geocoding"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

GIR_MESON_OPTION = "enable-introspection"
GTKDOC_MESON_OPTION = "enable-gtk-doc"

inherit gnomebase gobject-introspection gettext gtk-doc upstream-version-is-even

DEPENDS = " \
    json-glib \
    libsoup-3.0 \
"

SRC_URI[archive.sha256sum] = "2d9a6826d158470449a173871221596da0f83ebdcff98b90c7049089056a37aa"

EXTRA_OEMESON = "-Denable-installed-tests=false -Dsoup2=false"
