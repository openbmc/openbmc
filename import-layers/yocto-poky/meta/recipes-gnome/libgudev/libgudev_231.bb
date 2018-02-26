SUMMARY = "GObject wrapper for libudev"
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
SRC_URI[archive.md5sum] = "916c10c51ec61131e244c3936bbb2e0c"
SRC_URI[archive.sha256sum] = "3b1ef99d4a8984c35044103d8ddfc3cc52c80035c36abab2bcc5e3532e063f96"

DEPENDS = "glib-2.0 udev"

EXTRA_OECONF = "--disable-umockdev"

RCONFLICTS_${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection gtk-doc

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"
