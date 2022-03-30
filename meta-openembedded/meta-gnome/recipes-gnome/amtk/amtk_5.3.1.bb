SUMMARY = "Actions, Menus and Toolbars Kit"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
"

GNOMEBASEBUILDCLASS = "meson"

GIR_MESON_OPTION = ""

inherit gnomebase gettext features_check gobject-introspection

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "d5aa236c5d71dc41aa4674f345560a67a27f21c0efc97c9b3da09cb582b4638b"
