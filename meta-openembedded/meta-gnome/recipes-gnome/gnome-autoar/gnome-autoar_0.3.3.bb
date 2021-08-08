SUMMARY = "GNOME archive library"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.sha256sum] = "272400f73a375a7e88fdf1e12591bfb8f3f03edf01780cadcd74f70b613e5c04"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
