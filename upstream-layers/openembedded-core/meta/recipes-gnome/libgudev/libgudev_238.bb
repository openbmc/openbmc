SUMMARY = "GObject wrapper for libudev"
DESCRIPTION = "This library makes it much simpler to use libudev from programs \
already using GObject. It also makes it possible to easily use libudev from \
other programming languages, such as Javascript, because of GObject \
introspection support."
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libgudev/issues"
SRC_URI[archive.sha256sum] = "61266ab1afc9d73dbc60a8b2af73e99d2fdff47d99544d085760e4fa667b5dd1"

DEPENDS = "glib-2.0 glib-2.0-native udev"

RCONFLICTS:${PN} = "systemd (<= 220)"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection gtk-doc

SRC_URI += "file://0001-meson-Pass-export-dynamic-option-to-linker.patch"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

GTKDOC_MESON_OPTION = "gtk_doc"

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"

EXTRA_OEMESON += "-Dtests=disabled -Dvapi=disabled"

# This isn't a GNOME-style version do gnome_verdir fails. Just return the
# version as that is how the directory is structured.
def gnome_verdir(v):
    return v
