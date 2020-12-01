SUMMARY = "A GObject-based Exiv2 wrapper"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "exiv2"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI[archive.md5sum] = "44a3cfeab1afd83a71e852835d24e656"
SRC_URI[archive.sha256sum] = "8aeafd59653ea88f6b78cb03780ee9fd61a2f993070c5f0d0976bed93ac2bd77"

EXTRA_OEMESON = " \
    -Dvapi=false \
    -Dpython2_girdir=no \
    -Dpython3_girdir=no \
"
