SUMMARY = "GLib-based library for accessing online service APIs using the GData protocol"
HOMEPAGE = "http://live.gnome.org/libgdata"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24 \
                    file://gdata/gdata.h;endline=20;md5=079a554efcf65d46f96a515806e7e99a \
                    file://gdata/gdata-types.h;endline=20;md5=7399b111aac8718da13888fc634be6ef"

DEPENDS = "libxml2 glib-2.0 libsoup-2.4 intltool-native liboauth gcr json-glib"

GTKDOC_MESON_OPTION = "gtk_doc"
GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig gettext gtk-doc vala gobject-introspection manpages

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gdata/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[goa] = "-Dgoa=enabled,-Dgoa=disabled,gnome-online-accounts"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false"

EXTRA_OEMESON = "-Dalways_build_tests=false"

SRC_URI[archive.md5sum] = "d858d411ad394c275643b14993dfeed4"
SRC_URI[archive.sha256sum] = "c1d3d1f303f6a0416a0dac355a195276c31157a74bb1d040772d46d1cb15b012"
