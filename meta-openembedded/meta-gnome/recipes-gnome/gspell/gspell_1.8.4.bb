SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 iso-codes enchant2"

inherit gnomebase gettext gobject-introspection

SRC_URI[archive.sha256sum] = "cf4d16a716e813449bd631405dc1001ea89537b8cdae2b8abfb3999212bd43b4"
