SUMMARY = "Gedit Technology - File loading and saving"
SECTION = "gnome"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = "glib-2.0 gtk+3"

inherit gobject-introspection features_check gtk-doc gnomebase

SRC_URI = "git://gitlab.gnome.org/World/gedit/libgedit-gfls.git;branch=main;protocol=https;tag=${PV}"
SRCREV = "0a0c8dd29ae2f9f4a99cbaaf1628d2352953e0bc"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"
