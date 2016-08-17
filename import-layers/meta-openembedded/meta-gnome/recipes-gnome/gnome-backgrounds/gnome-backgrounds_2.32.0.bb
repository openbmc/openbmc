SUMMARY = "Gnome background images"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

# glib-2.0 for glib-gettext.m4 which provides AM_GLIB_GNU_GETTEXT
# intltool-native for IT_PROG_INTLTOOL(0.35.0)
DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase

SRC_URI[archive.md5sum] = "3df26626483b02e51adefc6ab5945a8d"
SRC_URI[archive.sha256sum] = "4d7b60b5ba768bf8834b5fa3a3471cd9a9e14b5884bc210dc2d3cdbf1faddcef"
GNOME_COMPRESS_TYPE="bz2"

FILES_${PN} += "${datadir}/gnome-background-properties"

