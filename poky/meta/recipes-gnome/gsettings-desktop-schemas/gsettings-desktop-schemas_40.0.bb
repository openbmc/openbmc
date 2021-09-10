SUMMARY = "GNOME desktop-wide GSettings schemas"
DESCRIPTION = "GSettings desktop-wide schemas contains a collection of \
GSettings schemas for settings shared by various components of a desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas/issues"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext

SRC_URI[archive.sha256sum] = "f1b83bf023c0261eacd0ed36066b76f4a520bbcb14bb69c402b7959257125685"
