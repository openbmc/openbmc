SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase gsettings gettext gobject-introspection upstream-version-is-even

SRC_URI[archive.md5sum] = "796b6ac1eff450261edd521b72e7fe6d"
SRC_URI[archive.sha256sum] = "76a3fa309f9de6074d66848987214f0b128124ba7184c958c15ac78a8ac7eea7"
