SUMMARY = "GNOME desktop-wide GSettings schemas"
DESCRIPTION = "GSettings desktop-wide schemas contains a collection of \
GSettings schemas for settings shared by various components of a desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas/issues"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"


inherit gnomebase gsettings gobject-introspection gettext

SRC_URI[archive.sha256sum] = "365c8d04daf79b38c8b3dc9626349a024f9e4befdd31fede74b42f7a9fbe0ae2"
