SUMMARY = "libportal provides GIO-style async APIs for most Flatpak portals."
DESCRIPTION = "It provides simple asynchronous wrappers for most Flatpak portals \
with a familiar GObject API along side the D-Bus API"
HOMEPAGE = "https://github.com/flatpak/libportal"
BUGTRACKER = "https://github.com/flatpak/libportal/issues"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI = "git://github.com/flatpak/${BPN}.git;protocol=https;branch=main"
SRCREV = "e9ed3a50cdde321eaf42361212480a66eb94a57a"
S = "${WORKDIR}/git"

inherit meson gi-docgen gobject-introspection vala features_check pkgconfig
GIDOCGEN_MESON_OPTION = 'docs'

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "glib-2.0 glib-2.0-native gtk+3 ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gtk4', '', d)}"

EXTRA_OEMESON = "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)} -Dbackend-qt5=disabled"
