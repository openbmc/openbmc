SUMMARY = "GNOME archive library"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.sha256sum] = "c0afbe333bcf3cb1441a1f574cc8ec7b1b8197779145d4edeee2896fdacfc3c2"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
