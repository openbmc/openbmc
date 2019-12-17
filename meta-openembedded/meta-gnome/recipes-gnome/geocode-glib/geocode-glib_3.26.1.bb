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

SRC_URI[archive.md5sum] = "21094494e66c86368add6a55bf480049"
SRC_URI[archive.sha256sum] = "5baa6ab76a76c9fc567e4c32c3af2cd1d1784934c255bc5a62c512e6af6bde1c"

EXTRA_OEMESON = "-Denable-installed-tests=false"
