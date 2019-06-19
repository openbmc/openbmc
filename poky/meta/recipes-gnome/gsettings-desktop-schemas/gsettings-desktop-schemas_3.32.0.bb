SUMMARY = "GNOME desktop-wide GSettings schemas"
HOMEPAGE = "http://live.gnome.org/gsettings-desktop-schemas"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext upstream-version-is-even

SRC_URI[archive.md5sum] = "0c2d468a482c12594757442c983aa8ea"
SRC_URI[archive.sha256sum] = "2d59b4b3a548859dfae46314ee4666787a00d5c82db382e97df7aa9d0e310a35"
SRC_URI += "file://0001-Do-not-skip-gir-installation-for-cross-compiling.patch"
