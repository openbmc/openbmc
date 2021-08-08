SUMMARY = "GObject wrapper for libudev"
DESCRIPTION = "This library makes it much simpler to use libudev from programs \
already using GObject. It also makes it possible to easily use libudev from \
other programming languages, such as Javascript, because of GObject \
introspection support."
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libgudev/issues"
SRC_URI[archive.sha256sum] = "e50369d06d594bae615eb7aeb787de304ebaad07a26d1043cef8e9c7ab7c9524"

SRC_URI:append = " file://0001-gudevenumtypes-make-deterministic.patch"

DEPENDS = "glib-2.0 udev"

RCONFLICTS:${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
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
