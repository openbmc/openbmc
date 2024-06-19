SUMMARY = "Gedit Technology - Source code editing widget"
SECTION = "gnome"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "glib-2.0 gtk+3 libxml2"

inherit meson pkgconfig gobject-introspection features_check gtk-doc

SRC_URI = "git://github.com/gedit-technology/libgedit-gtksourceview.git;protocol=https;branch=main"
S = "${WORKDIR}/git"
SRCREV = "eaafc892d033713c7c823d8ad602061e456b3c88"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"

do_install:prepend() {
    sed -i -e 's|${B}||g' ${B}/gtksourceview/gtksource-enumtypes.c
    sed -i -e 's|${B}||g' ${B}/gtksourceview/gtksource-enumtypes.h
}

FILES:${PN} += "${datadir}"
