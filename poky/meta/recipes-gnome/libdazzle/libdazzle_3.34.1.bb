SUMMARY = "The libdazzle library is a companion library to GObject and Gtk+."
DESCRIPTION = "A wide range of components from utilities for GIO, widgets for \
GTK+, an animation framework, state machines, paneling and high-performance \
counters are included."
LICENSE = "GPLv3+"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libdazzle"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libdazzle/issues"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even vala features_check gobject-introspection

DEPENDS = "glib-2.0-native glib-2.0 gtk+3"

SRC_URI[archive.md5sum] = "e796a92dd3f529616ed388c15208359b"
SRC_URI[archive.sha256sum] = "3d981cbb9d9bb87bfaff7bfd44d9847223b3ef81e69225e4d1f6ac725a669505"

GIR_MESON_OPTION = 'with_introspection'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
