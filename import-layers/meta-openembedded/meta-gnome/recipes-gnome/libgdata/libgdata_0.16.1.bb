SUMMARY = "GLib-based library for accessing online service APIs using the GData protocol"
HOMEPAGE = "http://live.gnome.org/libgdata"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24 \
                    file://gdata/gdata.h;endline=20;md5=079a554efcf65d46f96a515806e7e99a \
                    file://gdata/gdata-types.h;endline=20;md5=7399b111aac8718da13888fc634be6ef"

DEPENDS = "gnome-common-native libxml2 glib-2.0 libsoup-2.4 intltool-native liboauth gcr json-glib"

inherit gnomebase pkgconfig autotools-brokensep gettext gtk-doc vala gobject-introspection

do_configure_prepend_class-target () {
    # introspection.m4 pre-packaged with upstream tarballs does not yet
    # have our fixes
    rm -f ${S}/introspection.m4
}

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gdata/.libs"
}

EXTRA_OECONF += "--disable-goa --disable-tests --disable-gtk-doc"

SRC_URI[archive.md5sum] = "eb552a8a8482e4231a3d1baf7262e64d"
SRC_URI[archive.sha256sum] = "8740e071ecb2ae0d2a4b9f180d2ae5fdf9dc4c41e7ff9dc7e057f62442800827"
