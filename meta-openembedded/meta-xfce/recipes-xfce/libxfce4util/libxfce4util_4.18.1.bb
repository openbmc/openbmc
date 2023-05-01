SUMMARY = "Basic utility library for Xfce4"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection vala

SRC_URI[sha256sum] = "8a52063a5adc66252238cad9ee6997909b59983ed21c77eb83c5e67829d1b01f"
