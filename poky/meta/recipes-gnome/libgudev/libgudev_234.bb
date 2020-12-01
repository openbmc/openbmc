SUMMARY = "GObject wrapper for libudev"
DESCRIPTION = "This library makes it much simpler to use libudev from programs \
already using GObject. It also makes it possible to easily use libudev from \
other programming languages, such as Javascript, because of GObject \
introspection support."
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libgudev/issues"
SRC_URI[archive.sha256sum] = "1baeacacf0db42fa073ad5183d1decce9317857416a2b0f82ce3370d711a2e37"

DEPENDS = "glib-2.0 udev"

EXTRA_OECONF = "--disable-umockdev"

RCONFLICTS_${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection gtk-doc

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"
