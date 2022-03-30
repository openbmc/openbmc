SUMMARY = "GNOME archive library"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.sha256sum] = "7bdf0789553496abddc3c963b0ce7363805c0c02c025feddebcaacc787249e88"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
