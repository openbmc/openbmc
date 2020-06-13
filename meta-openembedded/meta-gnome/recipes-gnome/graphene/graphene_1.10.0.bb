SUMMARY = "A thin layer of graphic data types"
HOMEPAGE = "http://ebassi.github.io/graphene/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a7d871d9e23c450c421a85bb2819f648"

#DEPENDS = "gtk+3 iso-codes enchant2"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI[archive.md5sum] = "07f72436bc7a85d12f5edd9fcedd0184"
SRC_URI[archive.sha256sum] = "406d97f51dd4ca61e91f84666a00c3e976d3e667cd248b76d92fdb35ce876499"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dinstalled_tests=false"

FILES_${PN} += "${libdir}/graphene-1.0"
