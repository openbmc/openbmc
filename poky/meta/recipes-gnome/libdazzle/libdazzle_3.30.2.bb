SUMMARY = "The libdazzle library is a companion library to GObject and Gtk+."
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even vala distro_features_check gobject-introspection

DEPENDS = "glib-2.0-native glib-2.0 gtk+3"

SRC_URI += " file://0001-Add-a-define-so-that-gir-compilation-succeeds.patch"
SRC_URI[archive.md5sum] = "24e2e1b914a34f5b8868a9507d1f3c4c"
SRC_URI[archive.sha256sum] = "78770eae9fa15ac5acb9c733d29459330b2540affbf72933119e36dbd90b36d5"

GIR_MESON_OPTION = 'with_introspection'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
