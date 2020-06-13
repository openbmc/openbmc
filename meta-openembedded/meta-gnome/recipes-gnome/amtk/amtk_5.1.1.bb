SUMMARY = "Actions, Menus and Toolbars Kit"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
"

inherit gnomebase gettext features_check gobject-introspection

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.md5sum] = "f11c2e0cde10d5224a942b1e13d598b2"
SRC_URI[archive.sha256sum] = "f9656bdd4278aabfb8554f029049b9ffaf51b53faa463483a840365463355df1"
