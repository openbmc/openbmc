SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase gsettings gettext gobject-introspection upstream-version-is-even

SRC_URI[archive.md5sum] = "83bb19d025f126fae495ab43a2f26f40"
SRC_URI[archive.sha256sum] = "f88ea6849ffe897c51cfeca5e45c3890010c82c58be2aee18b01349648e5502f"
