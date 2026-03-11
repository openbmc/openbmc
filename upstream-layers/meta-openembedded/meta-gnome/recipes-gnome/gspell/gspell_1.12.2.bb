SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 icu enchant2"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gettext gobject-introspection vala gtk-doc

SRC_URI[archive.sha256sum] = "b4e993bd827e4ceb6a770b1b5e8950fce3be9c8b2b0cbeb22fdf992808dd2139"
