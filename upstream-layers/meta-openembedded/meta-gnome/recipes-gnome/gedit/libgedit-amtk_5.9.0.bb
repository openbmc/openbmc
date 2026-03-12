SUMMARY = "libgedit-amtk - Actions, Menus and Toolbars Kit for GTK applications"
SECTION = "gnome"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = "glib-2.0 gtk+3"

inherit gobject-introspection features_check gtk-doc gnomebase

SRC_URI[archive.sha256sum] = "7fc3348bef242e08967bdbb9a6698cf39f7810f95051fd8132910f36ed2d6d15"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"
