SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 iso-codes enchant2"

inherit gnomebase gettext gobject-introspection vala

SRC_URI[archive.sha256sum] = "dcbb769dfdde8e3c0a8ed3102ce7e661abbf7ddf85df08b29915e92cd723abdd"
