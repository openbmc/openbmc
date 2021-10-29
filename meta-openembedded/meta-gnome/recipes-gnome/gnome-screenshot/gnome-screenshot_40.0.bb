SUMMARY = "GNOME Screenshot"
DESCRIPTION = "GNOME Screenshot is a small utility that takes a screenshot \
of the whole desktop, the currently focused window, or an area of the screen."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit features_check gnomebase gettext upstream-version-is-even pkgconfig

SRC_URI[archive.sha256sum] = "368ca95a39e39dc2406c849e8c4205e3f574acdd874c30741873455e3d21a5e2"

DEPENDS += "glib-2.0 glib-2.0-native gtk+3 libhandy xext"

REQUIRED_DISTRO_FEATURES = "x11"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
