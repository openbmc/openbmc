SUMMARY = "A thin layer of graphic data types"
HOMEPAGE = "http://ebassi.github.io/graphene/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a7d871d9e23c450c421a85bb2819f648"

#DEPENDS = "gtk+3 iso-codes enchant2"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI[archive.md5sum] = "a2c26c4f44a02ca053e0e9afb63cc94c"
SRC_URI[archive.sha256sum] = "e97de8208f1aac4f913d4fa71ab73a7034e807186feb2abe55876e51c425a7f6"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dinstalled_tests=false"

FILES_${PN} += "${libdir}/graphene-1.0"
