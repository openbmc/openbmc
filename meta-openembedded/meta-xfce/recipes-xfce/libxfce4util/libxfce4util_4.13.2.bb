SUMMARY = "Basic utility library for Xfce4"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection

SRC_URI[md5sum] = "e3e8b9dd7e12028d3e642345b85d6ef1"
SRC_URI[sha256sum] = "c58275ff650080369e742695862c811cb78402c85f243ea0b5aec186027be361"
