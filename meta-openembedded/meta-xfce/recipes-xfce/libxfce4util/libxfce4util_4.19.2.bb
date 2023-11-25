SUMMARY = "Basic utility library for Xfce4"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection vala

SRC_URI[sha256sum] = "d4c7eb021d1c9408190bcfb92c7ce26f51e994674ac3c3b8a119270c1e900ac4"
