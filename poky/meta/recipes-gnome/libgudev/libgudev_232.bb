SUMMARY = "GObject wrapper for libudev"
HOMEPAGE = "https://wiki.gnome.org/Projects/libgudev"
SRC_URI[archive.sha256sum] = "ee4cb2b9c573cdf354f6ed744f01b111d4b5bed3503ffa956cefff50489c7860"
SRC_URI[archive.md5sum] = "6914852377156665567abf8a38d89236"

DEPENDS = "glib-2.0 udev"

EXTRA_OECONF = "--disable-umockdev"

RCONFLICTS_${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection gtk-doc

UPSTREAM_CHECK_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgudev/"
UPSTREAM_CHECK_REGEX = "(?P<pver>(\d+))"
