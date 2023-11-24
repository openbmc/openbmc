SUMMARY = "libgedit-amtk - Actions, Menus and Toolbars Kit for GTK applications"
SECTION = "gnome"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = "glib-2.0 gtk+3"

inherit meson pkgconfig gobject-introspection features_check gtk-doc

SRC_URI = "git://github.com/gedit-technology/libgedit-amtk.git;protocol=https;branch=main"
S = "${WORKDIR}/git"
SRCREV = "f6fbfd1c57de3d97cab2056a5c3088b0da49e8a4"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"
