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

SRC_URI[archive.sha256sum] = "77289972e596d044583f0c056306d8f1dbd8adcf912910a50da0a663e65332ed"
