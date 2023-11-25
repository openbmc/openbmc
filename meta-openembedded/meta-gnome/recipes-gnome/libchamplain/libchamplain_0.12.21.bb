SUMMARY = "libchamplain is a Gtk widget displaying zoomable and pannable maps"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
DEPENDS = "glib-2.0 gtk+3 clutter-1.0 clutter-gtk-1.0 libsoup-3.0 cairo sqlite3"

inherit features_check gobject-introspection meson pkgconfig vala

REQUIRED_DISTRO_FEATURES = "opengl"

SRCREV = "941560af497148588783db991e8135f52a82574d"
SRC_URI = "git://github.com/gnome/libchamplain.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

