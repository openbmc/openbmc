SUMMARY = "GNOME archive library"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.md5sum] = "3c8da1a8489255f2aaa5fdb690308e5d"
SRC_URI[archive.sha256sum] = "a48c4d5ce9a9ed05ba8bc8fdeb9af2d1a7bd8091c2911d6c37223c4f488f7c72"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
