SUMMARY = "GNOME archive library"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.md5sum] = "3149496d0189623a8e1289bbab4d8385"
SRC_URI[archive.sha256sum] = "0e78713e6f0de339fdee16bab82753ae290fe80fa7b4ba6e3db8c1465b81d0f8"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
