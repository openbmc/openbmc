SUMMARY = "GNOME keyboard library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

SECTION = "x11/gnome/libs"

DEPENDS = "gconf glib-2.0 libxklavier gtk+3 intltool-native"

inherit gnome gobject-introspection gettext

GNOME_COMPRESS_TYPE = "xz"

SRC_URI[archive.md5sum] = "7b1ebf99f4254c99922163c262c7ff04"
SRC_URI[archive.sha256sum] = "340b30dabfebbd4e0e6c0fe34a378966dd5640b5d44595ab8a19b0be255d77df"

EXTRA_OECONF_remove = "--disable-schemas-install"

