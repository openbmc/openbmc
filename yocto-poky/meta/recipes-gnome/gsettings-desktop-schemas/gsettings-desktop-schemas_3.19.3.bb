SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 intltool-native"

inherit gnomebase gsettings gettext gobject-introspection

SRC_URI[archive.md5sum] = "fdc92abcffe46821be423193b275cf8b"
SRC_URI[archive.sha256sum] = "9084989b75ca9b3fc5984c8a0d297a93d3d124f51cadd2bdaaaa75a783c80635"

