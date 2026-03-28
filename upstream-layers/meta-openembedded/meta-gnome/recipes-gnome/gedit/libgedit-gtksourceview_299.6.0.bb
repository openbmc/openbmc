SUMMARY = "Gedit Technology - Source code editing widget"
SECTION = "gnome"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=310c7c93cf5181c6b0cc8229a1f3c6f6"

DEPENDS = "glib-2.0 gtk+3 libxml2"

inherit gobject-introspection features_check gtk-doc gnomebase

SRC_URI = "git://gitlab.gnome.org/World/gedit/libgedit-gtksourceview.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "a7bdc39f9fbc10c49ea7468ac1e5bf77385da0c0"

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
