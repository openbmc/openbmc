SUMMARY = "The libdazzle library is a companion library to GObject and Gtk+."
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even vala distro_features_check gobject-introspection

DEPENDS = "glib-2.0-native glib-2.0 gtk+3"

SRC_URI[archive.md5sum] = "5f6455ebc47e86f63b9579997137f391"
SRC_URI[archive.sha256sum] = "238da19fdcc3ae9bb0c2d781d099fb8c6ec70c4dd3dffad80d230344ecc3f972"

GIR_MESON_OPTION = 'with_introspection'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
