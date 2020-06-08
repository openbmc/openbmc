SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 iso-codes enchant2"

inherit gnomebase gettext gobject-introspection

SRC_URI[archive.md5sum] = "d0892000d944e87fd74e8611ef400cdb"
SRC_URI[archive.sha256sum] = "5ae514dd0216be069176accf6d0049d6a01cfa6a50df4bc06be85f7080b62de8"
