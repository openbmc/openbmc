SUMMARY = "Library for embedding a Clutter canvas in a GTK+ application"
DESCRIPTION = "Clutter-GTK is a library providing facilities to integrate Clutter into GTK+ \
applications and vice versa. It provides a GTK+ widget, GtkClutterEmbed, for embedding the \
a Clutter stage into any GtkContainer; and GtkClutterActor, a Clutter \
actor for embedding any GtkWidget inside a Clutter stage."
HOMEPAGE = "http://www.clutter-project.org/"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/clutter/-/issues"
LICENSE = "LGPL-2.0-or-later"

CLUTTERBASEBUILDCLASS = "meson"
inherit clutter features_check upstream-version-is-even gobject-introspection gtk-doc

# depends on clutter-1.0 which depends on cogl-1.0
REQUIRED_DISTRO_FEATURES ?= "opengl"
# depends on gtk+3
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS = "clutter-1.0 gtk+3"
PACKAGES  =+ "${PN}-examples"

GTKDOC_MESON_OPTION = "enable_docs"

GIR_MESON_OPTION = 'enable_gi'
