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

inherit gnomebase pkgconfig gettext gtk-doc vala gobject-introspection manpages features_check

# gcr
REQUIRED_DISTRO_FEATURES = "x11"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gdata/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[goa] = "-Dgoa=enabled,-Dgoa=disabled,gnome-online-accounts"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false"

EXTRA_OEMESON = "-Dalways_build_tests=false"

SRC_URI[archive.md5sum] = "7b98e9059255d8a2fb147c4e727230a8"
SRC_URI[archive.sha256sum] = "6b9917122e4def6e6f78bcb660e49e146540832e296abcb5cde99829a04ea986"
