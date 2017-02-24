SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase gsettings gettext gobject-introspection upstream-version-is-even

SRC_URI[archive.md5sum] = "c5d87ea480aa9bf66b134ddb5b8ea0f8"
SRC_URI[archive.sha256sum] = "55a41b533c0ab955e0a36a84d73829451c88b027d8d719955d8f695c35c6d9c1"
