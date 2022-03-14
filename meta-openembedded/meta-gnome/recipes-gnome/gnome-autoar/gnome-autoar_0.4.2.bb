SUMMARY = "GNOME archive library"
LICENSE = "LGPLv2.1"
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

SRC_URI[archive.sha256sum] = "1d20694079300e12feabdb77304a969d1cc190d1d12a949be845cad01c78a5d3"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
