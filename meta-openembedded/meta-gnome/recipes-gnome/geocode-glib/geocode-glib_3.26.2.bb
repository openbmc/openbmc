SUMMARY = "A convenience library for the geocoding"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_OPTION = "enable-introspection"
GTKDOC_MESON_OPTION = "enable-gtk-doc"

inherit gnomebase gobject-introspection gettext gtk-doc upstream-version-is-even

DEPENDS = " \
    json-glib \
    libsoup-2.4 \
"

SRC_URI[archive.md5sum] = "e1ef140a11a543643d170dc701009e39"
SRC_URI[archive.sha256sum] = "01fe84cfa0be50c6e401147a2bc5e2f1574326e2293b55c69879be3e82030fd1"

EXTRA_OEMESON = "-Denable-installed-tests=false"
