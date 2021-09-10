DESCRIPTION = "libgee is a collection library providing GObject-based interfaces \
and classes for commonly used data structures."
HOMEPAGE = "http://live.gnome.org/Libgee"
SECTION = "libs"
DEPENDS = "glib-2.0"

BBCLASSEXTEND = "native"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

PE = "1"
inherit gnomebase vala gobject-introspection

do_configure:prepend() {
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f m4/$i
    done
}

SRC_URI[archive.md5sum] = "4d7d6f1f8054f1b3466c752ac2e50946"
SRC_URI[archive.sha256sum] = "524c1bf390f9cdda4fbd9a47b269980dc64ab5280f0801b53bc69d782c72de0e"
