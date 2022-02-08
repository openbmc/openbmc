SUMMARY = "libchamplain is a Gtk widget displaying zoomable and pannable maps"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
DEPENDS = "glib-2.0 gtk+3 gdk-pixbuf clutter-1.0 clutter-gtk-1.0 libsoup-2.4"

inherit meson gobject-introspection

SRCREV = "145e417f32e507b63c21ad4e915b808a6174099e"
SRC_URI = "git://github.com/gnome/libchamplain.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

