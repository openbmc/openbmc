SUMMARY = "GLib-based library for accessing online service APIs using the GData protocol"
HOMEPAGE = "http://live.gnome.org/libgdata"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24 \
                    file://gdata/gdata.h;endline=20;md5=079a554efcf65d46f96a515806e7e99a \
                    file://gdata/gdata-types.h;endline=20;md5=7399b111aac8718da13888fc634be6ef"

DEPENDS = "libxml2 glib-2.0 libsoup-2.4 intltool-native liboauth gcr3 json-glib"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase pkgconfig gettext gtk-doc vala gobject-introspection manpages features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gdata/.libs"
}

# goa is required for gnome-photos
PACKAGECONFIG ??= "goa gtk vala"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[goa] = "-Dgoa=enabled,-Dgoa=disabled,gnome-online-accounts"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false"

EXTRA_OEMESON = "-Dalways_build_tests=false"

SRC_URI[archive.sha256sum] = "dd8592eeb6512ad0a8cf5c8be8c72e76f74bfe6b23e4dd93f0756ee0716804c7"
