SUMMARY = "GNOME Screenshot"
DESCRIPTION = "GNOME Screenshot is a small utility that takes a screenshot \
of the whole desktop, the currently focused window, or an area of the screen."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SECTION = "x11/gnome"

inherit features_check gnomebase gettext upstream-version-is-even pkgconfig

SRC_URI += " file://0001-meson-remove-extraneous-positional-argument.patch"
SRC_URI[archive.sha256sum] = "4adb7dec926428f74263d5796673cf142e4720b6e768f5468a8d0933f98c9597"

DEPENDS += "glib-2.0 glib-2.0-native gtk+3 libhandy xext"

REQUIRED_DISTRO_FEATURES = "x11"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
