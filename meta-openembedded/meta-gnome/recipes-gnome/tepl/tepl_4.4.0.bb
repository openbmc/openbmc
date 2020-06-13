SUMMARY = "Tepl library eases the development of GtkSourceView-based projects"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    gtksourceview4 \
    amtk \
    libxml2 \
    uchardet \
"

inherit gnomebase gobject-introspection gettext

SRC_URI[archive.md5sum] = "013ee8aae178f75cc74e05fac70786b3"
SRC_URI[archive.sha256sum] = "e6f6673a8a27e8f280725db8fbacec79b20676ae0558755239d15a9808faa256"
