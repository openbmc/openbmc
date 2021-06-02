SUMMARY = "A GObject-based Exiv2 wrapper"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "exiv2"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI[archive.md5sum] = "4c0cd962f021f937507904df147ea750"
SRC_URI[archive.sha256sum] = "2322b552aca330eef79724a699c51a302345d5e074738578b398b7f2ff97944c"

EXTRA_OEMESON = " \
    -Dvapi=false \
    -Dpython2_girdir=no \
    -Dpython3_girdir=no \
"
