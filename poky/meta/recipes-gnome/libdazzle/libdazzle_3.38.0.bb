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

SRC_URI[archive.sha256sum] = "e18af28217943bcec106585298a91ec3da48aa3ad62fd0992f23f0c70cd1678f"

GIR_MESON_OPTION = 'with_introspection'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
