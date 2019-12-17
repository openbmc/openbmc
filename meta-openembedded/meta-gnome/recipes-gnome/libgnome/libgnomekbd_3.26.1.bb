SUMMARY = "GNOME keyboard library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

SECTION = "x11/gnome/libs"

DEPENDS = "gconf glib-2.0 libxklavier gtk+3 intltool-native"

inherit features_check gnomebase gobject-introspection gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "98040022484406e7ebe25f82cef93344"
SRC_URI[archive.sha256sum] = "f7ca02631576e9b88aee1b1bae37ac1488b80ee7975f20a97f29e761a7172679"

EXTRA_OECONF_remove = "--disable-schemas-install"

