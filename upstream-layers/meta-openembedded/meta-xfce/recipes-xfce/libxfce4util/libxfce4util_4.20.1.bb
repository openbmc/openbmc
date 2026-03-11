SUMMARY = "Basic utility library for Xfce4"
HOMEPAGE = "https://docs.xfce.org/xfce/libxfce4util/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection vala

SRC_URI[sha256sum] = "84bfc4daab9e466193540c3665eee42b2cf4d24e3f38fc3e8d1e0a2bebe3b8f1"
