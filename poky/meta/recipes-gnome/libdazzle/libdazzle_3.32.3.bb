SUMMARY = "The libdazzle library is a companion library to GObject and Gtk+."
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even vala distro_features_check gobject-introspection

DEPENDS = "glib-2.0-native glib-2.0 gtk+3"

SRC_URI[archive.md5sum] = "b6da085649dcda2795e6980a84667950"
SRC_URI[archive.sha256sum] = "6c8d9b1514b5f6422107596f4145b89b8f2a99abef6383e086dfcd28c28667e8"

GIR_MESON_OPTION = 'with_introspection'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
