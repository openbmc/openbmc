SUMMARY = "Small gobject library for playing system sounds"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=447b837ae57f08b7060593ac6256163f"

DEPENDS = " \
    glib-2.0 \
    libcanberra \
"


VALA_MESON_OPTION = "enable_vala"

inherit gnomebase gettext gobject-introspection vala

SRC_URI[archive.sha256sum] = "ca2d039e1ebd148647017a7f548862350bc9af01986d39f10cfdc8e95f07881a"
