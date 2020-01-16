SUMMARY = "GNOME archive library"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gtk+3 \
    libarchive \
"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.md5sum] = "36ab263f477eeee3c95c9381766eb3c2"
SRC_URI[archive.sha256sum] = "0a34c377f8841abbf4c29bc848b301fbd8e4e20c03d7318c777c58432033657a"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gnome-autoar/.libs"
}
