SUMMARY = "A thin layer of graphic data types"
HOMEPAGE = "http://ebassi.github.io/graphene/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a7d871d9e23c450c421a85bb2819f648"

#DEPENDS = "gtk+3 iso-codes enchant2"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI[archive.md5sum] = "390139e704772b915ff2b7cac56c24ae"
SRC_URI[archive.sha256sum] = "80ae57723e4608e6875626a88aaa6f56dd25df75024bd16e9d77e718c3560b25"

PACKAGECONFIG[introspection] = "-Dintrospection=enabled,-Dintrospection=disabled,"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dinstalled_tests=false"

FILES:${PN} += "${libdir}/graphene-1.0"
