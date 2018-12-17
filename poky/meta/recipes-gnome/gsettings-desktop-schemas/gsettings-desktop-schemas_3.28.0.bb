SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase gsettings gettext gobject-introspection upstream-version-is-even

SRC_URI[archive.md5sum] = "370610e29b37d063ede3ef0f29c06eb9"
SRC_URI[archive.sha256sum] = "4cb4cd7790b77e5542ec75275237613ad22f3a1f2f41903a298cf6cc996a9167"
