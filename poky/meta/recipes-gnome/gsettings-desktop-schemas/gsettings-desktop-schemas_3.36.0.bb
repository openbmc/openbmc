SUMMARY = "GNOME desktop-wide GSettings schemas"
DESCRIPTION = "GSettings desktop-wide schemas contains a collection of \
GSettings schemas for settings shared by various components of a desktop."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gsettings-desktop-schemas/issues"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext upstream-version-is-even

SRC_URI[archive.md5sum] = "697db9e6b2ae058f8a0d0b023e32ceac"
SRC_URI[archive.sha256sum] = "764ab683286536324533a58d4e95fc57f81adaba7d880dd0ebbbced63e960ea6"
SRC_URI += "file://0001-Do-not-skip-gir-installation-for-cross-compiling.patch"
