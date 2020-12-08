SUMMARY = "GObject wrapper for libudev"
DESCRIPTION = "This library makes it much simpler to use libudev from programs \
already using GObject. It also makes it possible to easily use libudev from \
other programming languages, such as Javascript, because of GObject \
introspection support."
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libgudev/issues"
SRC_URI[archive.sha256sum] = "587c4970eb23f4e2deee2cb1fb7838c94a78c578f41ce12cac0a3f4a80dabb03"
SRC_URI[archive.md5sum] = "d59a317a40aaa02a2226056c0bb4d3e1"

DEPENDS = "glib-2.0 udev"

EXTRA_OECONF = "--disable-umockdev"

RCONFLICTS_${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection gtk-doc

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"
