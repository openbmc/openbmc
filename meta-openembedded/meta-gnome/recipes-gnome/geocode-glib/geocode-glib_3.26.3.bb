SUMMARY = "A convenience library for the geocoding"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_OPTION = "enable-introspection"
GTKDOC_MESON_OPTION = "enable-gtk-doc"

inherit gnomebase gobject-introspection gettext gtk-doc upstream-version-is-even

DEPENDS = " \
    json-glib \
    libsoup-2.4 \
"

SRC_URI[archive.sha256sum] = "1dfeae83b90eccca1b6cf7dcf7c5e3b317828cf0b56205c4471ef0f911999766"

EXTRA_OEMESON = "-Denable-installed-tests=false"
