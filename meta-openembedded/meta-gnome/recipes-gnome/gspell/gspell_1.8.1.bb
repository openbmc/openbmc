SUMMARY = "spell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 iso-codes enchant2"

inherit gnomebase gettext gobject-introspection

SRC_URI[archive.md5sum] = "8269918ea5ff798e49943e7daf6a32b9"
SRC_URI[archive.sha256sum] = "819a1d23c7603000e73f5e738bdd284342e0cd345fb0c7650999c31ec741bbe5"
