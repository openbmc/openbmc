DESCRIPTION = "libgee is a collection library providing GObject-based interfaces \
and classes for commonly used data structures."
HOMEPAGE = "http://live.gnome.org/Libgee"
SECTION = "libs"
DEPENDS = "glib-2.0"

BBCLASSEXTEND = "native"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

PE = "1"
inherit gnomebase vala gobject-introspection

do_configure:prepend() {
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f m4/$i
    done
}

SRC_URI[archive.sha256sum] = "31863a8957d5a727f9067495cabf0a0889fa5d3d44626e54094331188d5c1518"
