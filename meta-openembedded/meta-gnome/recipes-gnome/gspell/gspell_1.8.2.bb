SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 iso-codes enchant2"

inherit gnomebase gettext gobject-introspection

SRC_URI[archive.md5sum] = "4f857382bc9d8d4afe1e67e5b5b9dbff"
SRC_URI[archive.sha256sum] = "bb9195c3a95bacf556d0203e9691f7489e0d3bc5ae1e5a440c89b2f2435d3ed6"
