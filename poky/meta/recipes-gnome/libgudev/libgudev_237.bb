SUMMARY = "GObject wrapper for libudev"
DESCRIPTION = "This library makes it much simpler to use libudev from programs \
already using GObject. It also makes it possible to easily use libudev from \
other programming languages, such as Javascript, because of GObject \
introspection support."
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libgudev/issues"
SRC_URI[archive.sha256sum] = "0d06b21170d20c93e4f0534dbb9b0a8b4f1119ffb00b4031aaeb5b9148b686aa"

DEPENDS = "glib-2.0 udev"

RCONFLICTS:${PN} = "systemd (<= 220)"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gobject-introspection gtk-doc

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

GTKDOC_MESON_OPTION = "gtk_doc"

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"

# This isn't a GNOME-style version do gnome_verdir fails. Just return the
# version as that is how the directory is structured.
def gnome_verdir(v):
    return v
