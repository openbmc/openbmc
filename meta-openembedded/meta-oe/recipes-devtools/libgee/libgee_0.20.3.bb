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

do_configure_prepend() {
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f m4/$i
    done
}

SRC_URI[archive.md5sum] = "e574b3952b93d219b5ec7c74c5892c33"
SRC_URI[archive.sha256sum] = "d0b5edefc88cbca5f1709d19fa62aef490922c6577a14ac4e7b085507911a5de"
