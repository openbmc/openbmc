SUMMARY = "Gedit Technology - Source code editing widget"
SECTION = "gnome"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "glib-2.0 gtk+3 libxml2"

inherit gobject-introspection features_check gtk-doc gnomebase

SRC_URI[archive.sha256sum] = "20c17ff89e2031aed5dc1107fe9a93fd50f92b569be2954b119c86f9e2fd85d6"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"

do_install:prepend() {
    sed -i -e 's|${B}||g' ${B}/gtksourceview/gtksource-enumtypes.c
    sed -i -e 's|${B}||g' ${B}/gtksourceview/gtksource-enumtypes.h
}

FILES:${PN} += "${datadir}"

# Most gnome projects have a verison like x.y.z, and they are stored in a folder called x.y
# Not this one. This has x.y.z version, but stored in folder called x.
# The original of this function is in gnomebase.bbclass.
def gnome_verdir(v):
    return v.split(".")[0]
