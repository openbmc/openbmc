SUMMARY = "Actions, Menus and Toolbars Kit"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
"

inherit gnomebase gettext features_check gobject-introspection

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "820545bb4cf87ecebc2c3638d6b6e58b8dbd60a419a9b43cf020124e5dad7078"
