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

SRC_URI[archive.md5sum] = "708ddd8dec388ebda5539667604197c3"
SRC_URI[archive.sha256sum] = "004bdbe43cf8290f2de7d8537e14d8957610ca479a4fa368e34dbd03f03ec9d9"
SRC_URI += "file://0001-Do-not-skip-gir-installation-for-cross-compiling.patch"
