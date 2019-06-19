SUMMARY = "Basic utility library for Xfce4"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection

SRC_URI[md5sum] = "f39185afe5f612bd2c9b3dfbaf50b4d2"
SRC_URI[sha256sum] = "724b523a4a9ec8cada727950ab2173be30f256fa332a891ccd28b46f4b91b67e"
