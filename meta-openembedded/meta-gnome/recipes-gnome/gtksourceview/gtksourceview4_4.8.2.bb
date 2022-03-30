SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 intltool-native gnome-common-native glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase lib_package gettext features_check gtk-doc gobject-introspection vala

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.gnome.org/sources/gtksourceview/4.8/${PNAME}-${PV}.tar.xz"
SRC_URI[sha256sum] = "842de7e5cb52000fd810e4be39cd9fe29ffa87477f15da85c18f7b82d45637cc"

GIR_MESON_OPTION = 'gir'
GTKDOC_MESON_OPTION = "gtk_doc"

# Override the definition in meson.bbclass.  The dependencies in mason.build are incomplete
# and the recipe will not build with "-j 1".  This fix is benign but should be reviewed when
# updating versions.
#
meson_do_compile() {
    bbnote "========== generating gtksourceview-gresources.h ========"
    bbnote "PARALLEL_MAKE is ${PARALLEL_MAKE}"
    ninja ${PARALLEL_MAKE} gtksourceview/gtksourceview-gresources.h
    bbnote "========== compiling target all ========"
    ninja ${PARALLEL_MAKE}
}

FILES:${PN} += "${datadir}/gtksourceview-4"
